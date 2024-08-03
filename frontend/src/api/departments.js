import axios from "./axiosConfig"

export function getDepartments() {
    return axios
    .get(`/departments`)
    .then(res => res.data)
}