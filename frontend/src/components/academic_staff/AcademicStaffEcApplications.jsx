import { formatDate } from "../../utils"
import Table from "react-bootstrap/Table"
import { Badge, Container, Spinner, Row, Col, ProgressBar } from 'react-bootstrap'
import { useQuery, useMutation } from '@tanstack/react-query'
import { useAuth } from "../../providers/AuthProvider"
import { getEcApplications, getEcApplicationsByIds, getEcApplicationsByStudentDepartmentId, getEcApplicationsByStudentDepartmentIdAndIsReferred } from '../../api/ecApplications'
import { getModuleRequestsByEcApplicationIds } from "../../api/moduleRequests"
import { getUsersByIds } from "../../api/users"
import { getModuleDecisionsByEcApplicationIds, getModuleDecisionsByStaffMemberId } from "../../api/moduleDecisions"


export default function AcademicStaffEcApplications() {
    const {setUser, user} = useAuth()

    // Get all EC applications submitted by students in the same department as the staff member
    // and which have been referred by clerical staff
    const ecApplicationsQuery = useQuery({
        queryKey: ["ecApplications", {studentDepartmentId: user.departmentId, isReferred: true}],
        queryFn: () => getEcApplicationsByStudentDepartmentIdAndIsReferred({studentDepartmentId: user.departmentId, isReferred: true}),
    })

    const ecApplicationIds = ecApplicationsQuery.data?.map(ecApplication => ecApplication.id)

    // Get all module decisions related to the fetched EC applications
    const moduleDecisionsQuery = useQuery({
        queryKey: ["moduleDecisions", {ecApplicationIds: ecApplicationIds}],
        queryFn: () => getModuleDecisionsByEcApplicationIds(ecApplicationIds),
        enabled: !!ecApplicationIds
    })

    // Fetch all module requests related to the fetched EC applications
    const moduleRequestsQuery = useQuery({
        queryKey: ["moduleRequests", {ecApplicationIds: ecApplicationIds}],
        queryFn: () => getModuleRequestsByEcApplicationIds(ecApplicationIds),
        enabled: !!ecApplicationIds
    })

    // Fetch all students that submitted the fetched EC applications
    const studentIds = Array.from(new Set(ecApplicationsQuery.data?.map(ecApplication => ecApplication.studentId)))
    const studentsQuery = useQuery({
        queryKey: ["users", {ids: studentIds}],
        queryFn: () => getUsersByIds(studentIds),
        enabled: !(studentIds.length == 0)
    }) 

    if (moduleDecisionsQuery.isLoading || ecApplicationsQuery.isLoading || moduleRequestsQuery.isLoading ||studentsQuery.isLoading )
        return (
            <Container className='mt-3'>
              <Row>
              <Col md={{offset: 6 }}>
                <Spinner animation="border" />
              </Col>
              </Row>
            </Container>
        )
    
    if (moduleDecisionsQuery.isError)
        return <h1>Error fetching module decisions: {moduleDecisionsQuery.error.response?.status}</h1>
    
    if (ecApplicationsQuery.isError)
        return <h1>Error fetching EC applications: {ecApplicationsQuery.error.response?.status}</h1>
    
    if (moduleRequestsQuery.isError)
        return <h1>Error fetching module requests: {moduleRequestsQuery.error.response?.status}</h1>
    
    if (studentsQuery.isError)
        return <h1>Error fetching students: {studentsQuery.error.response?.status}</h1>

    const ecApplications = ecApplicationsQuery.data
    const moduleRequests = moduleRequestsQuery.data
    const moduleDecisions = moduleDecisionsQuery.data
    const students = studentsQuery.data
    console.log("EC applications", ecApplications)
    console.log("module requests", moduleRequests)
    console.log("module decisions", moduleDecisions)
    console.log("students", students)


    // Mark any application with requests for deadline extensions or assessment/exam deferment as urgent
    function isEcApplicationUrgent(ecApplicationId) {
            let isUrgent = false
            moduleRequests.forEach(moduleRequest =>{
                if (moduleRequest.ecApplicationId == ecApplicationId && (moduleRequest.requestedOutcome === "Deadline Extension" 
                    || moduleRequest.requestedOutcome == "Defer Formal Examination" || moduleRequest.requestedOutcome == "Defer Formal Assessment")
                ) {
                    isUrgent = true
                    return
                }
            })
    
            return isUrgent
    }

    // Calculate the progress of the EC application
    function calculateProgress(ecApplicationId) {
        // Find module requests that were made as part of this application
        let applicationModuleRequests = []
        moduleRequests.forEach(request =>{
            if (request.ecApplicationId == ecApplicationId)
                applicationModuleRequests.push(request)
        })

        // A final decision needs to be made for each module request
        let finalDecisionsRequired = applicationModuleRequests.length

        let finalDecisionsMade = 0
        applicationModuleRequests.forEach(request => {
            let approvals = 0;
            let rejections = 0;

            moduleDecisions.forEach(decision => {
                if (decision.moduleRequestId == request.id)
                    if (decision.isApproved == true)
                        approvals++
                    else if (decision.isApproved == false)
                        rejections++
            })

            // A decision (i.e. approve or reject) must be shared by 2 staff members for it to be final
            if (approvals >= 2 || rejections >= 2)
                finalDecisionsMade++
        })


        return {finalDecisionsRequired, finalDecisionsMade}
    }

    return (
        <>
        <Container className="mt-3">
            <h2 className="text-center">Extenuating Circumstances Applications</h2>
            <Table striped hover className="mt-3 shadow">
                <thead className="table-light">
                <tr>
                    <th>#</th>
                    <th>Submitted By</th>
                    <th>Submitted On</th>
                    <th>Progress</th>
                    <th>Status</th>
                </tr>
                </thead>
                <tbody className="table-group-divider">
                {ecApplications.map(ecApplication =>{ 
                    let {finalDecisionsRequired, finalDecisionsMade} = calculateProgress(ecApplication.id)
                    let percentage = parseInt(parseFloat(finalDecisionsMade) / finalDecisionsRequired * 100)
                    return (
                        <tr key={ecApplication.id} >
                            <td>{ecApplication.id}</td>
                            <td>{students.find(student => student.id == ecApplication.studentId).name}</td>
                            <td>{formatDate(ecApplication.submittedOn)}</td>
                            <td> <ProgressBar className="mt-1" now={percentage} label={`${percentage}%`} variant={`${percentage == 100 && "success"}`}/> </td>
                            <td>
                                {percentage == 100 ? (
                                    ecApplication.isReferred != null && <Badge bg='success' className="me-1">Closed</Badge>
                                ): (
                                    <>
                                    <Badge bg='primary' className="me-1">Pending Review</Badge>
                                    {ecApplication.requiresFurtherEvidence && <Badge bg='info' className="me-1">Further Evidence Requested</Badge>}
                                    {isEcApplicationUrgent(ecApplication.id) && <Badge bg='danger' className="me-1">Urgent</Badge>}
                                    </>
                                )}
                            </td>
                        </tr>
                    )
                })}
                </tbody>
            </Table>
            </Container>
        </>
    )
}

