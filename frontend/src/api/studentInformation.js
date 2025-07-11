import axios from "./axiosConfig"

export function getStudentInformationByStudentId(studentId) {
    return axios
        .get(`/student-information?studentId=${studentId}`)
        .then(res => res.data)
}

export function updateStudentInformation(data) {
    return axios
        .patch(`/student-information/${data.id}`, data)
        .then(res => res.data)
}

export function createStudentInformation(data) {
    return axios
        .post(`/student-information`, data, { headers: { 'Authorization': null } })
        .then(res => res.data)
}

