# README

## Overview
This project consists of two parts:
1. **Frontend** - A React-based application located in the `todo-app` folder.
2. **Backend** - A Spring Boot RESTful API located in the `restfull-api-web-services` folder.

## Prerequisites
Make sure you have the following installed on your machine:
- **Node.js** for the frontend.
- **IntelliJ IDEA** or another Java IDE for the backend.
- **Java Development Kit (JDK)** for running the Spring Boot application.

## Instructions

### Starting the Frontend
1. Navigate to the `todo-app` folder:
   ```bash
   cd todo-app
   ```
2. Start the frontend application:
   ```bash
   npm start
   ```

### Starting the Backend
1. Open the `restfull-api-web-services` folder in IntelliJ IDEA.
2. Run the main class to start the backend server.
3. The backend will start on `http://localhost:8082`.

## Testing the Application

### Backend Endpoints
Once the backend is running, you can check the following endpoint to view the list of todos:
- `http://localhost:8082/users/aleks/todos`

The backend is protected with Basic Authentication. Use the following default credentials:
- **Username:** `aleks`
- **Password:** `test`

### Frontend Functionality
1. Access the frontend in your browser (it typically runs on `http://localhost:3000`).
2. Log in using the default credentials:
   - **Username:** `aleks`
   - **Password:** `test`
3. Once logged in, you can access all the application's functionalities.

### Changing Default Credentials
To change the default credentials for both the backend and frontend:
1. Open the `application.properties` file in the Spring Boot project.
2. Update the values for the username and password.
3. Save the changes and restart the backend.

---


