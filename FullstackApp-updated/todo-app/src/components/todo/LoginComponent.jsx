import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from './security/AuthContext';

function LoginComponent() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [showErrorMessage, setShowErrorMessage] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const navigate = useNavigate();
    const authContext = useAuth();

    function handleUsernameChange(event) {
        setUsername(event.target.value);
    }

    function handlePasswordChange(event) {
        setPassword(event.target.value);
    }

    async function handleSubmit() {
        if (!username || !password) {
            setErrorMessage('Please fill in both fields.');
            setShowErrorMessage(true);
            return;
        }

        setShowErrorMessage(false); // Reset error message before login attempt
        try {
            const loginResult = await authContext.login(username, password);
            if (loginResult) {
                navigate(`/welcome/${username}`);
            } else {
                setShowErrorMessage(true);
                setErrorMessage('Invalid credentials. Please try again.');
            }
        } catch (error) {
            console.error('Login error:', error);
            setShowErrorMessage(true);
            setErrorMessage(error.response ? error.response.data : 'Authentication failed.');
        }
    }

    return (
        <div className="Login">
            <h1>Time to Login!</h1>
            {showErrorMessage && <div className="errorMessage">{errorMessage}</div>}
            <div className="LoginForm">
                <div>
                    <input
                        className="mt-1"
                        type="text"
                        placeholder="Username"
                        name="username"
                        value={username}
                        onChange={handleUsernameChange}
                        onKeyDown={(e) => e.key === 'Enter' && handleSubmit()}
                    />
                </div>
                <div>
                    <input
                        className="mt-1"
                        type="password"
                        placeholder="Password"
                        name="password"
                        value={password}
                        onChange={handlePasswordChange}
                        onKeyDown={(e) => e.key === 'Enter' && handleSubmit()}
                    />
                </div>
                <div>
                    <button className="mt-2" type="button" name="login" onClick={handleSubmit}>Login</button>
                </div>
            </div>
        </div>
    );
}

export default LoginComponent;
