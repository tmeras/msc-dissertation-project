import { useEffect, useState } from "react"
import { Alert, Button, Col, Container, Form, Row, Toast, ToastContainer } from "react-bootstrap"
import { Link, useLocation } from "react-router-dom";


export default function LoginPage() {
    const location = useLocation()
    const [showCredentialsAlert, setShowCredentialsAlert] = useState(false)
    const [showToast, setShowToast] = useState(false)

    // State for the login form fields
    const [formData, setFormData] = useState({
        email: "",
        password: ""
    })
    
    // If redirected from registration page, show toast
    useEffect(() => {
        if (location.state?.accountCreated) {
            setShowToast(true)
            window.history.replaceState({}, '')
        }
    }, [location.state])
    

    function handleChange(event) {
        const { name, value } = event.target;
        setFormData(prevFormData => ({
            ...prevFormData,
            [name]: value
        }))
    }

    function handleSubmit(event) {
        event.preventDefault()

        console.log(formData)
    }

    console.log(formData)

    return (
        <>
        <ToastContainer className="p-3" position={"top-start"}>
          <Toast bg="success" onClose={() => setShowToast(false)} show={showToast} delay={5000} autohide>
            <Toast.Header closeButton={false}>
              <img 
                src="/sheffield-favicon.png"  
                style={{width: '3rem', height: '3rem'}}
              />
              <strong>ECF Portal</strong>
            </Toast.Header>
            <Toast.Body>Account successfully created! Please sign in.</Toast.Body>
          </Toast>
        </ToastContainer>

        <Container fluid className="vh-100 d-flex justify-content-center align-items-center">
            <Row>
            <Col className="text-center">
                <img src="/sheffield-logo.png" style={{width: '20rem', height: '15rem'}} />
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
                        <Alert className='mt-2' variant="danger" style={{width: "20rem"}}>
                            Invalid credentials!
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
