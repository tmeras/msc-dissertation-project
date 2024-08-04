import { useState, useEffect } from 'react'
import axios from '../../api/axiosConfig'
import { useAuth } from '../../providers/AuthProvider'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { bytesToMb, formatDate } from "../../utils"
import { Form, Card, Container, ListGroup, Button,  Row, Col, Alert, Spinner, ButtonGroup, Badge, Accordion} from 'react-bootstrap'
import { getEcApplication, updateEcApplication } from '../../api/ecApplications'
import { getUser } from '../../api/users'
import { getStudentInformationByStudentId } from '../../api/studentInformation'
import { createEvidence, getEvidenceByEcApplicationId } from '../../api/evidence'
import { getModuleRequestsByEcApplicationIds } from '../../api/moduleRequests'
import { getModulesByCodes } from '../../api/modules'
import { createModuleDecision, getModuleDecisionsByEcApplicationIds } from '../../api/moduleDecisions'
import { useParams } from 'react-router'
import ErrorPage from '../routing/ErrorPage'


export default function StudentEcDetails() {
    const {user} = useAuth()
    const {id} = useParams()
    const queryClient = useQueryClient()
    const [showFileDownloadAlert, setShowFileDownloadAlert] = useState(false)
    const [showFileUploadAlert, setShowFileUploadAlert] = useState(false)
    const [comments, setComments] = useState([''])
    const [files, setFiles] = useState([])


    // Get the details of the EC application
    const ecApplicationQuery = useQuery({
        queryKey: ["ecApplications", id],
        queryFn: () => getEcApplication(id)
    })

    // Get the details of the logged-in student who submitted the EC application
    const studentQuery = useQuery({
        queryKey: ["users", user.id],
        queryFn: () => getUser(user.id),
    })
    const studentInformationQuery = useQuery({
        queryKey: ["studentInformation", {studentId: user.id}],
        queryFn: () => getStudentInformationByStudentId(user.id),
    })

    // Fetch all evidence related to the EC application
    const evidenceQuery = useQuery({
        queryKey: ["evidence", {ecApplicationId: ecApplicationQuery.data?.id}],
        queryFn: () => getEvidenceByEcApplicationId(ecApplicationQuery.data?.id),
        enabled: !!ecApplicationQuery.data?.id
    })

    // Fetch all module requests related to the EC application
    const moduleRequestsQuery = useQuery({
        queryKey: ["moduleRequests", {ecApplicationIds: ecApplicationQuery.data?.id}],
        queryFn: () => getModuleRequestsByEcApplicationIds(ecApplicationQuery.data?.id),
        enabled: !!ecApplicationQuery.data?.id
    })

    // Fetch all modules for which requests have been made
    const moduleCodes = Array.from(new Set(moduleRequestsQuery.data?.map(moduleRequest => moduleRequest.moduleCode)))
    const modulesQuery = useQuery({
        queryKey: ["modules", {codes: moduleCodes}],
        queryFn: () => getModulesByCodes(moduleCodes),
        enabled: !(moduleCodes.length == 0)
    })

    // Get all module decisions related to the EC application
    const moduleDecisionsQuery = useQuery({
        queryKey: ["moduleDecisions", {ecApplicationIds: ecApplicationQuery.data?.id}],
        queryFn: () => getModuleDecisionsByEcApplicationIds(ecApplicationQuery.data?.id),
        enabled: !!ecApplicationQuery.data?.id
    })

    const createModuleDecisionMutation = useMutation({
        mutationFn: createModuleDecision,
        onSuccess: data => {
            queryClient.invalidateQueries(["moduleDecisions"])
        }
    })

    const createEvidenceMutation = useMutation({
        mutationFn: createEvidence,
        onSuccess: data => {
            queryClient.invalidateQueries(["evidence"])
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
    )
        return (
        <Container className='mt-3'>
            <Row>
            <Col md={{offset: 6 }}>
            <Spinner animation="border" />
            </Col>
            </Row>
        </Container>
        )

    if (ecApplicationQuery.isError)
        return <h1>Error fetching EC applications: {ecApplicationQuery.error.response?.status}</h1>

    if (studentQuery.isError)
        return <h1>Error fetching student information: {studentQuery.error.response?.status}</h1>

    if (studentInformationQuery.isError)
        return <h1>Error fetching student information: {studentInformationQuery.error.response?.status}</h1>

    if (evidenceQuery.isError)
        return <h1>Error fetching EC application evidence: {evidenceQuery.error.response?.status}</h1>
    
    if (moduleRequestsQuery.isError)
        return <h1>Error fetching module requests: {moduleRequestsQuery.error.response?.status}</h1>

    if (modulesQuery.isError)
        return <h1>Error fetching modules: {modulesQuery.error.response?.status}</h1>
    
    if (createModuleDecisionMutation.isError)
        return <h1>Error assigning EC application to academic staff: {createModuleDecisionMutation.error.response?.status}</h1>
    
    if (updateEcApplicationMutation.isError)
        return <h1>Error updating EC application: {updateEcApplicationMutation.error.response?.status}</h1>
    
    if (createEvidenceMutation.isError)
        return <h1>Error uploading evidence: {createEvidenceMutation.error.response?.status}</h1>


    const student = {...studentQuery.data, ...studentInformationQuery.data[0]}
    const ecApplication = {...ecApplicationQuery.data}
    const evidence = evidenceQuery.data
    const moduleRequests = moduleRequestsQuery.data
    const modules = modulesQuery.data
    const moduleDecisions = moduleDecisionsQuery.data

    // Student is only allowed to view their own EC applications
    if (user.id != ecApplication.studentId)
        return <ErrorPage 
                    errorMessage="You are not allowed to access this EC application.
                    It was submitted by another student"
                />


    function downloadEvidence(fileName) {
        axios.get(`/evidence/${fileName}`, {responseType: 'blob'})
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
            setShowFileDownloadAlert(true)
        })
    }

    function handleFileChange(event) {
        setFiles(event.target.files)
    }

    function handleFilesSubmit(event) {
        event.preventDefault()

        // Validate file sizes
        let fileValidationFailed = false
        Array.from(files).forEach(file => {
            if (bytesToMb(file.size) > 50 ){
                setShowFileUploadAlert(true)
                fileValidationFailed = true
                return
            }
        })
        if (fileValidationFailed)
            return

        // Upload the evidence
        Array.from(files).forEach((file, index) => {
            const fData = new FormData()
            fData.append('file', file)
            createEvidenceMutation.mutate({
                formData: fData,
                ecApplicationId: ecApplication.id
            })
        })

        // Additional evidence uploaded, update EC application
        updateEcApplicationMutation.mutate({
            id: ecApplication.id, 
            requiresFurtherEvidence: false
        })
        
        setFiles([])
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

    // Determine if the EC application is closed
    function isApplicationClosed() {
        let isClosed = true

        // The application is closed if a final decision has been made on each module request
        moduleRequests.forEach(request => {
            if (getDecisionMade(request.id) == null){
                isClosed = false
                return
            }
        })

        return isClosed
    }

    // Display the individual comments and decisions made by each staff member
    // for a module request
    function displayRequestCommentsAndDecisions(requestID, decisionMade) {

        // Get all the decisions related to this request
        let relevantDecisions = []
        moduleDecisions.forEach(decision => {
            if (decision.moduleRequestId == requestID)
                relevantDecisions.push(decision)
        })

        return (
            <Accordion className='mb-3' alwaysOpen>
                {relevantDecisions.map((decision, index) =>
                    <Accordion.Item key={index} eventKey={`${index}`}>
                        <Accordion.Header>
                            Staff Member #{index} 
                            {decision.isApproved ?
                                <Badge bg='success' className='ms-3'>Approved</Badge>
                            :
                                <Badge bg='danger' className='ms-3'>Rejected</Badge>
                            }
                        </Accordion.Header>
                        <Accordion.Body>
                            {(decision.comments && decision.comments.trim() != "")
                             ? decision.comments : "No comments were made by this staff member"}
                        </Accordion.Body>
                    </Accordion.Item>
                )}
            </Accordion>
        )
    }

    return (
        <Container className='mt-3'>
        <Row>
            <Col md={{offset: 1 }}>
            <h4 className='mb-3 text-center'>Extenuating Circumstances Application #{ecApplication.id}</h4>
            </Col>
        </Row>
        <Row>
            <Col md={{offset: 3 }}>
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
                    <Card.Header as="h5"> Application Details </Card.Header>
                    <Card.Body>
                    <Card.Title>Student Circumstances</Card.Title>
                    <Card.Subtitle className="mb-2 text-muted">
                        Affected Date: {formatDate(ecApplication.affectedDateStart)} - {formatDate(ecApplication.affectedDateEnd)}
                        </Card.Subtitle>
                    <Card.Text>
                        {ecApplication.circumstancesDetails}
                    </Card.Text>
                    {showFileDownloadAlert && 
                        <Alert variant="danger" onClose={() => setShowFileDownloadAlert(false)} style={{width: "25rem"}} dismissible>
                            There was an error when downloading the file
                        </Alert>
                    }
                    <ListGroup className='mb-3'>
                        {evidence.map((ev, index) => 
                            <ListGroup.Item style={{width: "10rem"}} key={ev.id}> 
                            <span className='fw-semibold'> Evidence #{index + 1} </span>
                            <Button variant="light" className=' mb-1' size='sm' onClick={() => downloadEvidence(ev.fileName)}>
                                <img src='/download.svg'/>
                            </Button>
                            </ListGroup.Item>
                        )}
                    </ListGroup>
                    {!isApplicationClosed() && 
                    <>
                        {ecApplication.requiresFurtherEvidence &&
                            <Alert variant="danger" style={{width: "25rem"}}>
                                A staff member has requested further evidence
                            </Alert>
                        }
                        {showFileUploadAlert && 
                            <Alert variant="danger" onClose={() => setShowFileUploadAlert(false)} style={{width: "25rem"}} dismissible>
                                One of the files is larger than 50MB
                            </Alert>
                        }
                        <Form onSubmit={handleFilesSubmit}>
                            <Form.Group className='mb-3' controlId='ecDetailsStudent.FileMutilple'>
                                <Form.Control type='file' multiple onChange={handleFileChange} required/>
                                <Form.Text muted>Please submit any additional evidence here (max 50MB per file)</Form.Text>
                            </Form.Group>
                            <Button variant='primary' type='submit'>Submit Evidence</Button>
                        </Form>
                    </>
                    }
                    </Card.Body>
                </Card>

                <Card className='mt-3 mb-3 w-75'> 
                    <Card.Header as="h5">Module Requests</Card.Header>
                    <Card.Body>
                    <ListGroup>
                        {ecApplication.isReferred == false &&
                            <p> 
                                The application was rejected by a clerical staff member. The circumstances were
                                likely not considered valid grounds for an extenuating circumstances application. 
                                Examples of valid grounds can be found <a target='_blank' href='https://students.sheffield.ac.uk/extenuating-circumstances/policy-procedure-23-24#examples-likely-to-be-accepted-as-extenuating-circumstances'>
                                here</a>.
                            </p>
                        }
                        {moduleRequests.map((moduleRequest, index) => {
                            const finalDecision = getDecisionMade(moduleRequest.id)

                            return (
                                <ListGroup.Item key={moduleRequest.id}>
                                    <Card.Title>ID#{moduleRequest.id} {moduleRequest.requestedOutcome}</Card.Title>
                                    <Card.Subtitle className="mb-2 text-muted">
                                        {moduleRequest.moduleCode} {modules.find(module => module.code === moduleRequest.moduleCode).name}
                                    </Card.Subtitle>
                                    {ecApplication.isReferred == null ?
                                        <h5><Badge bg='warning'>Under review by clerical staff</Badge></h5>
                                        :
                                        <>
                                            {finalDecision == null && ecApplication.isReferred != false &&
                                                <h5><Badge bg='warning'>Under review by academic staff</Badge></h5>
                                            }
                                            {(finalDecision == false  || ecApplication.isReferred == false) && 
                                                <h5><Badge bg='danger'>Rejected</Badge></h5>
                                            }
                                            {finalDecision == true && ecApplication.isReferred != false &&
                                                <h5><Badge bg='success'>Approved</Badge></h5>
                                            }
                                            {finalDecision != null && ecApplication.isReferred != false &&
                                            <>
                                                <h5 className='fw-normal mt-3'>Individual Staff Decisions</h5>
                                                {displayRequestCommentsAndDecisions(moduleRequest.id, finalDecision)}
                                            </>
                                            }
                                        </>
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
