import axios from "./axiosConfig"

export function getStudentInformationByStudentId(studentId) {
    return axios
    .get(`/student-information?studentId=${studentId}`)
    .then(res => res.data)
}

