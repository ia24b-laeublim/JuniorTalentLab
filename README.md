# JuniorTalentLab 🚀

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## 📋 Overview

JuniorTalentLab is a Spring Boot web application designed to manage and support junior talent development programs. This platform facilitates the connection between junior talents and mentors, providing tools for tracking progress, managing projects, and enhancing the learning experience.

## ✨ Features

- **User Management**: Registration and authentication for juniors and mentors
- **Project Tracking**: Monitor progress on learning projects
- **Skill Assessment**: Track and evaluate skill development
- **Resource Library**: Access to learning materials and resources
- **Mentorship Matching**: Connect juniors with appropriate mentors

## 🛠️ Tech Stack

- **Backend**: Java 21, Spring Boot 3.5.0
- **Database**: MySQL
- **Frontend**: Thymeleaf templates
- **Build Tool**: Maven
- **Server**: Apache Tomcat (embedded)

## 🚀 Getting Started

### Prerequisites

- Java Development Kit (JDK) 21 or higher
- Maven 3.6+ (or use the included Maven wrapper)
- MySQL Server

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/JuniorTalentLab.git
   cd JuniorTalentLab
   ```

2. **Configure the database**
   
   Create a MySQL database and update the application.properties file with your database credentials.

3. **Build the application**
   ```bash
   ./mvnw clean install
   ```

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

5. **Access the application**
   
   Open your web browser and navigate to `http://localhost:8080`

## 📝 Configuration

The application can be configured through the `application.properties` file:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/juniortalentlab
spring.datasource.username=yourUsername
spring.datasource.password=yourPassword

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```
