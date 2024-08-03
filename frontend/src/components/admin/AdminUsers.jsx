import {useState} from 'react'
import { useAuth } from '../../providers/AuthProvider'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Button, Col, Container, Row, Spinner, Table } from 'react-bootstrap'
import { getUsers, updateUser } from '../../api/users'
import { getDepartments } from '../../api/departments'
import { getRoles } from '../../api/roles'

export default function AdminUsers() {
    const {setUser, user} = useAuth()
    const queryClient = useQueryClient()

    const usersQuery = useQuery({
        queryKey: ["users"],
        queryFn: () => getUsers()
    })

    const departmentsQuery = useQuery({
        queryKey: ["departments"],
        queryFn: () => getDepartments()
    })

    const rolesQuery = useQuery({
        queryKey: ["roles"],
        queryFn: () => getRoles()
    })

    const updateUserMutation = useMutation({
        mutationFn: updateUser,
        onSuccess: data => {
            queryClient.invalidateQueries(["users"])
        }
    })

    if (usersQuery.isLoading || departmentsQuery.isLoading || rolesQuery.isLoading)
        return (
            <Container className='mt-3'>
              <Row>
              <Col md={{offset: 6 }}>
                <Spinner animation="border" />
              </Col>
              </Row>
            </Container>
        )  
    
    if (usersQuery.isError)
        return <h1>Error fetching users: {usersQuery.error.response?.status}</h1>
    
    if (departmentsQuery.isError)
        return <h1>Error fetching departments: {departmentsQuery.error.response?.status}</h1>

    if (rolesQuery.isError)
        return <h1>Error fetching roles: {rolesQuery.error.response?.status}</h1>

    if (updateUserMutation.isError)
        return <h1>Error updating user: {updateUserMutation.error.response?.status}</h1>

    const users = usersQuery.data
    const departments = departmentsQuery.data
    const roles = rolesQuery.data

    function approveUser(userId) {
        updateUserMutation.mutate({
            id: userId,
            isApproved: true
        })
    }

    return (
        <Container className="mt-3">
            <h2 className="text-center">Users</h2>
            <Table striped hover className="mt-3 shadow">
                <thead className="table-light">
                    <tr>
                        <th>#</th>
                        <th>Name</th>
                        <th>Email </th>
                        <th>Role </th>
                        <th>Department </th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody className="table-group-divider">
                {users.sort((a, b) => a.id - b.id).map(user => 
                    <tr key={user.id}>
                        <td>{user.id}</td>
                        <td>{user.name}</td>
                        <td>{user.email}</td>
                        <td>{roles.find(role => role.id == user.roleId).name}</td>
                        <td>{departments.find(department => department.id == user.departmentId).name}</td>
                        <td>
                            {user.isApproved ?
                                <Button variant='outline-primary' size='sm' disabled>Approved</Button>
                                :
                                <Button variant='primary' size='sm' onClick={() => approveUser(user.id)}>Approve</Button>
                            }
                        </td>
                    </tr>
                )}
                </tbody>
            </Table>
        </Container>
    )
}
