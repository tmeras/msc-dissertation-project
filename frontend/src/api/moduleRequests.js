import axios from "./axiosConfig"

export function getModuleRequestsByEcApplicationIds(ids) {
    return axios
    .get(`/module-requests?ecApplicationIds=${ids}`)
    .then(res => res.data)
}

export function createModuleRequest(data) {
    return axios
    .post(`/module-requests`, data)
    .then(res => res.data)
}
