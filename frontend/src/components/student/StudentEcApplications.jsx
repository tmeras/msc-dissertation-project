import { formatDate } from "../../utils"
import Table from "react-bootstrap/Table"
import { Badge, Container, Spinner, Row, Col, ProgressBar, Button, ToastContainer, Toast } from 'react-bootstrap'
import { useQuery } from '@tanstack/react-query'
import { useAuth } from "../../providers/AuthProvider"
import { getEcApplicationsByStudentId } from '../../api/ecApplications'
import { getModuleRequestsByEcApplicationIds } from "../../api/moduleRequests"
import { getModuleDecisionsByEcApplicationIds } from "../../api/moduleDecisions"
import { Navigate, useLocation, useNavigate } from "react-router"
import { useEffect, useState } from "react"
import ErrorPage from "../ErrorPage"


export default function StudentEcApplications() {
    const { user } = useAuth()
    const navigate = useNavigate()
    const location = useLocation()
    const [showToast, setShowToast] = useState(false)


    // If redirected from EC application form page, show toast
    useEffect(() => {
        if (location.state?.applicationSubmitted) {
            setShowToast(true)
            window.history.replaceState({}, '')
        }
    }, [location.state])

    // Get all EC applications submitted by the student
    const ecApplicationsQuery = useQuery({
        queryKey: ["ecApplications", { studentId: user.id }],
        queryFn: () => getEcApplicationsByStudentId(user.id),
    })

    const ecApplicationIds = ecApplicationsQuery.data?.map(ecApplication => ecApplication.id)

    // Get all module decisions related to the fetched EC applications
    const moduleDecisionsQuery = useQuery({
        queryKey: ["moduleDecisions", { ecApplicationIds: ecApplicationIds }],
        queryFn: () => getModuleDecisionsByEcApplicationIds(ecApplicationIds),
        enabled: !!ecApplicationIds
    })

    // Fetch all module requests related to the fetched EC applications
    const moduleRequestsQuery = useQuery({
        queryKey: ["moduleRequests", { ecApplicationIds: ecApplicationIds }],
        queryFn: () => getModuleRequestsByEcApplicationIds(ecApplicationIds),
        enabled: !!ecApplicationIds
    })


    if (moduleDecisionsQuery.isLoading || ecApplicationsQuery.isLoading || moduleRequestsQuery.isLoading)
        return (
            <Container className='mt-3'>
                <Row>
                    <Col md={{ offset: 6 }}>
                        <Spinner animation="border" />
                    </Col>
                </Row>
            </Container>
        )

    if (ecApplicationsQuery.isError)
        if (ecApplicationsQuery.error.response?.status == 401)
            // Token most likely expired or is invalid due to server restart
            return <Navigate to="/login" state={{ sessionExpired: true }} />
        else
            return <ErrorPage
                errorTitle={`when fetching EC applications`}
                errorMessage={`${ecApplicationsQuery.error.code}
                        | Status: ${ecApplicationsQuery.error.response?.status}`}
            />

    if (moduleDecisionsQuery.isError)
        if (moduleDecisionsQuery.error.response?.status == 401)
            // Token most likely expired or is invalid due to server restart
            return <Navigate to="/login" state={{ sessionExpired: true }} />
        else
            return <ErrorPage
                errorTitle={`when fetching module decisions`}
                errorMessage={`${moduleDecisionsQuery.error.code}
                        | Status: ${moduleDecisionsQuery.error.response?.status}`}
            />

    if (moduleRequestsQuery.isError)
        if (moduleRequestsQuery.error.response?.status == 401)
            // Token most likely expired or is invalid due to server restart
            return <Navigate to="/login" state={{ sessionExpired: true }} />
        else
            return <ErrorPage
                errorTitle={`when fetching module requests`}
                errorMessage={`${moduleRequestsQuery.error.code}
                        | Status: ${moduleRequestsQuery.error.response?.status}`}
            />

    const ecApplications = ecApplicationsQuery.data
    const moduleRequests = moduleRequestsQuery.data
    const moduleDecisions = moduleDecisionsQuery.data


    // Calculate the progress of the EC application
    function calculateProgress(ecApplicationId) {
        // Find module requests that were made as part of this application
        let applicationModuleRequests = []

        moduleRequests.forEach(request => {
            if (request.ecApplicationId == ecApplicationId)
                applicationModuleRequests.push(request)
        })

        // A final decision needs to be made for each module request
        let finalDecisionsRequired = applicationModuleRequests.length
        let finalDecisionsMade = 0

        applicationModuleRequests.forEach(request => {
            let approvals = 0;
            let rejections = 0;

            moduleDecisions.forEach(decision => {
                if (decision.moduleRequestId == request.id)
                    if (decision.isApproved == true)
                        approvals++
                    else if (decision.isApproved == false)
                        rejections++
            })

            // A decision (i.e. approve or reject) must be shared by 2 staff members for it to be final
            if (approvals >= 2 || rejections >= 2)
                finalDecisionsMade++
        })


        return { finalDecisionsRequired, finalDecisionsMade }
    }

    return (
        <>
            <ToastContainer className="p-5" position={"top-start"}>
                <Toast bg="success" onClose={() => setShowToast(false)} show={showToast} delay={5000} autohide>
                    <Toast.Header closeButton={false}>
                        <img
                            src="/sheffield-favicon.png"
                            style={{ width: '3rem', height: '3rem' }}
                        />
                        <strong>ECF Portal</strong>
                    </Toast.Header>
                    <Toast.Body>ECF application submitted successfully. </Toast.Body>
                </Toast>
            </ToastContainer>

            <Container className="mt-3">
                <h2 className="text-center mb-3 fw-normal">Extenuating Circumstances Applications</h2>

                <div className="d-flex justify-content-end">
                    <Button variant='primary' className='ms-auto' onClick={() => navigate("/student/ec-form")}>
                        <img src='/plus.svg' className='mb-1 me-1' />
                        Submit New Application
                    </Button>
                </div>

                <Table striped hover className="mt-3 shadow">
                    <thead className="table-light">
                        <tr>
                            <th scope="col" className="col-1">#</th>
                            <th scope="col" className="col-2"> Submitted On</th>
                            <th scope="col" className="col-3">Progress</th>
                            <th scope="col" className="col-4">Status</th>
                        </tr>
                    </thead>
                    <tbody className="table-group-divider">
                        {ecApplications.sort((a, b) => a.id - b.id).map(ecApplication => {
                            let { finalDecisionsRequired, finalDecisionsMade } = calculateProgress(ecApplication.id)
                            let percentage = ecApplication.isReferred == false ? 100 :
                                    parseInt(parseFloat(finalDecisionsMade) / finalDecisionsRequired * 100)

                            return (
                                <tr
                                    key={ecApplication.id}
                                    onClick={() => navigate(`/student/ec-applications/${ecApplication.id}`)}
                                    style={{ cursor: "pointer" }}
                                >
                                    <td>{ecApplication.id}</td>
                                    <td >{formatDate(ecApplication.submittedOn)}</td>
                                    <td> <ProgressBar className="mt-1" now={percentage} label={`${percentage}%`} variant={`${percentage == 100 && "success"}`} /> </td>
                                    <td>
                                        {percentage == 100 || ecApplication.isReferred == false ? (
                                            <Badge bg='success' className="me-1">Application results available</Badge>
                                        ) : (
                                            <>
                                                {ecApplication.isReferred == true ?
                                                    <Badge bg="primary" className="me-1">Under review by academic staff</Badge>
                                                    :
                                                    <Badge bg="primary" className="me-1">Under review by clerical staff</Badge>
                                                }
                                                {ecApplication.requiresFurtherEvidence &&
                                                    <Badge bg='danger' className="me-1">Further evidence requested</Badge>
                                                }
                                            </>
                                        )}
                                    </td>
                                </tr>
                            )
                        })}
                    </tbody>
                </Table>
            </Container>
        </>
    )
}

