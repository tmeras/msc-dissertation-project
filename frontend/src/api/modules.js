import axios from "./axiosConfig"

export function getModulesByCodes(moduleCodes) {
    return axios
    .get(`/modules?codes=${moduleCodes}`)
    .then(res => res.data)
}

