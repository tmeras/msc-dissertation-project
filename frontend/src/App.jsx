import { formatDate } from "./utils"
import Table from "react-bootstrap/Table"
import { Badge, Container } from 'react-bootstrap'
import { useQuery, useMutation } from '@tanstack/react-query'
import { getEcApplications } from './api/ecApplications'
import AuthProvider from "./providers/AuthProvider"
import ClericalStaffEcApplications from "./components/clerical_staff/ClericalStaffEcApplications"
import ClericalStaffNavBar from "./components/clerical_staff/ClericalStaffNavBar"
import ClericalStaffEcDetails from "./components/clerical_staff/ClericalStaffEcDetails"

export default function App() {

  return (
    <>
      <AuthProvider>
        <ClericalStaffNavBar />
        {/* <ClericalStaffEcApplications /> */}
        <ClericalStaffEcDetails />
      </AuthProvider>
    </>
  )
}

