import axios from "./axiosConfig"

export function getModulesByCodes(moduleCodes) {
    return axios
    .get(`/modules?codes=${moduleCodes}`)
    .then(res => res.data)
}

export function getModules() {
    return axios
    .get(`/modules`)
    .then(res => res.data)
}

export function createModule(data) {
    return axios
    .post(`/modules`, data)
    .then(res => res.data)
}

export function updateModule(data) {
    return axios
    .patch(`/modules/${data.code}`, data)
    .then(res => res.data)
}

