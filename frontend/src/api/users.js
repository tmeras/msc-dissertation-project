import axios from "./axiosConfig"

export function getUsers() {
    return axios
        .get(`/users`)
        .then(res => res.data)
}

export function getUsersByIds(ids) {
    return axios
        .get(`/users?ids=${ids}`)
        .then(res => res.data)
}

export function getUser(id) {
    return axios
        .get(`/users/${id}`)
        .then(res => res.data)
}

export function getUsersByDepartmentIdAndRoleId(departmentId, roleId) {
    return axios
        .get(`/users?departmentId=${departmentId}&roleId=${roleId}`)
        .then(res => res.data)
}

export function updateUser(data) {
    return axios
        .patch(`/users/${data.id}`, data)
        .then(res => res.data)
}

export function emailUser(data) {
    return axios
        .post(`/users/${data.id}/mail`, data)
        .then(res => res.data)
}

