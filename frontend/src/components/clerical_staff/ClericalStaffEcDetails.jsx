import React, { useState } from 'react'
import axios from '../../api/axiosConfig'
import { useAuth } from '../../providers/AuthProvider'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { formatDate } from "../../utils"
import { Card, Container, ListGroup, Button, Row, Col, Alert, Spinner, OverlayTrigger, Tooltip } from 'react-bootstrap'
import { getEcApplication, updateEcApplication } from '../../api/ecApplications'
import { emailUser, getUser, getUsersByDepartmentIdAndRoleId } from '../../api/users'
import { getStudentInformationByStudentId } from '../../api/studentInformation'
import { getEvidenceByEcApplicationId } from '../../api/evidence'
import { getModuleRequestsByEcApplicationIds } from '../../api/moduleRequests'
import { getModulesByCodes } from '../../api/modules'
import { getRoleByName } from '../../api/roles'
import { createModuleDecision } from '../../api/moduleDecisions'
import { Navigate, useParams } from 'react-router'
import ErrorPage from '../ErrorPage'


export default function ClericalStaffEcDetails() {
    const { user } = useAuth()
    const { id } = useParams()
    const [showFileAlert, setShowFileAlert] = useState(false)
    const queryClient = useQueryClient()

    // Get the details of the EC application
    const ecApplicationQuery = useQuery({
        queryKey: ["ecApplications", id],
        queryFn: () => getEcApplication(id)
    })

    // Get the details of the student who submitted the EC application
    const studentQuery = useQuery({
        queryKey: ["users", ecApplicationQuery.data?.studentId],
        queryFn: () => getUser(ecApplicationQuery.data?.studentId),
        enabled: !!ecApplicationQuery.data?.studentId
    })
    const studentInformationQuery = useQuery({
        queryKey: ["studentInformation", { studentId: ecApplicationQuery.data?.studentId }],
        queryFn: () => getStudentInformationByStudentId(ecApplicationQuery.data?.studentId),
        enabled: !!ecApplicationQuery.data?.studentId
    })

    // Fetch all evidence related to the EC application
    const evidenceQuery = useQuery({
        queryKey: ["evidence", { ecApplicationId: ecApplicationQuery.data?.id }],
        queryFn: () => getEvidenceByEcApplicationId(ecApplicationQuery.data?.id),
        enabled: !!ecApplicationQuery.data?.id
    })

    // Fetch all module requests related to the EC application
    const moduleRequestsQuery = useQuery({
        queryKey: ["moduleRequests", { ecApplicationIds: ecApplicationQuery.data?.id }],
        queryFn: () => getModuleRequestsByEcApplicationIds(ecApplicationQuery.data?.id),
        enabled: !!ecApplicationQuery.data?.id
    })

    // Fetch all modules for which requests have been made
    const moduleCodes = Array.from(new Set(moduleRequestsQuery.data?.map(moduleRequest => moduleRequest.moduleCode)))
    const modulesQuery = useQuery({
        queryKey: ["modules", { codes: moduleCodes }],
        queryFn: () => getModulesByCodes(moduleCodes),
        enabled: !(moduleCodes.length == 0)
    })

    // Fetch the id of the academic staff role
    const roleQuery = useQuery({
        queryKey: ["roles", { name: "Academic_Staff" }],
        queryFn: () => getRoleByName("Academic_Staff"),
    })

    // Fetch all academic staff members that are in the same department
    const staffQuery = useQuery({
        queryKey: ["users", { departmentId: user.departmentId, roleId: roleQuery.data?.[0]?.id }],
        queryFn: () => getUsersByDepartmentIdAndRoleId(user.departmentId, roleQuery.data?.[0]?.id),
        enabled: !!roleQuery.data?.[0]?.id
    })

    const createModuleDecisionMutation = useMutation({
        mutationFn: createModuleDecision,
        onSuccess: data => {
            queryClient.invalidateQueries(["moduleDecisions"])
        }
    })

    const updateEcApplicationMutation = useMutation({
        mutationFn: updateEcApplication,
        onSuccess: data => {
            queryClient.invalidateQueries(["ecApplications"])
        }
    })


    if (ecApplicationQuery.isLoading || studentQuery.isLoading || studentInformationQuery.isLoading
        || evidenceQuery.isLoading || moduleRequestsQuery.isLoading || modulesQuery.isLoading
        || roleQuery.isLoading || staffQuery.isLoading
    )
        return (
            <Container className='mt-3'>
                <Row>
                    <Col md={{ offset: 6 }}>
                        <Spinner animation="border" />
                    </Col>
                </Row>
            </Container>
        )

    if (ecApplicationQuery.isError)
        if (ecApplicationQuery.error.response?.status == 401)
            // Token most likely expired or is invalid due to server restart
            return <Navigate to="/login" state={{ sessionExpired: true }} />
        else
            return <ErrorPage
                errorTitle={`when fetching EC applications`}
                errorMessage={`${ecApplicationQuery.error.code}
                    | Status: ${ecApplicationQuery.error.response?.status}`}
            />

    if (studentQuery.isError)
        if (studentQuery.error.response?.status == 401)
            // Token most likely expired or is invalid due to server restart
            return <Navigate to="/login" state={{ sessionExpired: true }} />
        else
            return <ErrorPage
                errorTitle={`when fetching students`}
                errorMessage={`${studentQuery.error.code}
                    | Status: ${studentQuery.error.response?.status}`}
            />

    if (studentInformationQuery.isError)
        if (studentInformationQuery.error.response?.status == 401)
            // Token most likely expired or is invalid due to server restart
            return <Navigate to="/login" state={{ sessionExpired: true }} />
        else
            return <ErrorPage
                errorTitle={`when fetching student information`}
                errorMessage={`${studentInformationQuery.error.code}
                    | Status: ${studentInformationQuery.error.response?.status}`}
            />

    if (evidenceQuery.isError)
        if (evidenceQuery.error.response?.status == 401)
            // Token most likely expired or is invalid due to server restart
            return <Navigate to="/login" state={{ sessionExpired: true }} />
        else
            return <ErrorPage
                errorTitle={`when fetching evidence`}
                errorMessage={`${evidenceQuery.error.code}
                    | Status: ${evidenceQuery.error.response?.status}`}
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

    if (modulesQuery.isError)
        if (modulesQuery.error.response?.status == 401)
            // Token most likely expired or is invalid due to server restart
            return <Navigate to="/login" state={{ sessionExpired: true }} />
        else
            return <ErrorPage
                errorTitle={`when fetching modules`}
                errorMessage={`${modulesQuery.error.code}
                    | Status: ${modulesQuery.error.response?.status}`}
            />

    if (roleQuery.isError)
        if (roleQuery.error.response?.status == 401)
            // Token most likely expired or is invalid due to server restart
            return <Navigate to="/login" state={{ sessionExpired: true }} />
        else
            return <ErrorPage
                errorTitle={`when fetching role`}
                errorMessage={`${roleQuery.error.code}
                    | Status: ${roleQuery.error.response?.status}}`}
            />

    if (staffQuery.isError)
        if (staffQuery.error.response?.status == 401)
            // Token most likely expired or is invalid due to server restart
            return <Navigate to="/login" state={{ sessionExpired: true }} />
        else
            return <ErrorPage
                errorTitle={`when fetching clerical staff information`}
                errorMessage={`${staffQuery.error.code}
                    | Status: ${staffQuery.error.response?.status}`}
            />

    if (createModuleDecisionMutation.isError)
        if (createModuleDecisionMutation.error.response?.status == 401)
            // Token most likely expired or is invalid due to server restart
            return <Navigate to="/login" state={{ sessionExpired: true }} />
        else
            return <ErrorPage
                errorTitle={`when creating module decision`}
                errorMessage={`${createModuleDecisionMutation.error.code}
                    | Status: ${createModuleDecisionMutation.error.response?.status}`}
            />

    if (updateEcApplicationMutation.isError)
        if (updateEcApplicationMutation.error.response?.status == 401)
            // Token most likely expired or is invalid due to server restart
            return <Navigate to="/login" state={{ sessionExpired: true }} />
        else
            return <ErrorPage
                errorTitle={`when updating EC application`}
                errorMessage={`${updateEcApplicationMutation.error.code}
                    | Status: ${updateEcApplicationMutation.error.response?.status}`}
            />

    const student = { ...studentQuery.data, ...studentInformationQuery.data[0] }
    const ecApplication = { ...ecApplicationQuery.data }
    const evidence = evidenceQuery.data
    const moduleRequests = moduleRequestsQuery.data
    const modules = modulesQuery.data
    const academicStaff = staffQuery.data


    function downloadEvidence(fileName) {
        axios.get(`/evidence/${fileName}`, { responseType: 'blob' })
            .then(response => {
                // Create a URL for the blob object
                const blob = new Blob([response.data], { type: response.headers['content-type'] })
                const url = window.URL.createObjectURL(blob)

                // Create a temporary link element
                const link = document.createElement('a')
                link.href = url
                link.setAttribute('download', fileName) // Replace with the desired file name
                document.body.appendChild(link)

                // Trigger the download by simulating a click
                link.click()

                // Clean up and remove the link
                link.parentNode.removeChild(link)
                window.URL.revokeObjectURL(url)
            }).catch(error => {
                setShowFileAlert(true)
            });
    }

    // Request more evidence from the student
    function requestMoreEvidence() {

        // Inform student via email that further evidence is required
        emailUser({
            "id": ecApplication.studentId,
            "subject": `Further Evidence Requested`,
            "body": `A staff member has requested further evidence for one of your EC applications. Access the ECF portal to submit further evidence.`
        })

        updateEcApplicationMutation.mutate({
            id: ecApplication.id,
            requiresFurtherEvidence: true
        })
    }

    // Refer EC application to academic staff
    function referApplication() {
        updateEcApplicationMutation.mutate({
            id: ecApplication.id,
            isReferred: true
        })
    }

    // Reject applicatiion
    function rejectApplication() {
        // Inform student via email that a final decision has been made
        emailUser({
            "id": ecApplication.studentId,
            "subject": `EC Application Decision Available`,
            "body": `A final decision has been made for your EC application. Access the ECF portal to view it.`
        })


        updateEcApplicationMutation.mutate({
            id: ecApplication.id,
            isReferred: false
        })
    }

    return (
        <Container className='mt-3'>
            <Row>
                <Col md={{ offset: 1 }}>
                    <h4 className='mb-3 text-center '>Extenuating Circumstances Application #{ecApplication.id} Details</h4>
                </Col>
            </Row>
            <Row>
                <Col md={{ offset: 3 }}>
                    <Card className='w-75'>
                        <Card.Header as="h5">Student Information</Card.Header>
                        <Card.Body>
                            <Card.Title>Name: {student.name}</Card.Title>
                            <Card.Subtitle className="mb-2 text-muted">Student ID #{student.id}</Card.Subtitle>
                            {student.additionalDetails &&
                                <Card.Text>
                                    {student.additionalDetails}
                                </Card.Text>}
                        </Card.Body>
                        <ListGroup variant="flush">
                            {student.hasLsp && <ListGroup.Item> Student is on a LSP</ListGroup.Item>}
                            {student.hasHealthIssues && <ListGroup.Item>Student has chronic health issues</ListGroup.Item>}
                            {student.hasDisability && <ListGroup.Item>Student has a disability</ListGroup.Item>}
                        </ListGroup>
                    </Card>

                    <Card className='mt-3 w-75'>
                        <Card.Header as="h5">Application Details</Card.Header>
                        <Card.Body>
                            <Card.Title>Student Circumstances</Card.Title>
                            <Card.Subtitle className="mb-2 text-muted">
                                Affected Date: {formatDate(ecApplication.affectedDateStart)} - {formatDate(ecApplication.affectedDateEnd)}
                            </Card.Subtitle>
                            <Card.Text>
                                {ecApplication.circumstancesDetails}
                            </Card.Text>
                            {showFileAlert &&
                                <Alert variant="danger" onClose={() => setShowFileAlert(false)} style={{ width: "25rem" }} dismissible>
                                    There was an error when downloading the file
                                </Alert>
                            }
                            <ListGroup className='mb-2'>
                                {evidence.map((ev, index) =>
                                    <ListGroup.Item style={{ width: "10rem" }} key={ev.id}>
                                        <span className='fw-semibold'> Evidence #{index + 1} </span>
                                        <Button variant="light" className=' mb-1' size='sm' onClick={() => downloadEvidence(ev.fileName)}>
                                            <img src='/download.svg' />
                                        </Button>
                                    </ListGroup.Item>
                                )}
                            </ListGroup>
                            {!ecApplication.requiresFurtherEvidence ? <Button variant='info' className='me-2' onClick={requestMoreEvidence}>Request More Evidence</Button>
                                : <Button variant='disabled' className='me-2 btn-outline-info' style={{ "pointerEvents": "none" }}>More evidence has been requested </Button>}
                        </Card.Body>
                    </Card>

                    <Card className='mt-3 mb-3 w-75'>
                        <Card.Header as="h5">Module Requests</Card.Header>
                        <Card.Body>
                            <ListGroup>
                                {moduleRequests.map(moduleRequest =>
                                    <ListGroup.Item key={moduleRequest.id}>
                                        <Card.Title>{moduleRequest.requestedOutcome}</Card.Title>
                                        <Card.Subtitle className="mb-2 text-muted">
                                            {moduleRequest.moduleCode} {modules.find(module => module.code === moduleRequest.moduleCode).name}
                                        </Card.Subtitle>
                                    </ListGroup.Item>
                                )}
                            </ListGroup>
                        </Card.Body>
                    </Card>

                    {ecApplication.isReferred != null ?
                        <>
                            {ecApplication.isReferred == true && <Button variant='disabled' className='me-2 btn-outline-success' style={{ "pointerEvents": "none" }} disabled> Application has been referred to academic staff</Button>}
                            {ecApplication.isReferred == false && <Button variant='disabled' className='me-2 btn-outline-danger' style={{ "pointerEvents": "none" }} disabled> Application has been rejected</Button>}
                        </>
                        : <>
                            {academicStaff.length >= 3 ? <Button variant='primary' className='me-2' onClick={referApplication}> Refer to Academic Staff</Button>
                                : (
                                    <>
                                    <span className="d-inline-block" tabIndex="0" data-toggle="tooltip"
                                        title="Not enough academic staff in the department to review application (mininum 3 are required) - Please contact department">
                                        <Button variant='disabled' className='me-2' style={{ "pointerEvents": "none" }} disabled> Cannot refer to academic staff - At least 3 academic staff members are required</Button>
                                    </span>
                                      </>
                                )}
                            <Button variant='danger' className='me-2' onClick={rejectApplication}> Reject Application</Button>
                        </>
                    }
                </Col>
            </Row>
        </Container>
    )
}
