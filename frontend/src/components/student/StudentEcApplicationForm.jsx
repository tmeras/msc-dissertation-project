import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useAuth } from '../../providers/AuthProvider'
import React, { useState } from 'react'
import { Form, Container, Row, Col, Spinner, Button, Alert } from 'react-bootstrap'
import { getModules } from '../../api/modules'
import { bytesToMb, getCurrentDate } from '../../utils'
import { createEcApplication } from '../../api/ecApplications'
import { createEvidence } from '../../api/evidence'
import { createModuleRequest } from '../../api/moduleRequests'
import { Navigate, useNavigate } from 'react-router'
import ErrorPage from "../ErrorPage"


export default function StudentEcApplicationForm() {
    const { user } = useAuth()
    const queryClient = useQueryClient()
    const navigate = useNavigate()
    const [showCircumstancesAlert, setShowCircumstancesAlert] = useState(false)
    const [showDateAlert, setShowDateAlert] = useState(false)
    const [showFileAlert, setShowFileAlert] = useState(false)


    // State for module requests made by the student
    const [moduleRequests, setModuleRequests] = useState(([{
        requestedOutcome: '',
        moduleCode: ''
    }]))

    // State for the rest of the form fields
    const [formData, setFormData] = useState({
        circumstancesDetails: '',
        startDate: '',
        endDate: '',
        files: []
    })


    // Get the details of all modules
    const modulesQuery = useQuery({
        queryKey: ["modules"],
        queryFn: () => getModules()
    })

    const createEcApplicationMutation = useMutation({
        mutationFn: createEcApplication,
        onSuccess: data => {
            queryClient.invalidateQueries(["ecApplication"])
        },
    })

    const createEvidenceMutation = useMutation({
        mutationFn: createEvidence,
        onSuccess: data => {
            queryClient.invalidateQueries(["evidence"])
        },
    })

    const createModuleRequestMutation = useMutation({
        mutationFn: createModuleRequest,
        onSuccess: data => {
            queryClient.invalidateQueries(["moduleRequests"])
        }
    })

    if (modulesQuery.isLoading)
        return (
            <Container className='mt-3'>
                <Row>
                    <Col md={{ offset: 6 }}>
                        <Spinner animation="border" />
                    </Col>
                </Row>
            </Container>
        )

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

    if (createEcApplicationMutation.isError)
        if (createEcApplicationMutation.error.response?.status == 401)
            // Token most likely expired or is invalid due to server restart
            return <Navigate to="/login" state={{ sessionExpired: true }} />
        else
            return <ErrorPage
                errorTitle={`when creating EC application`}
                errorMessage={`${createEcApplicationMutation.error.code}
                    | Server Response: ${createEcApplicationMutation.error.response?.data.status}-${createEcApplicationMutation.error.response?.data.error}`}
            />

    if (createEvidenceMutation.isError)
        if (createEvidenceMutation.error.response?.status == 401)
            // Token most likely expired or is invalid due to server restart
            return <Navigate to="/login" state={{ sessionExpired: true }} />
        else
            return <ErrorPage
                errorTitle={`when uploading evidence`}
                errorMessage={`${createEvidenceMutation.error.code}
                        | Server Response: ${createEvidenceMutation.error.response?.data.status}-${createEvidenceMutation.error.response?.data.error}`}
            />

    if (createModuleRequestMutation.isError)
        if (createModuleRequestMutation.error.response?.status == 401)
            // Token most likely expired or is invalid due to server restart
            return <Navigate to="/login" state={{ sessionExpired: true }} />
        else
            return <ErrorPage
                errorTitle={`when creating module request`}
                errorMessage={`${createModuleRequestMutation.error.code}
                        | Server Response: ${createModuleRequestMutation.error.response?.data.status}-${createModuleRequestMutation.error.response?.data.error}`}
            />

    const modules = modulesQuery.data


    function addModuleRequest() {
        setModuleRequests(prevRequests => [...prevRequests, {
            requestedOutcome: '',
            moduleCode: ''
        }])
    }

    function removeModuleRequest() {
        setModuleRequests(prevRequests => {
            const prevRequestsCopy = [...prevRequests]
            prevRequestsCopy.pop()
            return prevRequestsCopy
        }
        )
    }

    function handleOutcomeRequestChange(index, event) {
        const newModuleRequests = [...moduleRequests];
        newModuleRequests[index].requestedOutcome = event.target.value;
        setModuleRequests(newModuleRequests)
    }

    function handleModuleChange(index, event) {
        const newModuleRequests = [...moduleRequests];
        newModuleRequests[index].moduleCode = event.target.value;
        setModuleRequests(newModuleRequests)
    }

    function handleChange(event) {
        const { name, value } = event.target;
        setFormData(prevFormData => ({
            ...prevFormData,
            [name]: value
        }))
    }

    function handleFileChange(event) {
        setFormData(prevFormData => ({
            ...prevFormData,
            files: event.target.files
        }))
    }

    function handleSubmit(event) {
        event.preventDefault()

        // Validate circumstances details
        if (formData.circumstancesDetails.trim() === "") {
            setShowCircumstancesAlert(true)
            return
        }
        setShowCircumstancesAlert(false)

        // Validate start and end dates
        const startDate = new Date(formData.startDate);
        const endDate = new Date(formData.endDate);
        if (startDate > endDate) {
            setShowDateAlert(true)
            return;
        }
        setShowDateAlert(false)

        // Validate file sizes
        let fileValidationFailed = false
        Array.from(formData.files).forEach(file => {
            if (bytesToMb(file.size) > 50) {
                fileValidationFailed = true
                setShowFileAlert(true)
                return
            }
        })
        if (fileValidationFailed)
            return

        // Create the EC application
        createEcApplicationMutation.mutate({
            circumstancesDetails: formData.circumstancesDetails,
            affectedDateStart: formData.startDate,
            affectedDateEnd: formData.endDate,
            submittedOn: getCurrentDate(),
            studentId: user.id
        }, {

            onSuccess: data => {
                // Upload the evidence
                Array.from(formData.files).forEach((file, index) => {
                    const fData = new FormData()
                    fData.append('file', file)
                    createEvidenceMutation.mutate({
                        formData: fData,
                        ecApplicationId: data.id
                    })
                })

                // Create the module requests
                moduleRequests.forEach(request => {
                    createModuleRequestMutation.mutate({
                        requestedOutcome: request.requestedOutcome,
                        moduleCode: request.moduleCode,
                        ecApplicationId: data.id
                    })
                })

                navigate("/student/ec-applications", { state: { applicationSubmitted: true } })
            }
        })
    }

    return (
        <Container className='mt-3'>
            <Row>
                <Col md={{ offset: 1 }}>
                    <h4 className='mb-3 text-center'>Extenuating Circumstances Application Form</h4>
                </Col>
            </Row>
            <Row>
                <Col md={{ offset: 3 }}>
                    <Form className='w-75' onSubmit={handleSubmit}>
                        <Form.Group className='mb-5' controlId='ecForm.ControlTextArea1'>
                            <Form.Label>Circumstances Details</Form.Label>
                            <Form.Control as="textarea"
                                rows={5}
                                name="circumstancesDetails"
                                value={formData.circumstancesDetails}
                                onChange={handleChange}
                                required
                            />
                            <Form.Text muted>
                                Please thoroughly explain your extenuating circumstances. Details on the university's policy regarding exteunating circumstances can be
                                found <a href='https://students.sheffield.ac.uk/extenuating-circumstances/policy-procedure-23-24#extenuating-circumstances-policy-and-procedure'
                                    target='_blank'>here. </a>
                            </Form.Text>
                            {showCircumstancesAlert &&
                                <Alert className=' mt-2' variant="danger" onClose={() => setShowCircumstancesAlert(false)} style={{ width: "20rem" }}>
                                    Please explain your circumstances!
                                </Alert>
                            }
                        </Form.Group>
                        <hr />

                        <Form.Group className='mb-5' controlId='ecForm.DateArea1'>
                            <Form.Label>Period Affected By Circumstances</Form.Label>
                            <Row>
                                <Col>
                                    <Form.Text muted>Start Date</Form.Text>
                                </Col>
                                <Col>
                                    <Form.Text muted>End Date</Form.Text>
                                </Col>
                            </Row>
                            <Row>
                                <Col>
                                    <input type='date'
                                        name='startDate'
                                        value={formData.startDate}
                                        onChange={handleChange}
                                        max={getCurrentDate()}
                                        required
                                    />
                                </Col>
                                <Col>
                                    <input
                                        type='date'
                                        name='endDate'
                                        value={formData.endDate}
                                        onChange={handleChange}
                                        max={getCurrentDate()}
                                        required
                                    />
                                </Col>
                            </Row>
                            {showDateAlert &&
                                <Alert className=' mt-2' variant="danger" onClose={() => setShowDateAlert(false)} style={{ width: "20rem" }}>
                                    Start date cannot be after end date
                                </Alert>
                            }
                        </Form.Group>
                        <hr className='mb-3' />

                        <Form.Group className='mb-5' controlId='ecForm.Select1'>
                            <Form.Label>Module Outcome Requests</Form.Label>
                            <Row>
                                <Col>
                                    <Form.Text muted>Module</Form.Text>
                                </Col>
                                <Col>
                                    <Form.Text muted>Request</Form.Text>
                                </Col>
                            </Row>
                            {moduleRequests.map((value, index) =>
                                <Row key={index} className='mb-1'>
                                    <Col>
                                        <Form.Select
                                            value={moduleRequests[index].moduleCode}
                                            onChange={(event) => handleModuleChange(index, event)}
                                            required
                                        >
                                            <option value="">Select a module</option>
                                            {modules.map(module =>
                                                <option key={module.code} value={module.code}>{module.code} {module.name}</option>
                                            )}
                                        </Form.Select>
                                    </Col>
                                    <Col>
                                        <Form.Select
                                            value={moduleRequests[index].requestedOutcome}
                                            onChange={(event) => handleOutcomeRequestChange(index, event)}
                                            required
                                        >
                                            <option value="">Select an option</option>
                                            <option value="Deadline Extension">Deadline Extension</option>
                                            <option value="Disregard Missing Component Mark">Disregard Missing Component Mark</option>
                                            <option value="Remove Lateness Penalties">Remove Lateness Penalties</option>
                                            <option value="Defer Formal Examination">Defer Formal Examination</option>
                                            <option value="Defer Formal Assessment">Defer Formal Assessment</option>
                                        </Form.Select>
                                    </Col>
                                </Row>
                            )}
                            <Button size='sm' className='mt-2 me-2' variant='info' onClick={addModuleRequest}>Add Module Request</Button>
                            {moduleRequests.length > 1 &&
                                <Button size='sm' className='mt-2' variant='danger' onClick={removeModuleRequest}>Remove Module Request</Button>
                            }
                        </Form.Group>
                        <hr className='mb-3' />


                        <Form.Group className='mb-3' controlId='ecForm.FileMutilple'>
                            <Form.Label>Evidence</Form.Label>
                            <Form.Control type='file' multiple onChange={handleFileChange} />
                            <Form.Text muted>Please submit your evidence here (max 50MB per file)</Form.Text>
                        </Form.Group>
                        {showFileAlert &&
                            <Alert variant="danger" onClose={() => setShowFileAlert(false)} style={{ width: "20rem" }}>
                                One of the files is larger than 50MB
                            </Alert>
                        }

                        <Button variant="primary" type="submit">Submit Application</Button>
                    </Form>
                </Col>
            </Row>
        </Container>
    )
}
