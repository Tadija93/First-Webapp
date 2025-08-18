## **Application Configuration**

The following configurations are used to set up the **myfirstwebapp** Spring Boot application:

### **1. Application Details:**
- `spring.application.name=myfirstwebapp`: Defines the name of the Spring Boot application.

### **2. View Resolver Configuration:**
- `spring.mvc.view.prefix=/WEB-INF/jsp/`: Specifies the prefix for JSP views stored in the `/WEB-INF/jsp/` directory.
- `spring.mvc.view.suffix=.jsp`: Specifies the file extension for the JSP views.

### **3. Logging:**
- `logging.level:` Configure the logging level for different packages.

### **4. Date Format:**
- `spring.mvc.format.date`: Specifies the default date format for the application.

### **5. Data Source Configuration:**
- `spring.datasource.url=jdbc:mysql`: Defines the connection URL for the MySQL database.
- `spring.datasource.username`: Specifies the username for the database.
- `spring.datasource.password`: Specifies the password for the database.
- `spring.jpa.defer-datasource-initialization=true`: Ensures that the database initialization is deferred until after JPA has fully started.
- `spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect`: Defines the SQL dialect for Hibernate to use with MySQL 8.
- `spring.jpa.hibernate.ddl-auto=update`: Automatically updates the database schema based on the current JPA entities.

### **6. Server Port:**
- `server.port`: Specify the port on which the application runs.

---
