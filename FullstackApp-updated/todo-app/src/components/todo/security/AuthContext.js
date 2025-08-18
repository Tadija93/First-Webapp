import { createContext, useContext, useState, useEffect } from "react";
import { executeBasicAuthenticationService } from "../api/AuthenticationApiService";
import { apiClient } from "../api/ApiClient";

// 1: Create a Context
export const AuthContext = createContext();

// Custom hook to access the AuthContext
export const useAuth = () => useContext(AuthContext);

// 2: Share the created context with other components
export default function AuthProvider({ children }) {
    const [isAuthenticated, setAuthenticated] = useState(false);
    const [username, setUsername] = useState(null);
    const [token, setToken] = useState(null);
    const [role, setRole] = useState(null); // Added role state

    // Effect to load user data (username, token, role) from localStorage when the app initializes
    useEffect(() => {
        const savedToken = localStorage.getItem('token');
        const savedUsername = localStorage.getItem('username');
        const savedRole = localStorage.getItem('role');
        

        if (savedToken && savedUsername) {
            setToken(savedToken);
            setUsername(savedUsername);
            setRole(savedRole);
            setAuthenticated(true);
            
            // Add token to the request headers for future API calls
            apiClient.interceptors.request.use((config) => {
                config.headers.Authorization = savedToken;
                return config;
            });
        }
    }, []);

    async function login(username, password) {
        const baToken = 'Basic ' + window.btoa(username + ":" + password);
        console.log("Token before API request:", baToken); // Log the token to check if it's valid
    
        try {
            const response = await executeBasicAuthenticationService(baToken);
            console.log("API response:", response);
    
            if (response.status === 200) {
                setAuthenticated(true);
                setUsername(username);
                setToken(baToken);
    
                // Save to localStorage
                localStorage.setItem('token', baToken);
                localStorage.setItem('username', username);
    
                // Now fetch user details from /users to get the role
                const userResponse = await apiClient.get("/users", {
                    headers: {
                        Authorization: baToken // Use the correct token
                    }
                });
                console.log("User details response:", userResponse);
    
                const user = userResponse.data.find(u => u.username === username);
                if (user) {
                    const role = user.role;
                    setRole(role);
                    console.log("Role from API response:", role);
    
                    // Save role to localStorage
                    localStorage.setItem('role', role);
                } else {
                    console.log("User not found");
                }
    
                // Add interceptor for future requests
                apiClient.interceptors.request.use((config) => {
                    config.headers.Authorization = baToken;
                    return config;
                });
    
                return true;
            } else {
                logout();
                return false;
            }
        } catch (error) {
            console.log("Login error:", error);
            logout();
            return false;
        }
    }
    

    // Logout function
    function logout() {
        setAuthenticated(false);
        setToken(null);
        setUsername(null);
        setRole(null);

        // Remove from localStorage
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        localStorage.removeItem('role');

        // Remove the token from request headers
        apiClient.interceptors.request.eject(apiClient.interceptors.request.handlers[0]);
    }

    // Provide context to children components
    return (
        <AuthContext.Provider value={{ isAuthenticated, login, logout, username, token, role }}>
            {children}
        </AuthContext.Provider>
    );
}
