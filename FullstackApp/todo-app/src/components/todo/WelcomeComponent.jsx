import { useParams, Link } from 'react-router-dom';
import { useState } from 'react';
import { retreiveHelloWorldPathVariable } from './api/AuthenticationApiService';
import { useAuth } from './security/AuthContext';

function WelcomeComponent() {
	const { username } = useParams();
	const [message, setMessage] = useState(null);
	const authContext = useAuth();
    
    function CallHelloWorldApi() {

        retreiveHelloWorldPathVariable('alekss', authContext.token)
            .then((response) => successfulResponse(response))
            .catch((error) => errorResponse(error))
            .finally(() => console.log('cleanup'));

        function successfulResponse(response) {
            console.log(response);
            setMessage(response.data.message);
        }
        function errorResponse(error) {
            console.log(error);
        }
    }
	return (
		<div className="WelcomeComponent">
			<h1>Welcome {username}</h1>
			<div className="text-info">
				Manage your todos - <Link to="/todos">Go here</Link>
			</div>
			<button className="btn btn-success m-5" onClick={CallHelloWorldApi}>
				Call Hello World
			</button>
			<div className="text-info">{message}</div>
		</div>
	);
}
export default WelcomeComponent;
