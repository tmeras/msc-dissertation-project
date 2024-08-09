import { formatDate } from "../../utils"
import Table from "react-bootstrap/Table"
import { Badge, Container, Spinner, Row, Col, ProgressBar } from 'react-bootstrap'
import { useQuery, useMutation } from '@tanstack/react-query'
import { useAuth } from "../../providers/AuthProvider"
import { getEcApplications, getEcApplicationsByIds, getEcApplicationsByStudentDepartmentId, getEcApplicationsByStudentDepartmentIdAndIsReferred } from '../../api/ecApplications'
import { getModuleRequestsByEcApplicationIds } from "../../api/moduleRequests"
import { getUsersByIds } from "../../api/users"
import { getModuleDecisionsByEcApplicationIds, getModuleDecisionsByStaffMemberId } from "../../api/moduleDecisions"
import { useNavigate } from "react-router"
import ErrorPage from "../ErrorPage"


export default function AcademicStaffEcApplications() {
    const {user} = useAuth()
    const navigate = useNavigate()

    console.log(user)

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
        return <ErrorPage 
                    errorTitle={`when fetching module decisions`}
                    errorMessage={`${moduleDecisionsQuery.error.code}
                    | Server Response: ${moduleDecisionsQuery.error.response?.data.status}-${moduleDecisionsQuery.error.response?.data.error}`} 
                />      

    if (ecApplicationsQuery.isError)
        return <ErrorPage 
                    errorTitle={`when fetching EC applications`}
                    errorMessage={`${ecApplicationsQuery.error.code}
                    | Server Response: ${ecApplicationsQuery.error.response?.data.status}-${ecApplicationsQuery.error.response?.data.error}`} 
                />      
    
    if (moduleRequestsQuery.isError)
        return <ErrorPage 
                    errorTitle={`when fetching module requests`}
                    errorMessage={`${moduleRequestsQuery.error.code}
                    | Server Response: ${moduleRequestsQuery.error.response?.data.status}-${moduleRequestsQuery.error.response?.data.error}`} 
                />      
        
    if (studentsQuery.isError)
        return <ErrorPage 
                    errorTitle={`when fetching students`}
                    errorMessage={`${studentsQuery.error.code}
                    | Server Response: ${studentsQuery.error.response?.data.status}-${studentsQuery.error.response?.data.error}`} 
                />      
        
    const ecApplications = ecApplicationsQuery.data
    const moduleRequests = moduleRequestsQuery.data
    const moduleDecisions = moduleDecisionsQuery.data
    const students = studentsQuery.data

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
            <h2 className="text-center fw-normal">Extenuating Circumstances Applications</h2>
            <Table striped hover className="mt-3 shadow">
                <thead className="table-light">
                <tr>
                    <th scope="col" className="col-1">#</th>
                    <th scope="col" className="col-2">Submitted By</th>
                    <th scope="col" className="col-2" >Submitted On</th>
                    <th scope="col" className="col-3">Progress</th>
                    <th>Status</th>
                </tr>
                </thead>
                <tbody className="table-group-divider">
                {ecApplications.sort((a, b) => a.id - b.id).map(ecApplication =>{ 
                    let {finalDecisionsRequired, finalDecisionsMade} = calculateProgress(ecApplication.id)
                    let percentage = parseInt(parseFloat(finalDecisionsMade) / finalDecisionsRequired * 100)

                    return (
                        <tr
                            key={ecApplication.id} 
                            onClick={() => navigate(`/academic-staff/ec-applications/${ecApplication.id}`)} 
                            style={{cursor: "pointer"}}
                        >
                            <td>{ecApplication.id}</td>
                            <td>{students.find(student => student.id == ecApplication.studentId).name}</td>
                            <td>{formatDate(ecApplication.submittedOn)}</td>
                            <td> <ProgressBar className="mt-1" now={percentage} label={`${percentage}%`} variant={`${percentage == 100 && "success"}`}/> </td>
                            <td>
                                {percentage == 100 ? (
                                    <Badge bg='success' className="me-1">Closed</Badge>
                                ): (
                                    <>
                                    <Badge bg='primary' className="me-1">Pending review</Badge>
                                    {ecApplication.requiresFurtherEvidence && <Badge bg='info' className="me-1">Further evidence requested</Badge>}
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

