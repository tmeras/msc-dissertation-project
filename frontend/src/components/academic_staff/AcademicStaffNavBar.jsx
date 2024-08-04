import React from 'react'
import { Button, Container, Nav, Navbar } from 'react-bootstrap'
import { Outlet } from 'react-router'
import { Link } from 'react-router-dom'

export default function AcademicStaffNavBar() {

  return (
    <>
    <Navbar expand="lg" className="bg-secondary-subtle">
        <Container>
            <Navbar.Brand >
                {/* <img 
                    src='/sheffield-logo.png'
                    height='50'
                /> */}
                ECF Portal
            </Navbar.Brand>
            <Navbar.Toggle aria-controls='basic-navbar-nav' />
            <Navbar.Collapse id='basic-navbar-nav'>
                <Nav className='me-auto'>
                    <Nav.Link as={Link} to="/academic-staff/ec-applications">EC Applications</Nav.Link>
                </Nav>
                <Nav.Item>
                    <Button size='sm' variant='outline-danger'>Log Out</Button>
                </Nav.Item>
            </Navbar.Collapse>
        </Container>
    </Navbar>

    <Outlet />
    </>
  )
}
