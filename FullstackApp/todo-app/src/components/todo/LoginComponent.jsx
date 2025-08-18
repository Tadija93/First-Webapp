import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from './security/AuthContext'

function LoginComponent() {
    const [username, setUsername] = useState('')

    const [password, setPassword] = useState('')

    //const [showSuccessMessage, setShowSuccessMessage] = useState(false)

    const [showErrorMessage, setShowErrorMessage] = useState(false)

    const navigate = useNavigate();
    const authContext = useAuth()



    function handleUsernameChange(event) {
        setUsername(event.target.value)
    }

    function handlePasswordChange(event) {
        setPassword(event.target.value)
    }

    async function handleSubmit() {
        try {
            const loginResult = await authContext.login(username, password);
            if (loginResult) {
                navigate(`/welcome/${username}`);
            } else {
                setShowErrorMessage(true);
            }
        } catch (error) {
            console.error('Login error:', error);
            setShowErrorMessage(true);
        }
    }
    
    return (
        <div className="Login">
            <h1>Time to Login!</h1>
            {/*showSuccessMessage && <div className="successMessage"> This is ok.
               </div>*/}
            {showErrorMessage && <div className="errorMessage">Authentication Failed. 
                    Please check your credentials.</div>}
            <div className="LoginForm">
                <div>
                    <label>User Name:</label>
                    <input type="text" name="username" value={username} onChange={handleUsernameChange}/>
                </div>
                <div>
                    <label>Password:</label>
                    <input type="password" name="password" value={password} onChange={handlePasswordChange}/>
                </div>
                <div>
                    <button type="button" name="login" onClick={handleSubmit}>login</button>
                </div>
            </div>
        </div>
    )
}
export default LoginComponent
