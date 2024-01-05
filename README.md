# Chat Engine Application

## Overview

Chat Engine is a real-time chat application that enables users to register, log in, and engage in live chat conversations. The application leverages a serverless architecture model, utilizing state-of-the-art web development technologies.

## Technologies

This project uses the following key technologies:

- **Spring Boot**: Simplifies the bootstrapping and development of new Spring applications.
- **Spring Security**: Provides authentication and authorization services.
- **Spring Data JPA**: Facilitates the integration of JPA-based repositories.
- **Spring WebSocket**: Manages WebSocket communication.
- **Thymeleaf**: Serves as a server-side Java template engine.
- **Lombok**: Reduces boilerplate code in Java.
- **MySQL**: Acts as the relational database to store user and chat data.
- **Maven**: Handles project build and dependency management.

## Configuration

The `application.properties` file contains configurable settings for the database connection, such as the URL, username, and password, as well as application-specific settings like the maximum number of chat messages to retain in history. You can also adjust the server's port and context path if needed:

```properties
# Example of database configuration
spring.datasource.url=jdbc:mysql://localhost:3306/chat_engine
spring.datasource.username=root
spring.datasource.password=yourpassword

# Server port configuration
server.port=8080

Before Start:
1. Ensure you have Java 17 installed on your machine.
2. A MySQL instance should be up and running on `localhost:3306` (configurable in `application.properties`).
3. Setup MySQL database configuration in application.properties

Using the Application:
Run ChatingApplication.java to launching the application
After launching the application:
Navigate to http://localhost:8080/register to create a new user account.
Once registered, go to http://localhost:8080/login to log in to the chat application.
After logging in, you will be directed to the chat interface where you can start messaging other users in real-time.

Customization
You can customize the server's IP address and port in the application.properties file if you want to run the application on a different server or port.