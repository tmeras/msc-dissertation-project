import axios from "./axiosConfig"

export function getEvidenceByEcApplicationId(ecApplicationId) {
    return axios
    .get(`/evidence?ecApplicationId=${ecApplicationId}`)
    .then(res => res.data)
}

