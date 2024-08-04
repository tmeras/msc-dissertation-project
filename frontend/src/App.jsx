import { formatDate } from "./utils"
import Table from "react-bootstrap/Table"
import { Badge, Container } from 'react-bootstrap'
import { useQuery, useMutation } from '@tanstack/react-query'
import { getEcApplications } from './api/ecApplications'
import AuthProvider from "./providers/AuthProvider"
import ClericalStaffEcApplications from "./components/clerical_staff/ClericalStaffEcApplications"
import ClericalStaffNavBar from "./components/clerical_staff/ClericalStaffNavBar"
import ClericalStaffEcDetails from "./components/clerical_staff/ClericalStaffEcDetails"
import StudentEcApplicationForm from "./components/student/StudentEcApplicationForm"
import AcademicStaffEcApplications from "./components/academic_staff/AcademicStaffEcApplications"
import AcademicStaffEcDetails from "./components/academic_staff/AcademicStaffEcDetails"
import StudentInformation from "./components/student/StudentInformation"
import StudentEcApplications from "./components/student/StudentEcApplications"
import StudentEcDetails from "./components/student/StudentEcDetails"
import AdminUsers from "./components/admin/AdminUsers"
import AdminModules from "./components/admin/AdminModules"
import AdminDepartments from "./components/admin/AdminDepartments"

export default function App() {

  return (
    <>
      <AuthProvider>
        <ClericalStaffNavBar />
        {/* <ClericalStaffEcApplications /> */}
        {/* <ClericalStaffEcDetails /> */}
        {/* <AcademicStaffEcApplications /> */}
        {/* <AcademicStaffEcDetails /> */}
        <StudentEcApplicationForm />
        {/* <StudentEcApplications /> */}
        {/* <StudentEcDetails /> */}
        {/* <StudentInformation /> */}
        {/* <AdminUsers /> */}
        {/* <AdminModules /> */}
        {/* <AdminDepartments /> */}
      </AuthProvider>
    </>
  )
}

