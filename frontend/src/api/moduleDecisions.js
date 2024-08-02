import axios from "./axiosConfig"

export function createModuleDecision(data) {
    return axios
    .post(`/module-decisions`, data)
    .then(res => res.data)
}

export function getModuleDecisionsByStaffMemberId(staffMemberId) {
    return axios
    .get(`/module-decisions?staffMemberId=${staffMemberId}`)
    .then(res => res.data)
}

export function getModuleDecisionsByEcApplicationIds(ecApplicationIds) {
    return axios
    .get(`/module-decisions?ecApplicationIds=${ecApplicationIds}`)
    .then(res => res.data)
}
