import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useEffect, useState } from 'react'
import { Container, Row, Col, Form, Spinner, Button, Alert } from 'react-bootstrap'
import { getDepartments } from '../api/departments'
import { getRoles } from '../api/roles'
import ErrorPage from './ErrorPage'
import { createStudentInformation } from '../api/studentInformation'
import { useLocation, useNavigate } from 'react-router'
import { registerUser } from '../api/authentication'
import { Link } from 'react-router-dom'
import { replace } from 'dom/lib/mutation'

export default function RegisterPage() {
    const queryClient = useQueryClient()
    const navigate = useNavigate()
    const [showNameAlert, setShowNameAlert] = useState(false)
    const [showEmailAlert, setShowEmailAlert] = useState(false)
    const [showUserExistsAlert, setShowUserExistsAlert] = useState(false)
    const [showPasswordAlert, setShowPasswordAlert] = useState(false)
    const [showPasswordConfirmAlert, setShowPasswordConfirmAlert] = useState(false)
    const [showDepartmentAlert, setShowDepartmentAlert] = useState(false)
    const [showRoleAlert, setShowRoleAlert] = useState(false)
    const [showStudentFields, setShowStudentFields] = useState(false)

    // State for general login fields
    const [formData, setFormData] = useState({
        name: "",
        email: "",
        password: "",
        confirmPassword: "",
        roleId: 0,
        departmentId: 0
    })

    // State for student information specific fields
    const [studentInfoData, setStudentInfoData] = useState({
        hasHealthIssues: false,
        hasDisability: false,
        hasLsp: false,
        additionalDetails: "",
        studentId: 0
    })

    // Get all the departments
    const departmentsQuery = useQuery({
        queryKey: ["departments"],
        queryFn: () => getDepartments()
    })

    // Get all the roles
    const rolesQuery = useQuery({
        queryKey: ["roles"],
        queryFn: () => getRoles()
    })

    const createUserMutation = useMutation({
        mutationFn: registerUser,
        onSuccess: data => {
            queryClient.invalidateQueries("users")
        }
    })

    const createStudentInformationMutation = useMutation({
        mutationFn: createStudentInformation,
        onSuccess: data => {
            queryClient.invalidateQueries("studentInformation")
        }
    })

    if (departmentsQuery.isLoading || rolesQuery.isLoading)
        return (
            <Container className='mt-3'>
                <Row>
                    <Col md={{ offset: 6 }}>
                        <Spinner animation="border" />
                    </Col>
                </Row>
            </Container>
        )

    if (departmentsQuery.isError) {
        return <ErrorPage
            errorTitle={`when fetching departments`}
            errorMessage={`${departmentsQuery.error.code}
                     | Server Response: Status: ${departmentsQuery.error.response?.status} - Message: ${departmentsQuery.error.response?.data.error}`}
            redirectTo="/login"
        />
    }

    if (rolesQuery.isError)
        return <ErrorPage
            errorTitle={`when fetching roles`}
            errorMessage={`${rolesQuery.error.code}
                     | Server Response: ${rolesQuery.error.response?.data.status}-${rolesQuery.error.response?.data.error}`}
            redirectTo="/login"
        />

    if (createUserMutation.isError) {
        // User already exists
        if (createUserMutation.error.response?.status == 409) {
            return <ErrorPage
                errorTitle={`when creating user`}
                errorMessage={`A user with that email already exists`}
                redirectTo="/login"
            />
        }
        else
            return <ErrorPage
                errorTitle={`when creating user`}
                errorMessage={`${createUserMutation.error.code}
                     | Server Response: ${createUserMutation.error.response?.data.status}-${createUserMutation.error.response?.data.error}`}
                redirectTo="/login"
            />
    }

    if (createStudentInformationMutation.isError)
        return <ErrorPage
            errorTitle={`when creating student information`}
            errorMessage={`${createStudentInformationMutation.error.code}
                     | Server Response: ${createStudentInformationMutation.error.response?.data.status}-${createStudentInformationMutation.error.response?.data.error}`}
            redirectTo="/login"
        />

    const departments = departmentsQuery.data
    const roles = rolesQuery.data


    function handleChange(event) {
        let { name, value } = event.target;
        if (name === "departmentId" || name === "roleId")
            value = parseInt(value)

        // Display student information fields if the user has selected the student role
        if (name === "roleId" && value != 0 && roles.find((role) => role.id == value).name === "Student")
            setShowStudentFields(true)
        else if (name === "roleId") {
            setShowStudentFields(false)
            setStudentInfoData({
                hasHealthIssues: false,
                hasDisability: false,
                hasLsp: false,
                additionalDetails: "",
                studentId: 0
            })
        }

        setFormData(prevFormData => ({
            ...prevFormData,
            [name]: value
        }))
    }

    function handleStudentChange(event) {
        const { name, value, type, checked } = event.target;
        setStudentInfoData(prevFormData => ({
            ...prevFormData,
            [name]: type === "checkbox" ? checked : value
        }))
    }

    function handleSubmit(event) {
        event.preventDefault()
        setShowUserExistsAlert(false)

        // Validate name
        if (formData.name.trim() === "") {
            setShowNameAlert(true)
            return
        }
        setShowNameAlert(false)

        // Validate email
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(formData.email)) {
            setShowEmailAlert(true)
            return
        }
        setShowEmailAlert(false)

        // Validate password
        if (formData.password.trim() === "") {
            setShowPasswordAlert(true)
            return
        }
        setShowPasswordAlert(false)

        if (formData.confirmPassword !== formData.password) {
            setShowPasswordConfirmAlert(true)
            return
        }
        setShowPasswordConfirmAlert(false)

        // Validate department
        if (formData.departmentId == 0) {
            setShowDepartmentAlert(true)
            return
        }
        setShowDepartmentAlert(false)


        // Validate role
        if (formData.roleId == 0) {
            setShowRoleAlert(true)
            return
        }
        setShowRoleAlert(false)

        // Staff member accounts require approval
        if (roles.find((role) => role.id == formData.roleId).name === "Clerical_Staff" ||
            roles.find((role) => role.id == formData.roleId).name === "Academic_Staff")
            formData.isApproved = false
        else
            formData.isApproved = true


        createUserMutation.mutate(formData, {
            onSuccess: data => {

                // If creating student account, also create associated student information
                if (roles.find((role) => role.id == formData.roleId).name === "Student") {
                    studentInfoData.studentId = data.id
                    createStudentInformationMutation.mutate(studentInfoData, {
                        onSuccess: data => {
                            navigate("/login", { state: { accountCreated: true, replace: true } })
                        }
                    })
                }
                else {
                    navigate("/login", { state: { accountCreated: true, replace: true } })
                }
            }
        })
    }


    return (
        <Container className='mt-3'>
            <Row>
                <Col>
                    <h4 className='mb-4 mt-5 text-center'>Registration Form</h4>
                </Col>
            </Row>
            <Row>
                <Col md={{ offset: 4 }}>
                    <Form onSubmit={handleSubmit} className='w-75'>
                        <Form.Group className="mb-4 w-75" controlId="registrationForm.Text1">
                            <Form.Label className='text-end'>Name</Form.Label>
                            <Form.Control
                                type="text"
                                name="name"
                                value={formData.name}
                                onChange={handleChange}
                                required
                            />
                        </Form.Group>
                        {showNameAlert &&
                            <Alert className=' mt-2' variant="danger" style={{ width: "20rem" }}>
                                Name cannot be empty
                            </Alert>
                        }

                        <Form.Group className="mb-4 w-75" controlId="registrationForm.Email1">
                            <Form.Label className='text-end'>Email address</Form.Label>
                            <Form.Control
                                type="email"
                                name="email"
                                value={formData.email}
                                onChange={handleChange}
                                required
                            />
                        </Form.Group>
                        {showEmailAlert &&
                            <Alert className=' mt-2' variant="danger" style={{ width: "20rem" }}>
                                Invalid email address
                            </Alert>
                        }
                        {showUserExistsAlert &&
                            <Alert className=' mt-2' variant="danger" style={{ width: "20rem" }}>
                                This email is already in use
                            </Alert>
                        }

                        <Form.Group className="mb-4 w-75" controlId="registrationForm.Password1">
                            <Form.Label className='text-end'>Password</Form.Label>
                            <Form.Control
                                type="password"
                                name="password"
                                value={formData.password}
                                onChange={handleChange}
                                required
                            />
                        </Form.Group>
                        {showPasswordAlert &&
                            <Alert className=' mt-2' variant="danger" style={{ width: "20rem" }}>
                                Password cannot be empty
                            </Alert>
                        }

                        <Form.Group className="mb-4 w-75" controlId="registrationForm.Password2">
                            <Form.Label className='text-end'>Confirm Password</Form.Label>
                            <Form.Control
                                type="password"
                                name="confirmPassword"
                                value={formData.confirmPassword}
                                onChange={handleChange}
                                required
                            />
                        </Form.Group>
                        {showPasswordConfirmAlert &&
                            <Alert className=' mt-2' variant="danger" style={{ width: "20rem" }}>
                                Passwords do not match
                            </Alert>
                        }

                        <Form.Group className="mb-4 w-75" controlId="registrationForm.Select1">
                            <Form.Label className='text-end'>Department</Form.Label>
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
                            <Alert className=' mt-2' variant="danger" style={{ width: "20rem" }}>
                                Please select a department
                            </Alert>
                        }

                        <Form.Group className="mb-4 w-75" controlId="registrationForm.Select2">
                            <Form.Label className='text-end'>Role</Form.Label>
                            <Form.Select
                                name='roleId'
                                value={formData.roleId}
                                onChange={handleChange}
                                required
                            >
                                <option value={0}>Select a role</option>
                                {roles.map(role =>
                                    role.name !== "Administrator" &&
                                    <option key={role.id} value={role.id}>{role.name.replace('_', ' ')}</option>
                                )}
                            </Form.Select>
                        </Form.Group>
                        {showRoleAlert &&
                            <Alert className=' mt-2' variant="danger" style={{ width: "20rem" }}>
                                Please select a role
                            </Alert>
                        }

                        {showStudentFields &&
                            <>
                                <Form.Check
                                    type='checkbox'
                                    name='hasLsp'
                                    checked={studentInfoData.hasLsp}
                                    onChange={handleStudentChange}
                                    label='Are you on a learning support plan?'
                                    className='mb-3'
                                />

                                <Form.Check
                                    type='checkbox'
                                    name='hasDisability'
                                    checked={studentInfoData.hasDisability}
                                    onChange={handleStudentChange}
                                    label='Do you have any disabilities?'
                                    className='mb-3'
                                />

                                <Form.Check
                                    type='checkbox'
                                    name='hasHealthIssues'
                                    checked={studentInfoData.hasHealthIssues}
                                    onChange={handleStudentChange}
                                    label='Have you been diagnosed with any chronic illnesses?'
                                    className='mb-4'
                                />

                                <Form.Group className='mb-3 w-75' controlId='registrationForm.TextArea1'>
                                    <Form.Label>Additional Information</Form.Label>
                                    <Form.Control
                                        as="textarea"
                                        rows={4}
                                        name='additionalDetails'
                                        value={studentInfoData.additionalDetails}
                                        onChange={handleStudentChange}
                                    />
                                    <Form.Text muted>
                                        Please enter any other information you deem relevant
                                        for your exteunating circumstances applications here
                                    </Form.Text>
                                </Form.Group>
                            </>
                        }
                        <Button variant='primary' type='submit' className='w-75 mb-1'>Register</Button>
                        <div>
                            <Form.Text muted>
                                Already registered? Sign in <Link to="/login" replace>here</Link>.
                            </Form.Text>
                        </div>
                    </Form>
                </Col>
            </Row>
        </Container>
    )
}
