import axios from "./axiosConfig"

export function registerUser(data) {
    return axios
        .post(`/auth/register`, data, { headers: { 'Authorization': null } })
        .then(res => res.data)
}

