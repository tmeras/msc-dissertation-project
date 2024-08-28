import { useQuery } from "@tanstack/react-query";
import { Navigate, Outlet } from "react-router";
import { getRoles } from "../api/roles";
import { Container, Row, Col, Spinner } from "react-bootstrap";
import ErrorPage from "./ErrorPage";
import { useAuth } from "../providers/AuthProvider";


export default function ProtectedRoute(props) {
    const { token, user } = useAuth()

    const rolesQuery = useQuery({
        queryKey: ["roles"],
        queryFn: () => getRoles()
    })


    if (rolesQuery.isLoading)
        return (
            <Container className='mt-3'>
                <Row>
                    <Col md={{ offset: 6 }}>
                        <Spinner animation="border" />
                    </Col>
                </Row>
            </Container>
        )

    if (rolesQuery.isError)
        return <ErrorPage
            errorTitle={`when fetching roles`}
            errorMessage={`The server might not be running`}
            redirectTo="refresh"
        />

    const roles = rolesQuery.data

    // Ensure that the user is authenticated and has the appropriate role
    if (!token || !user || (roles.find(role => role.id == user.roleId).name !== props.requiredRole)) {

        // If not authenticated or if role is not appropriate, redirect to login page
        return <Navigate to="/login" replace />;
    }


    // Authentication and role ok, render child routes
    return <Outlet />

}