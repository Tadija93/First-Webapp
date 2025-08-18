# Todo Application (Full Stack)

## Overview

This project consists of two main parts:

1. **Frontend** – A React-based application located in the `todo-app` folder.
2. **Backend** – A Spring Boot RESTful API located in the `restfull-api-web-services` folder.
3. **Database** – A MySQL instance managed via Docker Compose.

## Prerequisites

Make sure you have the following installed on your machine:

- **Node.js** (for the React frontend)
- **Java Development Kit (JDK 17+)** (for Spring Boot backend)
- **Maven** (for building the backend)
- **IntelliJ IDEA** or another Java IDE (for backend development)
- **Docker Desktop & Docker Compose** (for the MySQL database)

---

## Project Structure

```plaintext
.
├── docker-compose.yml            # Database setup
├── restfull-api-web-services/    # Spring Boot backend
└── todo-app/                     # React frontend
```

## Instructions

### 🐬 Setting up the MySQL Database (Docker)

1. Make sure Docker is installed and running.
2. In the project root, you can find a file named `docker-compose.yml`
3. Start the MySQL container:

```bash
docker-compose up -d
```

4. The database will be accessible at `localhost:3307`.

---

### 🚀 Starting the Backend (Spring Boot)

1. Open the `restfull-api-web-services` folder in IntelliJ IDEA or your IDE of choice.
2. Make sure `application.properties` is configured to use the database:

```properties
spring.datasource.url=jdbc:mysql://localhost:3307/todos
spring.datasource.username=aleks
spring.datasource.password=test
spring.jpa.hibernate.ddl-auto=update
```

3. Run the main application class (`RestfullApiWebServicesApplication`).
4. The backend should now be running at `http://localhost:8082`.

#### 🔐 Basic Authentication

Use the following credentials:

- **Username:** `aleks`
- **Password:** `test`

#### 🔗 Backend Endpoints

Example endpoint to test:

```bash
GET http://localhost:8082/users/aleks/todos
```

---

### 🌐 Starting the Frontend (React)

1. Navigate to the frontend folder:

```bash
cd todo-app
```

2. Install dependencies:

```bash
npm install
```

3. Start the development server:

```bash
npm start
```

4. Open your browser and go to: [http://localhost:3000](http://localhost:3000)

---

## 🧪 Testing the Application

1. Open the frontend in your browser.
2. Log in using the default credentials:

   - **Username:** `aleks`
   - **Password:** `test`

3. You can now create, update, and delete todo items.

---

## 🔧 Changing Default Credentials

To change the credentials used for login and API access:

### Backend

1. Open `application.properties` in the backend.
2. Modify the username/password values for security config (depending on implementation).

### Frontend

1. Update the login credentials (usually located in `LoginComponent.jsx` or a similar file).
2. Rebuild and restart the frontend app.

---

## 🧑‍💻 Contact

**Author:** Aleksandar Tadic
**GitHub:** [github.com/Tadija93](https://github.com/Tadija93)  
**Email:** aleksandartadic93@yahoo.com
