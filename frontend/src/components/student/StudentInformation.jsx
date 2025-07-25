import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useEffect, useState } from 'react'
import { useAuth } from '../../providers/AuthProvider'
import { getStudentInformationByStudentId, updateStudentInformation } from '../../api/studentInformation'
import { Container, Row, Col, Spinner, Form, Button, Alert } from 'react-bootstrap'
import ErrorPage from "../ErrorPage"
import { Navigate } from 'react-router'


export default function StudentInformation() {
    const { user } = useAuth()
    const queryClient = useQueryClient()
    const [showSuccessAlert, setShowSuccessAlert] = useState(false)

    // State for the form fields
    const [formData, setFormData] = useState({
        hasHealthIssues: false,
        hasDisability: false,
        hasLsp: false,
        additionalDetails: ""
    })

    // Get the student's information
    const studentInformationQuery = useQuery({
        queryKey: ["studentInformation", { studentId: user.id }],
        queryFn: () => getStudentInformationByStudentId(user.id)
    })

    const updateStudentInformationMutation = useMutation({
        mutationFn: updateStudentInformation,
        onSuccess: data => {
            queryClient.invalidateQueries(["studentInformation"])
        }
    })

    useEffect(() => {
        if (studentInformationQuery.data?.[0])
            setFormData(studentInformationQuery.data[0])
    }, [studentInformationQuery.data])


    if (studentInformationQuery.isLoading)
        return (
            <Container className='mt-3'>
                <Row>
                    <Col md={{ offset: 6 }}>
                        <Spinner animation="border" />
                    </Col>
                </Row>
            </Container>
        )

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

    if (updateStudentInformationMutation.isError)
        if (updateStudentInformationMutation.error.response?.status == 401)
            // Token most likely expired or is invalid due to server restart
            return <Navigate to="/login" state={{ sessionExpired: true }} />
        else
            return <ErrorPage
                errorTitle={`when updating student information`}
                errorMessage={`${updateStudentInformationMutation.error.code}
                    | Status: ${updateStudentInformationMutation.error.response?.status}`}
            />


    // Handle change for the form fields
    const handleChange = (event) => {
        const { name, value, type, checked } = event.target;
        setFormData(prevFormData => ({
            ...prevFormData,
            [name]: type === "checkbox" ? checked : value
        }))
    }

    function handleSubmit(event) {
        event.preventDefault()

        updateStudentInformationMutation.mutate({
            ...formData,
            id: studentInformationQuery.data[0].id
        }, {
            onSuccess: data => {
                setShowSuccessAlert(true)
            }
        })
    }


    return (
        <Container className='mt-3'>
            <Row>
                <Col>
                    <h4 className='mb-3 text-center '>Personal Information</h4>
                </Col>
            </Row>
            <Row>
                <Col md={{ offset: 3 }}>
                    <Form onSubmit={handleSubmit}>
                        <Form.Check
                            type='checkbox'
                            name='hasLsp'
                            checked={formData.hasLsp}
                            onChange={handleChange}
                            label='Are you on a learning support plan?'
                            className='mb-3'
                        />
                        <Form.Check
                            type='checkbox'
                            name='hasDisability'
                            checked={formData.hasDisability}
                            onChange={handleChange}
                            label='Do you have any disabilities?'
                            className='mb-3'
                        />
                        <Form.Check
                            type='checkbox'
                            name='hasHealthIssues'
                            checked={formData.hasHealthIssues}
                            onChange={handleChange}
                            label='Have you been diagnosed with any chronic illnesses?'
                            className='mb-3'
                        />

                        <div className='mb-4 w-75'>
                                <Form.Text muted className='mb-4'>
                                        Please select any of the above which apply to you. Any disabilities
                                        or illnesses which your department is not aware of
                                        (e.g. through an existing learning support plan) should also be evidenced in your EC applications
                                </Form.Text>
                        </div>

                        <Form.Group className='mb-2 w-75' controlId='studentInfoForm.TextArea1'>
                            <Form.Label>Additional Information</Form.Label>
                            <Form.Control
                                as="textarea"
                                rows={3}
                                name='additionalDetails'
                                value={formData.additionalDetails}
                                onChange={handleChange}
                            />
                            <Form.Text muted>
                                Please enter any other information you deem relevant
                                for your exteunating circumstances applications here
                            </Form.Text>
                        </Form.Group>

                        <Button variant='primary' type='submit' className='mb-3'>
                            Update Information
                        </Button>

                        {showSuccessAlert &&
                            <Alert style={{ width: "13rem" }} variant="success" onClose={() => setShowSuccessAlert(false)} dismissible>
                                Update successful!
                            </Alert>
                        }
                    </Form>
                </Col>
            </Row>
        </Container>
    )
}
