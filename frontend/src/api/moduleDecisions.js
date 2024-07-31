import axios from "./axiosConfig"

export function createModuleDecision(data) {
    return axios
    .post(`/module-decisions`, data)
    .then(res => res.data)
}

