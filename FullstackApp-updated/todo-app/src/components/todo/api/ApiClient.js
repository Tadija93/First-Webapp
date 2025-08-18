import axios from "axios";

export const apiClient = axios.create(
    {
    baseURL: 'http://localhost:8082'
    }
);

const token = localStorage.getItem('token');
if (token) {
    apiClient.defaults.headers.common['Authorization'] = token;
}
