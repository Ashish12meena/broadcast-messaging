# Services Project

A modern, scalable backend service built with **Spring Boot 3**, supporting REST, GraphQL, MongoDB, JPA, and robust security.  
Author: Ashish Meena

---

## 🚀 Features

- **Spring Boot 3.5.3**: Rapid development with the latest Spring ecosystem.
- **REST & GraphQL APIs**: Flexible data access for modern clients.
- **MongoDB & JPA**: Hybrid persistence with both NoSQL and relational databases.
- **Spring Security**: Secure endpoints with customizable authentication and authorization.
- **Actuator**: Production-ready monitoring and management endpoints.
- **Environment Profiles**: Seamless configuration for development and production.

---

## 📁 Project Structure
. ├── src/ │ ├── main/ │ │ ├── java/com/ # Application source code │ │ └── resources/ │ │ ├── application-dev.properties │ │ ├── application-prod.properties │ │ └── ... │ └── test/ │ └── java/com/ # Unit and integration tests ├── .mvn/wrapper/ # Maven Wrapper scripts ├── pom.xml # Maven project configuration └── README.md # Project documentation


---

## 🛠️ Getting Started

### Prerequisites

- Java 17+
- Maven 3.9.x (or use the provided Maven Wrapper)

### Build & Run

```sh
# Build the project
./mvnw clean install

# Run the application (dev profile)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

./mvnw test

📡 API Endpoints
REST: /api/...
GraphQL: /graphql (query via POST)

📊 Monitoring
Spring Boot Actuator endpoints are available at /actuator/* for health checks and metrics.

🤝 Contributing
Fork the repository
Create your feature branch (git checkout -b feature/YourFeature)
Commit your changes
Push to the branch
Open a Pull Request

📄 License
This project is licensed under the Apache License 2.0.


🙋‍♂️ Author
Ashish Meena
