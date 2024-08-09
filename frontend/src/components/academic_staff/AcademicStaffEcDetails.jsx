import { useState, useEffect } from 'react'
import axios from '../../api/axiosConfig'
import { useAuth } from '../../providers/AuthProvider'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { formatDate } from "../../utils"
import { Form, Card, Container, ListGroup, Button, Row, Col, Alert, Spinner, ButtonGroup, Badge } from 'react-bootstrap'
import { getEcApplication, updateEcApplication } from '../../api/ecApplications'
import { getUser } from '../../api/users'
import { getStudentInformationByStudentId } from '../../api/studentInformation'
import { getEvidenceByEcApplicationId } from '../../api/evidence'
import { getModuleRequestsByEcApplicationIds } from '../../api/moduleRequests'
import { getModulesByCodes } from '../../api/modules'
import { createModuleDecision, getModuleDecisionsByEcApplicationIds } from '../../api/moduleDecisions'
import { Navigate, useParams } from 'react-router'
import ErrorPage from '../ErrorPage'


export default function AcademicStaffEcDetails() {
    const { user } = useAuth()
    const { id } = useParams()
    const queryClient = useQueryClient()
    const [showFileAlert, setShowFileAlert] = useState(false)
    const [comments, setComments] = useState([''])


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

    // Get all module decisions related to the EC application
    const moduleDecisionsQuery = useQuery({
        queryKey: ["moduleDecisions", { ecApplicationIds: ecApplicationQuery.data?.id }],
        queryFn: () => getModuleDecisionsByEcApplicationIds(ecApplicationQuery.data?.id),
        enabled: !!ecApplicationQuery.data?.id
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

    useEffect(() => {
        const newCommentsArray = Array(moduleRequestsQuery.data?.length)
        setComments(newCommentsArray)
    }, [moduleRequestsQuery.data])


    if (ecApplicationQuery.isLoading || studentQuery.isLoading || studentInformationQuery.isLoading
        || evidenceQuery.isLoading || moduleRequestsQuery.isLoading || modulesQuery.isLoading
        || moduleDecisionsQuery.isLoading
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
                    | Server Response: ${ecApplicationQuery.error.response?.data.status}-${ecApplicationQuery.error.response?.data.error}`}
            />

    if (studentQuery.isError)
        if (studentQuery.error.response?.status == 401)
            // Token most likely expired or is invalid due to server restart
            return <Navigate to="/login" state={{ sessionExpired: true }} />
        else
            return <ErrorPage
                errorTitle={`when fetching students`}
                errorMessage={`${studentQuery.error.code}
                    | Server Response: ${studentQuery.error.response?.data.status}-${studentQuery.error.response?.data.error}`}
            />

    if (studentInformationQuery.isError)
        if (studentInformationQuery.error.response?.status == 401)
            // Token most likely expired or is invalid due to server restart
            return <Navigate to="/login" state={{ sessionExpired: true }} />
        else
            return <ErrorPage
                errorTitle={`when fetching student information`}
                errorMessage={`${studentInformationQuery.error.code}
                    | Server Response: ${studentInformationQuery.error.response?.data.status}-${studentInformationQuery.error.response?.data.error}`}
            />

    if (evidenceQuery.isError)
        if (evidenceQuery.error.response?.status == 401)
            // Token most likely expired or is invalid due to server restart
            return <Navigate to="/login" state={{ sessionExpired: true }} />
        else
            return <ErrorPage
                errorTitle={`when fetching evidence`}
                errorMessage={`${evidenceQuery.error.code}
                    | Server Response: ${evidenceQuery.error.response?.data.status}-${evidenceQuery.error.response?.data.error}`}
            />

    if (moduleRequestsQuery.isError)
        if (moduleRequestsQuery.error.response?.status == 401)
            // Token most likely expired or is invalid due to server restart
            return <Navigate to="/login" state={{ sessionExpired: true }} />
        else
            return <ErrorPage
                errorTitle={`when fetching module requests`}
                errorMessage={`${moduleRequestsQuery.error.code}
                    | Server Response: ${moduleRequestsQuery.error.response?.data.status}-${moduleRequestsQuery.error.response?.data.error}`}
            />

    if (modulesQuery.isError)
        if (modulesQuery.error.response?.status == 401)
            // Token most likely expired or is invalid due to server restart
            return <Navigate to="/login" state={{ sessionExpired: true }} />
        else
            return <ErrorPage
                errorTitle={`when fetching modules`}
                errorMessage={`${modulesQuery.error.code}
                    | Server Response: ${modulesQuery.error.response?.data.status}-${modulesQuery.error.response?.data.error}`}
            />

    if (createModuleDecisionMutation.isError)
        if (createModuleDecisionMutation.error.response?.status == 401)
            // Token most likely expired or is invalid due to server restart
            return <Navigate to="/login" state={{ sessionExpired: true }} />
        else
            return <ErrorPage
                errorTitle={`when creating module decision`}
                errorMessage={`${createModuleDecisionMutation.error.code}
                    | Server Response: ${createModuleDecisionMutation.error.response?.data.status}-${createModuleDecisionMutation.error.response?.data.error}`}
            />

    if (updateEcApplicationMutation.isError)
        if (updateEcApplicationMutation.error.response?.status == 401)
            // Token most likely expired or is invalid due to server restart
            return <Navigate to="/login" state={{ sessionExpired: true }} />
        else
            return <ErrorPage
                errorTitle={`when updating EC application`}
                errorMessage={`${updateEcApplicationMutation.error.code}
                    | Server Response: ${updateEcApplicationMutation.error.response?.data.status}-${updateEcApplicationMutation.error.response?.data.error}`}
            />

    const student = { ...studentQuery.data, ...studentInformationQuery.data[0] }
    const ecApplication = { ...ecApplicationQuery.data }
    const evidence = evidenceQuery.data
    const moduleRequests = moduleRequestsQuery.data
    const modules = modulesQuery.data
    const moduleDecisions = moduleDecisionsQuery.data

    // Staff is not allowed to view applications submitted by a student in another department
    if (student.departmentId != user.departmentId)
        return <ErrorPage
            errorMessage="You are not allowed to access this EC application.
                    It was submitted by a student in another department"
        />


    function downloadEvidence(fileName) {
        axios.get(`/evidence/${fileName}`, { responseType: 'blob' })
            .then(response => {
                // Create a URL for the blob object
                const blob = new Blob([response.data], { type: response.headers['content-type'] })
                const url = window.URL.createObjectURL(blob)

                // Create a temporary link element
                const link = document.createElement('a')
                link.href = url
                link.setAttribute('download', fileName); // Replace with the desired file name
                document.body.appendChild(link)

                // Trigger the download by simulating a click
                link.click()

                // Clean up and remove the link
                link.parentNode.removeChild(link)
                window.URL.revokeObjectURL(url)
            }).catch(error => {
                setShowFileAlert(true)
            })
    }

    // Request more evidence from the student
    function requestMoreEvidence() {
        updateEcApplicationMutation.mutate({
            id: ecApplication.id,
            requiresFurtherEvidence: true
        })
    }

    // Reject applicatiion
    function rejectApplication() {
        updateEcApplicationMutation.mutate({
            id: ecApplication.id,
            isReferred: false
        })
    }

    // Determine if a final decision has been made for a module request
    function getDecisionMade(requestId) {
        let approvals = 0;
        let rejections = 0;

        moduleDecisions.forEach(decision => {
            if (decision.moduleRequestId == requestId)
                if (decision.isApproved == true)
                    approvals++
                else if (decision.isApproved == false)
                    rejections++
        })

        // A decision (i.e. approve or reject) must be shared by 2 staff members for it to be final
        if (approvals >= 2) // request approved
            return true
        else if (rejections >= 2) // request rejected
            return false
        else                // no final decision made yet
            return null

    }

    // Determine if the staff member has decided before on a request
    function hasDecided(requestId) {
        let hasDecided = false
        moduleDecisions.forEach(decision => {
            if (decision.moduleRequestId == requestId && decision.staffMemberId == user.id) {
                hasDecided = true
                return
            }
        })
        return hasDecided
    }

    function handleCommentsChange(event, index) {
        const newComments = [...comments]
        newComments[index] = event.target.value
        setComments(newComments)
    }

    // Approve a module request and submit any comments
    function handleApprove(index, requestId) {
        createModuleDecisionMutation.mutate({
            comments: comments[index],
            isApproved: true,
            moduleRequestId: requestId,
            staffMemberId: user.id,
            ecApplicationId: ecApplication.id
        })
    }

    // Reject a module request and submit any comments
    function handleReject(index, requestId) {
        createModuleDecisionMutation.mutate({
            comments: comments[index],
            isApproved: false,
            moduleRequestId: requestId,
            staffMemberId: user.id,
            ecApplicationId: ecApplication.id
        })
    }

    return (
        <Container className='mt-3'>
            <Row>
                <Col md={{ offset: 1 }}>
                    <h4 className='mb-3 text-center'>Extenuating Circumstances Application #{ecApplication.id}</h4>
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
                            {student.hasLsp && <ListGroup.Item> Student is on a LSP program</ListGroup.Item>}
                            {student.hasHealthIssues && <ListGroup.Item>Student has health issues</ListGroup.Item>}
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
                            {!ecApplication.requiresFurtherEvidence ?
                                <Button variant='info' className='me-2' onClick={requestMoreEvidence}>Request More Evidence</Button>
                                : <Button variant='disabled' className='me-2 btn-outline-info' style={{ "pointerEvents": "none" }}>More evidence has been requested </Button>
                            }
                        </Card.Body>
                    </Card>

                    <Card className='mt-3 mb-3 w-75'>
                        <Card.Header as="h5">Module Requests</Card.Header>
                        <Card.Body>
                            <ListGroup>
                                {moduleRequests.map((moduleRequest, index) => {
                                    const finalDecision = getDecisionMade(moduleRequest.id)

                                    return (
                                        <ListGroup.Item key={moduleRequest.id}>
                                            <Card.Title>{moduleRequest.requestedOutcome}</Card.Title>
                                            <Card.Subtitle className="mb-2 text-muted">
                                                {moduleRequest.moduleCode} {modules.find(module => module.code === moduleRequest.moduleCode).name}
                                            </Card.Subtitle>
                                            {finalDecision == true &&
                                                <h5><Badge bg='success'>Approved</Badge> </h5>
                                            }
                                            {finalDecision == false &&
                                                <h5><Badge bg='danger'>Rejected</Badge> </h5>
                                            }
                                            {finalDecision == null && !hasDecided(moduleRequest.id) &&
                                                <>
                                                    <Form.Group className='mb-2'>
                                                        <Form.Text muted>
                                                            Please type any comments explaining your decision here
                                                        </Form.Text>
                                                        <Form.Control
                                                            as="textarea"
                                                            rows={3}
                                                            value={comments[index]}
                                                            onChange={(event) => handleCommentsChange(event, index)}
                                                        />
                                                    </Form.Group>
                                                    <ButtonGroup className='mb-1'>
                                                        <Button variant='success' onClick={() => handleApprove(index, moduleRequest.id)} >Approve</Button>
                                                        <Button variant='danger' onClick={() => handleReject(index, moduleRequest.id)}>Reject</Button>
                                                    </ButtonGroup>
                                                </>
                                            }
                                            {finalDecision == null && hasDecided(moduleRequest.id) &&
                                                <h5><Badge bg='warning'>Pending decisions from other academic staff members</Badge></h5>
                                            }
                                        </ListGroup.Item>
                                    )
                                })}
                            </ListGroup>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    )
}
