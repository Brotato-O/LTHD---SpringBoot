import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8080",
    headers: {
        "Content-Type": "application/json",
    },
});

const savedToken = localStorage.getItem("token");
if (savedToken) {
    api.defaults.headers.common.Authorization = "Bearer " + savedToken;
}

api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem("token");

        if (token) {
            const authorization = "Bearer " + token;
            config.headers.Authorization = authorization;
        }

        return config;
    },
    (error) => Promise.reject(error)
);

api.interceptors.response.use((response) => {
    if (
        response.data &&
        typeof response.data === "object" &&
        Object.prototype.hasOwnProperty.call(response.data, "data")
    ) {
        response.data = response.data.data;
    }

    return response;
}, (error) => {
    if (error.response?.status === 401) {
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        window.dispatchEvent(new Event("auth:expired"));
    }

    return Promise.reject(error);
});

export default api;
