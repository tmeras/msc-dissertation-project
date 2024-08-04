import axios from "./axiosConfig"

export function getDepartments() {
    return axios
    .get(`/departments`)
    .then(res => res.data)
}

export function createDepartment(data) {
    return axios
    .post(`/departments`, data)
    .then(res => res.data)
}

export function updateDepartment(data) {
    return axios
    .patch(`/departments/${data.id}`, data)
    .then(res => res.data)
}

