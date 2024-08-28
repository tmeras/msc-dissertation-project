import { useState } from 'react'
import { Alert, Button, Col, Container, Form, Modal, Row, Spinner, Table } from 'react-bootstrap'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { createModule, getModules, updateModule } from '../../api/modules'
import { getDepartments } from '../../api/departments'
import ErrorPage from "../ErrorPage"
import { Navigate } from 'react-router'


export default function AdminModules() {
    const queryClient = useQueryClient()
    const [showNameAlert, setShowNameAlert] = useState(false)
    const [showCodeAlert, setShowCodeAlert] = useState(false)
    const [showDepartmentAlert, setShowDepartmentAlert] = useState(false)
    const [showCreateModal, setShowCreateModal] = useState(false)
    const [showUpdateModal, setShowUpdateModal] = useState(false)

    // State for the modal input fields
    const [formData, setFormData] = useState({
        code: "",
        name: "",
        departmentId: 0
    })

    // Get all the modules
    const modulesQuery = useQuery({
        queryKey: ["modules"],
        queryFn: () => getModules()
    })

    // Get all the departments
    const departmentsQuery = useQuery({
        queryKey: ["departments"],
        queryFn: () => getDepartments()
    })

    const createModuleMutation = useMutation({
        mutationFn: createModule,
        onSuccess: data => {
            queryClient.invalidateQueries(["modules"])
        }
    })

    const updateModuleMutation = useMutation({
        mutationFn: updateModule,
        onSuccess: data => {
            queryClient.invalidateQueries(["modules"])
        }
    })

    if (modulesQuery.isLoading || departmentsQuery.isLoading)
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
                | Status: ${modulesQuery.error.response?.status}`}
            />

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

    if (createModuleMutation.isError)
        if (createModuleMutation.error.response?.status == 401)
            // Token most likely expired or is invalid due to server restart
            return <Navigate to="/login" state={{ sessionExpired: true }} />
        else
            return <ErrorPage
                errorTitle={`when creating module`}
                errorMessage={`${createModuleMutation.error.code}
                | Status: ${createModuleMutation.error.response?.status}`}
            />

    if (updateModuleMutation.isError)
        if (updateModuleMutation.error.response?.status == 401)
            // Token most likely expired or is invalid due to server restart
            return <Navigate to="/login" state={{ sessionExpired: true }} />
        else
            return <ErrorPage
                errorTitle={`when updating module`}
                errorMessage={`${updateModuleMutation.error.code}
                | Status: ${updateModuleMutation.error.response?.status}`}
            />

    const modules = modulesQuery.data
    const departments = departmentsQuery.data


    function handleChange(event) {
        let { name, value } = event.target;
        if (name === "departmentId")
            value = parseInt(value)

        setFormData(prevFormData => ({
            ...prevFormData,
            [name]: value
        }))
    }

    function handleCloseCreateModal() {
        setShowCreateModal(false)
        setFormData({
            code: "",
            name: "",
            departmentId: 0
        })
        setShowNameAlert(false)
        setShowCodeAlert(false)
        setShowDepartmentAlert(false)
    }

    function handleShowCreateModal() {
        setShowCreateModal(true)
    }

    function handleCreateModule() {

        // Validate code
        if (formData.code.trim() === "" || modules.some(module => module.code == formData.code)) {
            setShowCodeAlert(true)
            return
        }
        setShowCodeAlert(false)

        // Validate name
        if (formData.name.trim() === "") {
            setShowNameAlert(true)
            return
        }
        setShowNameAlert(false)

        // Validate department
        if (formData.departmentId == 0) {
            setShowDepartmentAlert(true)
            return
        }

        createModuleMutation.mutate(formData, {
            onSuccess: data => {
                handleCloseCreateModal()
            }
        })
    }

    function handleCloseUpdateModal() {
        setShowUpdateModal(false)
        setFormData({
            code: "",
            name: "",
            departmentId: 0
        })
        setShowNameAlert(false)
        setShowCodeAlert(false)
        setShowDepartmentAlert(false)
    }

    function handleShowUpdateModal(module) {
        setFormData(module)
        setShowUpdateModal(true)
    }

    function handleUpdateModule() {

        // Validate Name
        if (formData.name.trim() === "") {
            setShowNameAlert(true)
            return
        }
        setShowNameAlert(false)

        // Validate department
        if (formData.departmentId == 0) {
            setShowDepartmentAlert(true)
            return
        }

        updateModuleMutation.mutate(formData, {
            onSuccess: data => {
                handleCloseUpdateModal()
            }
        })
    }

    return (
        <>
            <Modal show={showCreateModal} onHide={handleCloseCreateModal} centered>
                <Modal.Header closeButton>
                    <Modal.Title>Add New Module</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Form.Group controlId='createModuleForm.Text1' className='mb-3'>
                            <Form.Label>Module Code</Form.Label>
                            <Form.Control
                                type='text'
                                name='code'
                                value={formData.code}
                                onChange={handleChange}
                                required
                            />
                        </Form.Group>
                        {showCodeAlert &&
                            <Alert className='mt-2' variant="danger" onClose={() => setShowCodeAlert(false)} style={{ width: "25rem" }} >
                                Module code must be unique and not empty
                            </Alert>
                        }

                        <Form.Group controlId='createModuleForm.Text2' className='mb-3'>
                            <Form.Label>Module Name</Form.Label>
                            <Form.Control
                                type='text'
                                name='name'
                                value={formData.name}
                                onChange={handleChange}
                                required
                            />
                        </Form.Group>
                        {showNameAlert &&
                            <Alert className='mt-2' variant="danger" onClose={() => setShowNameAlert(false)} style={{ width: "25rem" }} >
                                Module name must not be empty
                            </Alert>
                        }

                        <Form.Group controlId='createModuleForm.Select1' className='mb-3'>
                            <Form.Label>Module Department</Form.Label>
                            <Form.Select
                                name='departmentId'
                                value={formData.departmentId}
                                onChange={handleChange}
                                required
                            >
                                <option value={0}>Select a department</option>
                                {departments.map(department =>
                                    <option key={department.id} value={department.id}>{department.name}</option>
                                )}
                            </Form.Select>
                        </Form.Group>
                        {showDepartmentAlert &&
                            <Alert className='mt-2' variant="danger" onClose={() => setShowDepartmentAlert(false)} style={{ width: "25rem" }} >
                                Please select a department
                            </Alert>
                        }
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleCloseCreateModal}>
                        Close
                    </Button>
                    <Button variant="primary" onClick={handleCreateModule}>
                        Save
                    </Button>
                </Modal.Footer>
            </Modal>

            <Modal show={showUpdateModal} onHide={handleCloseUpdateModal} centered>
                <Modal.Header closeButton>
                    <Modal.Title>Edit Module</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Form.Group controlId='updateModuleForm.Text1' className='mb-3'>
                            <Form.Label>Module Code</Form.Label>
                            <Form.Control
                                type='text'
                                name='code'
                                value={formData.code}
                                onChange={handleChange}
                                disabled
                                required
                            />
                        </Form.Group>

                        <Form.Group controlId='updateModuleForm.Text2' className='mb-3'>
                            <Form.Label>Module Name</Form.Label>
                            <Form.Control
                                type='text'
                                name='name'
                                value={formData.name}
                                onChange={handleChange}
                                required
                            />
                        </Form.Group>
                        {showNameAlert &&
                            <Alert className='mt-2' variant="danger" onClose={() => setShowNameAlert(false)} style={{ width: "25rem" }} >
                                Module name must not be empty
                            </Alert>
                        }

                        <Form.Group controlId='updateModuleForm.Select1' className='mb-3'>
                            <Form.Label>Module Department</Form.Label>
                            <Form.Select
                                name='departmentId'
                                value={formData.departmentId}
                                onChange={handleChange}
                                required
                            >
                                <option value={0}>Select a department</option>
                                {departments.map(department =>
                                    <option key={department.id} value={department.id}>{department.name}</option>
                                )}
                            </Form.Select>
                        </Form.Group>
                        {showDepartmentAlert &&
                            <Alert className='mt-2' variant="danger" onClose={() => setShowDepartmentAlert(false)} style={{ width: "25rem" }} >
                                Please select a department
                            </Alert>
                        }
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleCloseUpdateModal}>
                        Close
                    </Button>
                    <Button variant="primary" onClick={handleUpdateModule}>
                        Save
                    </Button>
                </Modal.Footer>
            </Modal>

            <Container className="mt-3">
                <h2 className="text-center fw-normal">Modules</h2>

                <div className="d-flex justify-content-end">
                    <Button variant='primary' className='ms-auto' onClick={handleShowCreateModal}>
                        <img src='/plus.svg' className='mb-1 me-1' />
                        Add Module
                    </Button>
                </div>

                <Table striped hover className="mt-3 shadow">
                    <thead className="table-light">
                        <tr>
                            <th scope="col" className="col-2">Code</th>
                            <th scope="col" className="col-5">Name</th>
                            <th>Department </th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody className="table-group-divider">
                        {modules.sort((a, b) => a.code.localeCompare(b.code)).map(module =>
                            <tr key={module.code}>
                                <td>{module.code}</td>
                                <td>{module.name}</td>
                                <td>
                                    {departments.find(department => department.id == module.departmentId).name}

                                </td>
                                <td>
                                    <Button variant='secondary' size='sm' onClick={() => handleShowUpdateModal(module)}>Edit</Button>
                                </td>
                            </tr>
                        )}
                    </tbody>
                </Table>
            </Container>
        </>
    )
}
