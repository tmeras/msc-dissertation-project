import React from 'react'
import { Button, Container, Nav, Navbar } from 'react-bootstrap'
import { Outlet, useNavigate } from 'react-router'
import { Link } from 'react-router-dom'
import { useAuth } from '../../providers/AuthProvider'

export default function StudentNavBar() {
    const {setToken, setUser} = useAuth()
    const navigate = useNavigate()

    function logOut() {
        setToken(null)
        navigate("/login", {replace: true})
    }

    return (
        <>
        <Navbar expand="lg" className="bg-secondary-subtle">
            <Container>
                <Navbar.Brand >
                    ECF Portal
                </Navbar.Brand>
                <Navbar.Toggle aria-controls='basic-navbar-nav' />
                <Navbar.Collapse id='basic-navbar-nav'>
                    <Nav className='me-1'>
                        <Nav.Link as={Link} to="/student/information">Personal Information</Nav.Link>
                    </Nav>
                    <Nav className='me-auto'>
                        <Nav.Link as={Link} to="/student/ec-applications">EC Applications</Nav.Link>
                    </Nav>
                    <Nav.Item>
                        <Button size='sm' variant='outline-danger' onClick={logOut}>Log Out</Button>
                    </Nav.Item>
                </Navbar.Collapse>
            </Container>
        </Navbar>

        <Outlet />
        </>
    )
}
