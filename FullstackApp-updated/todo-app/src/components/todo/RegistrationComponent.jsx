import React, { useState } from "react";
import axios from "axios";

const Register = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [role, setRole] = useState("");
    const [message, setMessage] = useState("");
    const [error, setError] = useState("");

    const handleRegister = async () => {
        // Validation: Check if fields are empty
        if (!username || !password || !role) {
            setError("All fields are required.");
            setMessage("");
            return;
        }

        // Reset error message if fields are filled
        setError("");

        try {
            const response = await axios.post("http://localhost:8082/register", {
                username,
                password,
                role,
            });
            setMessage("User registered successfully!");
            setError("");

            // Clear input fields after successful registration
            setUsername("");
            setPassword("");
            setRole("");
        } catch (error) {
            setMessage(error.response ? error.response.data : "Registration failed");
        }
    };

    return (
        <div className="flex min-h-screen items-center justify-center bg-gray-100">
            <div className="bg-white p-8 rounded-lg shadow-lg w-96">
                <h2 className="text-2xl font-semibold text-center mb-4">Register</h2>

                {/* Show error message if validation fails */}
                {error && <p className="text-center text-red-600 mb-3">{error}</p>}
                
                {/* Show success or server error message */}
                {message && <p className="text-center text-green-600 mb-3">{message}</p>}

                <div className="space-y-4">
                    <div>
                        <input
                            type="text"
                            placeholder="Username"
                            className="w-full p-2 border rounded-lg focus:ring-2 focus:ring-blue-500 mt-1"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                        />
                    </div>
                    <div>
                        <input
                            type="password"
                            placeholder="Password"
                            className="w-full p-2 border rounded-lg focus:ring-2 focus:ring-blue-500 mt-1"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                        />
                    </div>
                    <div>
                        <input
                            type="text"
                            placeholder="Role (USER / ADMIN)"
                            className="w-full p-2 border rounded-lg focus:ring-2 focus:ring-blue-500 mt-1"
                            value={role}
                            onChange={(e) => setRole(e.target.value)}
                        />
                    </div>
                    <button 
                        className="mt-2"
                        onClick={handleRegister}
                    >
                        Register
                    </button>
                </div>
            </div>
        </div>
    );
};

export default Register;
