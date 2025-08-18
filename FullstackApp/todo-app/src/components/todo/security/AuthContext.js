import { createContext, useContext, useState } from "react";
import { executeBasicAuthenticationService } from "../api/AuthenticationApiService";
import { apiClient } from "../api/ApiClient";


//1: Create a Context
export const AuthContext = createContext()

export const useAuth = () => useContext(AuthContext)

//2: Share the created context with other components
export default function AuthProvider({ children }) {

    //Put some state in the context
   // const [number, setNumber] = useState(10)

    const [isAuthenticated, setAuthenticated] = useState(false)
    
    const [username, setUsername] = useState(false)
    const [token, setToken] = useState(false)

    //setInterval( () => setNumber(number+1), 10000)

    //const valueToBeShared = {number, isAuthenticated, setAuthenticated}

    /*function login(username, password) {
        if(username==='aleks' && password==='test'){
            setAuthenticated(true)
            setUsername(username)
            return true            
        } else {
            setAuthenticated(false)
            setUsername(null)
            return false
        }        
    }*/

    async function login(username, password) {

        const baToken = 'Basic ' + window.btoa( username + ":" + password )

        const response = await executeBasicAuthenticationService(baToken)
        try{
            if(response.status===200){
                setAuthenticated(true)
                setUsername(username)
                setToken(baToken)

                apiClient.interceptors.request.use(
                    (config) => {
                        console.log("interecept and add token")
                        config.headers.Authorization = baToken
                        return config
                    }
                )
                return true            
            } else {
                logout()
                return false
            }
        }catch (error) {
            logout()
            return false
        }    
    }
    function logout() {
        setAuthenticated(false)
        setToken(false)
        setUsername(false)
    }


    return (
        <AuthContext.Provider value={ {isAuthenticated, login, logout, username, token}  }>
            {children}
        </AuthContext.Provider>
    )

}