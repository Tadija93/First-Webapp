import axios from "axios";

export const apiClient = axios.create({
    baseURL: 'http://localhost:8082'
});

// Add a request interceptor to dynamically add the token
apiClient.interceptors.request.use(
    (config) => {
        // Get the token from localStorage on every request
        const token = localStorage.getItem('token');
        
        if (token) {
            // Your backend uses Basic Auth format, so use the token as-is
            config.headers.Authorization = token;
        }
        
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Add a response interceptor to handle 401 errors
apiClient.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        if (error.response?.status === 401) {
            // Token is invalid or expired, clear it and redirect to login
            localStorage.removeItem('token');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);