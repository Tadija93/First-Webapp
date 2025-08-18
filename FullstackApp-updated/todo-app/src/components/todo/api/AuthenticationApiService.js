import { apiClient } from "./ApiClient"

//export function retreiveHelloWorldBean() {
//    return apiClient.get('hello-world-bean')
//

//export const retreiveHelloWorld =
   // () => apiClient.get('/hello-world')

export const retreiveHelloWorldPathVariable =
    (username, token) => apiClient.get(`/hello-world/path-variable/${username}`,{
        headers: {
            Authorization: token
        }
    })

export const executeBasicAuthenticationService
    = (token) => apiClient.get(`/basicauth`,{
        headers: {
            Authorization: token
        }
    })

    