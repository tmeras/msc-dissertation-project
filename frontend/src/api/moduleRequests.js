import axios from "./axiosConfig"

export function getModuleRequestsByEcApplicationIds(ids) {
    return axios
    .get(`/module-requests?ecApplicationIds?=${ids}`)
    .then(res => res.data)
}


