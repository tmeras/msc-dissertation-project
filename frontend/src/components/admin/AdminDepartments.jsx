import { useState } from 'react'
import { Container, Button, Table, Spinner, Row, Col, Modal, Form, Alert } from 'react-bootstrap'
import { useAuth } from '../../providers/AuthProvider'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { createDepartment, getDepartments, updateDepartment } from '../../api/departments'
import ErrorPage from "../ErrorPage"
import { Navigate } from 'react-router'


export default function AdminDepartments() {
    const queryClient = useQueryClient()
    const [showNameAlert, setShowNameAlert] = useState(false)
    const [showCreateModal, setShowCreateModal] = useState(false)
    const [showUpdateModal, setShowUpdateModal] = useState(false)

    // State for the modal input fields
    const [formData, setFormData] = useState({
        id: "",
        name: ""
    })

    // Get all departments
    const departmentsQuery = useQuery({
        queryKey: ["departments"],
        queryFn: () => getDepartments()
    })

    const createDepartmentMutation = useMutation({
        mutationFn: createDepartment,
        onSuccess: data => {
            queryClient.invalidateQueries(["departments"])
        }
    })

    const updateDepartmentMutation = useMutation({
        mutationFn: updateDepartment,
        onSuccess: data => {
            queryClient.invalidateQueries(["departments"])
        }
    })

    if (departmentsQuery.isLoading)
        return (
            <Container className='mt-3'>
                <Row>
                    <Col md={{ offset: 6 }}>
                        <Spinner animation="border" />
                    </Col>
                </Row>
            </Container>
        )

    if (departmentsQuery.isError)
        if (departmentsQuery.error.response?.status == 401)
            // Token most likely expired or is invalid due to server restart
            return <Navigate to="/login" state={{ sessionExpired: true }} />
        else
            return <ErrorPage
                errorTitle={`when fetching departments`}
                errorMessage={`${departmentsQuery.error.code}
                    | Status: ${departmentsQuery.error.response?.status}`}
            />

    if (createDepartmentMutation.isError)
        if (createDepartmentMutation.error.response?.status == 401)
            // Token most likely expired or is invalid due to server restart
            return <Navigate to="/login" state={{ sessionExpired: true }} />
        else
            return <ErrorPage
                errorTitle={`when creating department`}
                errorMessage={`${createDepartmentMutation.error.code}
                    | Status: ${createDepartmentMutation.error.response?.status}`}
            />

    if (updateDepartmentMutation.isError)
        if (updateDepartmentMutation.error.response?.status == 401)
            // Token most likely expired or is invalid due to server restart
            return <Navigate to="/login" state={{ sessionExpired: true }} />
        else
            return <ErrorPage
                errorTitle={`when updating department`}
                errorMessage={`${updateDepartmentMutation.error.code}
                | Status: ${updateDepartmentMutation.error.response?.status}`}
            />

    const departments = departmentsQuery.data


    function handleChange(event) {
        const { name, value } = event.target;
        setFormData(prevFormData => ({
            ...prevFormData,
            [name]: value
        }))
    }

    function handleCloseCreateModal() {
        setShowCreateModal(false)
        setFormData({
            id: "",
            name: ""
        })
        setShowNameAlert(false)
    }

    function handleShowCreateModal() {
        setShowCreateModal(true)
    }

    function handleCreateDepartment() {

        // Validate Name
        if (formData.name.trim() === "") {
            setShowNameAlert(true)
            return
        }

        createDepartmentMutation.mutate(formData, {
            onSuccess: data => {
                handleCloseCreateModal()
            }
        })
    }

    function handleCloseUpdateModal() {
        setShowUpdateModal(false)
        setFormData({
            id: "",
            name: ""
        })
        setShowNameAlert(false)
    }

    function handleShowUpdateModal(department) {
        setFormData(department)
        setShowUpdateModal(true)
    }

    function handleUpdateDepartment() {

        // Validate name
        if (formData.name.trim() === "") {
            setShowNameAlert(true)
            return
        }

        updateDepartmentMutation.mutate(formData, {
            onSuccess: data => {
                handleCloseUpdateModal()
            }
        })
    }

    return (
        <>
            <Modal show={showCreateModal} onHide={handleCloseCreateModal} centered>
                <Modal.Header closeButton>
                    <Modal.Title>Add New Department</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Form.Group controlId='createDepartmentForm.Text1' className='mb-3'>
                            <Form.Label>Department Name</Form.Label>
                            <Form.Control
                                type='text'
                                name='name'
                                value={formData.name}
                                onChange={handleChange}
                                required
                            />
                        </Form.Group>
                    </Form>
                    {showNameAlert &&
                        <Alert className='mt-2' variant="danger" onClose={() => setShowNameAlert(false)} style={{ width: "25rem" }} >
                            Department name must not be empty
                        </Alert>
                    }
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleCloseCreateModal}>
                        Close
                    </Button>
                    <Button variant="primary" onClick={handleCreateDepartment}>
                        Save
                    </Button>
                </Modal.Footer>
            </Modal>

            <Modal show={showUpdateModal} onHide={handleCloseUpdateModal} centered>
                <Modal.Header closeButton>
                    <Modal.Title>Update Department</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Form.Group controlId='updateDepartmentForm.Text1' className='mb-3'>
                            <Form.Label>Department ID</Form.Label>
                            <Form.Control
                                type='text'
                                name='id'
                                value={formData.id}
                                disabled
                                required
                            />
                        </Form.Group>
                        <Form.Group controlId='updateDepartmentForm.Text2'>
                            <Form.Label>Department Name</Form.Label>
                            <Form.Control
                                type='text'
                                name='name'
                                value={formData.name}
                                onChange={handleChange}
                                required
                            />
                        </Form.Group>
                    </Form>
                    {showNameAlert &&
                        <Alert className='mt-2' variant="danger" onClose={() => setShowNameAlert(false)} style={{ width: "25rem" }} >
                            Department name must not be empty
                        </Alert>
                    }
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleCloseUpdateModal}>
                        Close
                    </Button>
                    <Button variant="primary" onClick={handleUpdateDepartment}>
                        Save
                    </Button>
                </Modal.Footer>
            </Modal>

            <Container className="mt-3 w-75">
                <h2 className="text-center fw-normal">Departments</h2>

                <div className="d-flex justify-content-end">
                    <Button variant='primary' className='ms-auto' onClick={handleShowCreateModal}>
                        <img src='/plus.svg' className='mb-1 me-1' />
                        Add Department
                    </Button>
                </div>

                <Table striped hover className="mt-3 shadow">
                    <thead className="table-light">
                        <tr>
                            <th scope="col" className="col-3">#</th>
                            <th scope="col" className="col-8">Name</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody className="table-group-divider">
                        {departments.sort((a, b) => a.id - b.id).map(department =>
                            <tr key={department.id}>
                                <td>{department.id}</td>
                                <td>{department.name}</td>
                                <td>
                                    <Button variant='secondary' size='sm' onClick={() => handleShowUpdateModal(department)} >Edit</Button>
                                </td>
                            </tr>
                        )}
                    </tbody>
                </Table>
            </Container>
        </>
    )
}
