import { formatDate } from "../../utils"
import Table from "react-bootstrap/Table"
import { Badge, Container, Spinner, Row, Col } from 'react-bootstrap'
import { useQuery, useMutation } from '@tanstack/react-query'
import { useAuth } from "../../providers/AuthProvider"
import { getEcApplications, getEcApplicationsByStudentDepartmentId } from '../../api/ecApplications'
import { getModuleRequestsByEcApplicationIds } from "../../api/moduleRequests"
import { getUsersByIds } from "../../api/users"
import { Link, useNavigate } from "react-router-dom"


export default function ClericalStaffEcApplications() {
    const {setUser, user} = useAuth()
    const navigate = useNavigate()
    console.log(user)

    // Get all EC applications submitted by students in the same department as the staff member
    const ecApplicationsQuery = useQuery({
        queryKey: ["ecApplications", {studentDepartmentId: user.departmentId}],
        queryFn: () => getEcApplicationsByStudentDepartmentId(user.departmentId),
    })

    const ecApplicationIds = ecApplicationsQuery.data?.map(ecApplication => ecApplication.id)

    // Fetch all module requests related to the fetched EC applications
    const moduleRequestsQuery = useQuery({
        queryKey: ["moduleRequests", {ecApplicationIds: ecApplicationIds}],
        queryFn: () => getModuleRequestsByEcApplicationIds(ecApplicationIds),
        enabled: !!ecApplicationIds
    })

    // Fetch all students that submitted the fetched EC applications
    const studentIds =  Array.from(new Set(ecApplicationsQuery.data?.map(ecApplication => ecApplication.studentId)))
    const studentsQuery = useQuery({
        queryKey: ["users", {ids: studentIds}],
        queryFn: () => getUsersByIds(studentIds),
        enabled: !(studentIds.length == 0)
    }) 

    
    if (ecApplicationsQuery.isLoading || moduleRequestsQuery.isLoading ||studentsQuery.isLoading )
        return (
            <Container className='mt-3'>
              <Row>
              <Col md={{offset: 6 }}>
                <Spinner animation="border" />
              </Col>
              </Row>
            </Container>
        )
    
    if (ecApplicationsQuery.isError)
        return <h1>Error fetching EC applications: {ecApplicationsQuery.error.response?.status}</h1>
    
    if (moduleRequestsQuery.isError)
        return <h1>Error fetching module requests: {moduleRequestsQuery.error.response?.status}</h1>
    
    if (studentsQuery.isError)
        return <h1>Error fetching students: {studentsQuery.error.response?.status}</h1>
    
    const ecApplications = ecApplicationsQuery.data

    // Any requests for deadline extensions are urgent
    function isEcApplicationUrgent(ecApplicationId) {
        let isUrgent = false
        moduleRequestsQuery.data.forEach(moduleRequest =>{
            if (moduleRequest.ecApplicationId == ecApplicationId && (moduleRequest.requestedOutcome === "Deadline Extension" 
                || moduleRequest.requestedOutcome == "Defer Formal Examination" || moduleRequest.requestedOutcome == "Defer Formal Assessment")) {
                isUrgent = true
                return
            }
        })

        return isUrgent
    }

    return (
        <Container className="mt-3">
            <h2 className="text-center">Extenuating Circumstances Applications</h2>
            <Table striped hover className="mt-3 shadow">
                <thead className="table-light">
                    <tr>
                        <th>#</th>
                        <th>Submitted By</th>
                        <th>Submitted On</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody className="table-group-divider">
                    {ecApplications.sort((a, b) => a.id - b.id).map(ecApplication => (
                        <tr
                            key={ecApplication.id} 
                            onClick={() => navigate(`/clerical-staff/ec-applications/${ecApplication.id}`)} 
                            style={{cursor: "pointer"}}
                        >
                            <td>{ecApplication.id}</td>
                            <td>{studentsQuery.data.find(student => student.id == ecApplication.studentId).name}</td>
                            <td>{formatDate(ecApplication.submittedOn)}</td>
                            <td>
                                {ecApplication.isReferred!= null ? (
                                    ecApplication.isReferred != null && <Badge bg='success' className="me-1">Decision submitted</Badge>
                                ): (
                                    <>
                                    <Badge bg='primary' className="me-1">Pending Decision</Badge>
                                    {ecApplication.requiresFurtherEvidence && <Badge bg='info' className="me-1">Further evidence requested</Badge>}
                                    {isEcApplicationUrgent(ecApplication.id) && <Badge bg='danger' className="me-1">Urgent</Badge>}
                                    </>
                                )}
                            </td>
                        </tr>
                    ))}
                </tbody>
            </Table>
        </Container>
    )
}

