import axios from "./axiosConfig"

export function registerUser(data) {
    return axios
    .post(`/auth/register`, data)
    .then(res => res.data)
}

