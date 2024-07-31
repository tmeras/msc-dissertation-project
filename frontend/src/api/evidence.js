import axios from "./axiosConfig"

export function getEvidenceByEcApplicationId(ecApplicationId) {
    return axios
    .get(`/evidence?ecApplicationId=${ecApplicationId}`)
    .then(res => res.data)
}

export function createEvidence(data) {
    return axios
    .post(`/evidence?ecApplicationId=${data.ecApplicationId}`, data.formData,{
        headers: {
            'Content-Type': 'multipart/form-data',
        },
    })
    .then(res => res.data)
}