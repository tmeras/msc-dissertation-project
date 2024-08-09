import {useState} from 'react'
import { useAuth } from '../../providers/AuthProvider'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Button, Col, Container, Row, Spinner, Table } from 'react-bootstrap'
import { getUsers, updateUser } from '../../api/users'
import { getDepartments } from '../../api/departments'
import { getRoles } from '../../api/roles'
import ErrorPage from "../ErrorPage"
import { Navigate } from 'react-router'


export default function AdminUsers() {
    const {user} = useAuth()
    const queryClient = useQueryClient()


    // Get all the users
    const usersQuery = useQuery({
        queryKey: ["users"],
        queryFn: () => getUsers()
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
        if (usersQuery.error.response?.status == 401)
            // Token most likely expired or is invalid due to server restart
            return <Navigate to="/login" state={{ sessionExpired: true }} />
        else
        return <ErrorPage 
                    errorTitle={`when fetching users`}
                    errorMessage={`${usersQuery.error.code}
                    | Server Response: ${usersQuery.error.response?.data.status}-${usersQuery.error.response?.data.error}`} 
                />     

    if (departmentsQuery.isError)
        if (departmentsQuery.error.response?.status == 401)
            // Token most likely expired or is invalid due to server restart
            return <Navigate to="/login" state={{ sessionExpired: true }} />
        else
        return <ErrorPage 
                    errorTitle={`when fetching departments`}
                    errorMessage={`${departmentsQuery.error.code}
                    | Server Response: ${departmentsQuery.error.response?.data.status}-${departmentsQuery.error.response?.data.error}`} 
                />     
          
    if (rolesQuery.isError)
        if (rolesQuery.error.response?.status == 401)
            // Token most likely expired or is invalid due to server restart
            return <Navigate to="/login" state={{ sessionExpired: true }} />
        else
        return <ErrorPage 
                    errorTitle={`when fetching roles`}
                    errorMessage={`${rolesQuery.error.code}
                    | Server Response: ${rolesQuery.error.response?.data.status}-${rolesQuery.error.response?.data.error}`} 
                />     

    if (updateUserMutation.isError)
        if (updateUserMutation.error.response?.status == 401)
            // Token most likely expired or is invalid due to server restart
            return <Navigate to="/login" state={{ sessionExpired: true }} />
        else
        return <ErrorPage 
                    errorTitle={`when updating user`}
                    errorMessage={`${updateUserMutation.error.code}
                    | Server Response: ${updateUserMutation.error.response?.data.status}-${updateUserMutation.error.response?.data.error}`} 
                />     

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
            <h2 className="text-center fw-normal">Users</h2>
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
                        <td>{roles.find(role => role.id == user.roleId).name.replace('_',' ')}</td>
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
