import React from 'react'
import { Button, Container, Nav, Navbar, NavDropdown } from 'react-bootstrap'
import { Outlet, useNavigate } from 'react-router'
import { Link } from 'react-router-dom'
import { useAuth } from '../../providers/AuthProvider'

export default function AcademicStaffNavBar() {
    const { setToken, user } = useAuth()
    const navigate = useNavigate()

    function logOut() {
        setToken(null)
        navigate("/login")
    }

    return (
        <>
            <Navbar expand="lg" className="bg-secondary-subtle">
                <Container>
                    <Navbar.Brand >
                        ECF Portal
                    </Navbar.Brand>
                    <Navbar.Toggle aria-controls='basic-navbar-nav' />
                    <Navbar.Collapse>
                        <Nav className='me-auto'>
                            <Nav.Link as={Link} to="/academic-staff/ec-applications">EC Applications</Nav.Link>
                        </Nav>
                        <NavDropdown title={user?.name} className='me-5'>
                            <NavDropdown.Item onClick={logOut} style={{ cursor: 'pointer', color: 'red' }}>
                                Log Out
                            </NavDropdown.Item>
                        </NavDropdown>
                    </Navbar.Collapse>
                </Container>
            </Navbar>

            <Outlet />
        </>
    )
}
