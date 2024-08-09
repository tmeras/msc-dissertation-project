import axios from "./axiosConfig"

export function getRoleByName(roleName) {
    return axios
        .get(`/roles?name=${roleName}`)
        .then(res => res.data)
}

export function getRoles() {
    return axios
        .get(`/roles`, { headers: { 'Authorization': null } })
        .then(res => res.data)
}

