import axios from "./axiosConfig"

export function getEcApplications() {
    return axios
    .get("/ec-applications")
    .then(res => res.data)
}

export function getEcApplicationsByStudentDepartmentId(departmentId) {
    return axios
    .get("/ec-applications?studentDepartmentId=" + departmentId)
    .then(res => res.data)
}

export function getEcApplicationsByIds(ids) {
    return axios
    .get(`/ec-applications?ids=${ids}`)
    .then(res => res.data)
}

export function getEcApplication(id) {
    return axios
    .get(`/ec-applications/${id}`)
    .then(res => res.data)
}

export function updateEcApplication(data) {
    return axios
    .patch(`/ec-applications/${data.id}`, data)
    .then(res => res.data)
}


