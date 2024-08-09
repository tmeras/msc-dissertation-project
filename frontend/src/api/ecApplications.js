import axios from "./axiosConfig"

export function getEcApplications() {
    return axios
        .get(`/ec-applications`)
        .then(res => res.data)
}

export function getEcApplicationsByStudentDepartmentId(departmentId) {
    return axios
        .get(`/ec-applications?studentDepartmentId=${departmentId}`)
        .then(res => res.data)
}

export function getEcApplicationsByStudentDepartmentIdAndIsReferred(data) {
    return axios
        .get(`/ec-applications?studentDepartmentId=${data.studentDepartmentId}&isReferred=${data.isReferred}`)
        .then(res => res.data)
}

export function getEcApplicationsByStudentId(studentId) {
    return axios
        .get(`/ec-applications?studentId=${studentId}`)
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

export function createEcApplication(data) {
    return axios
        .post(`/ec-applications`, data)
        .then(res => res.data)
}
