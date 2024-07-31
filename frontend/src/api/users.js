import axios from "./axiosConfig"

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

export function getUserByDepartmentIdAndRoleId(departmentId, roleId) {
    return axios
    .get(`/users?departmentId=${departmentId}&roleId=${roleId}`)
    .then(res => res.data)
}

