import React from 'react'
import { Button, Container, Nav, Navbar } from 'react-bootstrap'

export default function ClericalStaffNavBar() {


  return (
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
                    <Nav.Link >EC Applications</Nav.Link>
                </Nav>
                <Nav.Item>
                    <Button variant='outline-danger'>Log Out</Button>
                </Nav.Item>
            </Navbar.Collapse>
        </Container>
    </Navbar>
  )
}
