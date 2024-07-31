import axios from "./axiosConfig"

export function getRoleByName(roleName) {
    return axios
    .get(`/roles?name=${roleName}`)
    .then(res => res.data)
}

