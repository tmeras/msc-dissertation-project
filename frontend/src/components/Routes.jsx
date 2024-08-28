import { RouterProvider, createBrowserRouter } from "react-router-dom";
import ClericalStaffNavBar from "./clerical_staff/ClericalStaffNavBar";
import ClericalStaffEcApplications from "./clerical_staff/ClericalStaffEcApplications";
import ClericalStaffEcDetails from "./clerical_staff/ClericalStaffEcDetails";
import ErrorPage from "./ErrorPage";
import AcademicStaffNavBar from "./academic_staff/AcademicStaffNavBar";
import AcademicStaffEcApplications from "./academic_staff/AcademicStaffEcApplications"
import AcademicStaffEcDetails from "./academic_staff/AcademicStaffEcDetails"
import StudentNavBar from "./student/StudentNavBar"
import StudentEcApplicationForm from "./student/StudentEcApplicationForm"
import StudentEcApplications from "./student/StudentEcApplications"
import StudentEcDetails from "./student/StudentEcDetails"
import StudentInformation from "./student/StudentInformation"
import AdminNavBar from "./admin/AdminNavbar"
import AdminDepartments from "./admin/AdminDepartments"
import AdminModules from "./admin/AdminModules"
import AdminUsers from "./admin/AdminUsers"
import LoginPage from "./LoginPage";
import RegisterPage from "./RegisterPage"
import ProtectedRoute from "./ProtectedRoute";


export default function Routes() {

    // Define publically accessible routes
    const publicRoutes = [
        {
            path: "/",
            element: <LoginPage />,
            errorElement: <ErrorPage redirectTo="refresh" />
        },
        {
            path: "/login",
            element: <LoginPage />,
            errorElement: <ErrorPage redirectTo="refresh" />
        },
        {
            path: "/register",
            element: <RegisterPage />,
            errorElement: <ErrorPage redirectTo="/login" />
        },
        {
            path: "*",
            element: <ErrorPage errorMessage="404 Not Found" redirectTo="/login" />
        }
    ]

    // Define routes only accessible to clerical staff members
    const clericalStaffRoutes = [
        {
            path: "/clerical-staff",
            element: <ProtectedRoute requiredRole="Clerical_Staff" />,
            errorElement: <ErrorPage redirectTo="/login" />,
            children: [{
                element: <ClericalStaffNavBar />,
                errorElement: <ErrorPage redirectTo="/login" />,
                children: [{
                    errorElement: <ErrorPage />,
                    children: [
                        { index: true, element: <ClericalStaffEcApplications /> },
                        {
                            path: "ec-applications",
                            element: <ClericalStaffEcApplications />
                        },
                        {
                            path: "ec-applications/:id",
                            element: <ClericalStaffEcDetails />
                        }
                    ]
                }]
            }]
        }
    ]

    // Define routes only accessible to clerical staff members
    const academicStaffRoutes = [
        {
            path: "/academic-staff",
            element: <ProtectedRoute requiredRole="Academic_Staff" />,
            errorElement: <ErrorPage redirectTo="/login" />,
            children: [{
                element: <AcademicStaffNavBar />,
                errorElement: <ErrorPage redirectTo="/login" />,
                children: [{
                    errorElement: <ErrorPage />,
                    children: [
                        { index: true, element: <AcademicStaffEcApplications /> },
                        {
                            path: "ec-applications",
                            element: <AcademicStaffEcApplications />
                        },
                        {
                            path: "ec-applications/:id",
                            element: <AcademicStaffEcDetails />
                        }
                    ]
                }]
            }]
        }
    ]

    // Define routes only accessible to administrators
    const adminRoutes = [
        {
            path: "/admin",
            element: <ProtectedRoute requiredRole="Administrator" />,
            errorElement: <ErrorPage redirectTo="/login" />,
            children: [{
                element: <AdminNavBar />,
                errorElement: <ErrorPage redirectTo="/login" />,
                children: [{
                    errorElement: <ErrorPage />,
                    children: [
                        { index: true, element: <AdminUsers /> },
                        {
                            path: "users",
                            element: <AdminUsers />
                        },
                        {
                            path: "departments",
                            element: <AdminDepartments />
                        },
                        {
                            path: "modules",
                            element: <AdminModules />
                        }
                    ]
                }]
            }]
        }
    ]

    // Define routes only accessible to students
    const studentRoutes = [
        {
            path: "/student",
            element: <ProtectedRoute requiredRole="Student" />,
            errorElement: <ErrorPage redirectTo="/login" />,
            children: [{
                element: <StudentNavBar />,
                errorElement: <ErrorPage redirectTo="/login" />,
                children: [{
                    errorElement: <ErrorPage />,
                    children: [
                        { index: true, element: <StudentEcApplications /> },
                        {
                            path: "ec-applications",
                            element: <StudentEcApplications />
                        },
                        {
                            path: "ec-applications/:id",
                            element: <StudentEcDetails />
                        },
                        {
                            path: "information",
                            element: <StudentInformation />
                        },
                        {
                            path: "ec-form",
                            element: <StudentEcApplicationForm />
                        }
                    ]
                }]
            }]
        }
    ]

    // Create router
    const router = createBrowserRouter([
        ...publicRoutes,
        ...clericalStaffRoutes,
        ...academicStaffRoutes,
        ...adminRoutes,
        ...studentRoutes
    ])

    return <RouterProvider router={router} />
}