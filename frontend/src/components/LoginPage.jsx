import { useEffect, useState } from "react"
import { Alert, Button, Col, Container, Form, Row, Spinner, Toast, ToastContainer } from "react-bootstrap"
import { Link, useLocation, useNavigate } from "react-router-dom";
import axios from '../api/axiosConfig'
import { useAuth } from "../providers/AuthProvider";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import ErrorPage from "./ErrorPage";
import { getRoles } from "../api/roles";


export default function LoginPage() {
    const { user, token, setToken } = useAuth()
    const location = useLocation()
    const navigate = useNavigate()
    const queryClient = useQueryClient()
    const [showCredentialsAlert, setShowCredentialsAlert] = useState(false)
    const [showServerAlert, setShowServerAlert] = useState(false)
    const [showRegistrationToast, setShowRegistrationToast] = useState(false)


    // State for the login form fields
    const [formData, setFormData] = useState({
        email: "",
        password: ""
    })


    useEffect(() => {
        // If redirected from registration page, show toast
        if (location.state?.accountCreated) {
            setShowToast(true)
            window.history.replaceState({}, '')
        }
        // If redirected because token is invalid, set it to null
        else if (location.state?.sessionExpired) {
            window.history.replaceState({}, '')
            queryClient.resetQueries() // reset all queries so that they are refetched
            setToken(null)
        }
    }, [location.state])

    console.log("user", user)

    // Get all the roles
    const rolesQuery = useQuery({
        queryKey: ["roles"],
        queryFn: () => getRoles()
    })

    // If user is already logged in, redirect to appropriate page
    useEffect(() => {
        if (user && rolesQuery.data) {
            let role = rolesQuery.data.find(role => role.id == user.roleId).name

            switch (role) {
                case "Student":
                    navigate("/student", { replace: true })
                    break
                case "Clerical_Staff":
                    navigate("/clerical-staff", { replace: true })
                    break
                case "Academic_Staff":
                    navigate("/academic-staff", { replace: true })
                    break
                case "Administrator":
                    navigate("/admin", { replace: true })
            }
        }
    }, [user, rolesQuery.data])

    if (rolesQuery.isLoading)
        return (
            <Container className='mt-3'>
                <Row>
                    <Col md={{ offset: 6 }}>
                        <Spinner animation="border" />
                    </Col>
                </Row>
            </Container>
        )

    if (rolesQuery.isError)
        return <ErrorPage
            errorTitle={`when fetching roles`}
            errorMessage={`The server might not be running`}
            redirectTo="refresh"
        />

    const roles = rolesQuery.data


    function handleChange(event) {
        const { name, value } = event.target;
        setFormData(prevFormData => ({
            ...prevFormData,
            [name]: value
        }))
    }

    function handleSubmit(event) {
        event.preventDefault()

        axios.post(`/auth/login`, formData, { headers: { 'Authorization': null } })
            .then(res => {
                const data = res.data
                setToken(data.jwt)
            }).catch(error => {
                if (error.response?.status == 401) {
                    setShowCredentialsAlert(true)
                    setShowServerAlert(false)
                }
                else {
                    setShowServerAlert(true)
                    setShowCredentialsAlert(false)
                }
            })
    }


    return (
        <>
            <ToastContainer className="p-3" position={"top-start"}>
                <Toast bg="success" onClose={() => setShowRegistrationToast(false)} show={showRegistrationToast} delay={7000} autohide>
                    <Toast.Header closeButton={false}>
                        <img
                            src="/sheffield-favicon.png"
                            style={{ width: '3rem', height: '3rem' }}
                        />
                        <strong>ECF Portal</strong>
                    </Toast.Header>
                    <Toast.Body>Account successfully created. Please sign in.</Toast.Body>
                </Toast>
            </ToastContainer>

            <Container fluid className="vh-100 d-flex justify-content-center align-items-center">
                <Row>
                    <Col className="text-center">
                        <img src="/sheffield-logo.png" style={{ width: '20rem', height: '15rem' }} />
                        <h3 className="mb-4 fw-normal">ECF Portal</h3>
                        <h5 className="mb-2 fw-light">Please sign in</h5>
                        <Form onSubmit={handleSubmit}>
                            <Form.Group className="mb-1" controlId="loginForm.Email1">
                                <Form.Control
                                    type="email"
                                    name="email"
                                    value={formData.email}
                                    onChange={handleChange}
                                    placeholder="Email address"
                                    required
                                />
                            </Form.Group>

                            <Form.Group className="mb-2" controlId="loginForm.Password1">
                                <Form.Control
                                    type="password"
                                    name="password"
                                    value={formData.password}
                                    onChange={handleChange}
                                    placeholder="Password"
                                    required
                                />
                            </Form.Group>

                            <Button variant="primary" type="submit" className="w-100 mb-1">Sign In</Button>

                            {showCredentialsAlert &&
                                <Alert className='mt-2' variant="danger" style={{ width: "20rem" }}>
                                    Invalid credentials or account has not yet been approved by administrators
                                </Alert>
                            }
                            {showServerAlert &&
                                <Alert className='mt-2' variant="danger" style={{ width: "20rem" }}>
                                    Login unsuccessful, server might be offline
                                </Alert>
                            }
                            <Form.Text muted>
                                Registering? Click <Link to="/register">here</Link>.
                            </Form.Text>
                        </Form>
                    </Col>
                </Row>
            </Container>
        </>
    )
}
