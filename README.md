# 🚀 Job Portal Management System

A RESTful Job Portal Management System built using **Spring Boot**, **Spring Data JPA**, **Hibernate**, and **MySQL**. This project allows companies to manage job postings through REST APIs.

## 📌 Features

* Manage Company details
* Post new Job Openings
* Search Companies by Industry & Location
* Search Jobs by Location
* Delete Jobs based on Company & Job Type
* Update Job Salary
* Search Jobs by Industry & Minimum Salary
* Update Job Status (AVAILABLE / UNAVAILABLE)
* Delete Company Details

---

# 🛠️ Tech Stack

* Java 17+ (Compatible with Java 24)
* Spring Boot
* Spring Data JPA
* Hibernate ORM
* MySQL
* Lombok
* ModelMapper
* Maven
* Postman (API Testing)

---

# 📂 Project Structure

```
src
├── main
│   ├── java
│   │   └── com.backend
│   │       ├── controller
│   │       ├── service
│   │       ├── repository
│   │       ├── entites
│   │       ├── dtos
│   │       ├── custom_exception
│   │       ├── config
│   │       └── Application.java
│   └── resources
│       └── application.properties
```

---

# 🗄️ Database Design

## Company

| Column    | Type      |
| --------- | --------- |
| id        | Long      |
| name      | String    |
| email     | String    |
| location  | String    |
| industry  | Enum      |
| createdOn | LocalDate |

---

## Job

| Column      | Type        |
| ----------- | ----------- |
| id          | Long        |
| title       | String      |
| description | String      |
| salary      | Double      |
| location    | String      |
| jobType     | Enum        |
| postedDate  | LocalDate   |
| status      | Enum        |
| company     | Many-to-One |

---

# 🔗 Entity Relationship

```
Company (1)
      │
      │
      │
      ▼
Job (Many)
```

---

# 📌 REST APIs

## Company APIs

### 1. Get Companies by Industry & Location

```
GET /companies/{industry}/{location}
```

---

### 2. Delete Company

```
DELETE /companies/{companyId}
```

---

## Job APIs

### 1. Post New Job

```
POST /jobs
```

Request Body

```json
{
    "companyId":1,
    "title":"Java Developer",
    "description":"Spring Boot Developer",
    "salary":900000,
    "location":"Pune",
    "jobType":"FULL_TIME"
}
```

---

### 2. Get Jobs by Location

```
GET /jobs/location/{location}
```

---

### 3. Delete Jobs by Company & Job Type

```
DELETE /jobs/{companyName}/{jobType}
```

---

### 4. Update Job Salary

```
PUT /jobs/{companyId}/{title}
```

Request Body

```json
{
    "salary":1200000
}
```

---

### 5. Get Jobs by Industry & Minimum Salary

```
GET /jobs/industry/{industry}/salary/{salary}
```

---

### 6. Update Job Status

```
PUT /jobs/{companyName}/{title}/status
```

---

# ⚙️ How to Run

### Clone Repository

```bash
git clone https://github.com/your-username/job-portal-management.git
```

---

### Configure Database

Update `application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/job_portal
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
```

---

### Run Application

```bash
mvn spring-boot:run
```

or run `Application.java` from your IDE.

---

# 🧪 API Testing

You can test all REST APIs using:

* Postman
* Swagger (if added)

---

# 📚 Spring Boot Concepts Used

* REST API Development
* Spring Boot
* Spring Data JPA
* Hibernate ORM
* Entity Relationships (`@ManyToOne`)
* DTO Pattern
* ModelMapper
* Layered Architecture
* Exception Handling
* Transactions (`@Transactional`)
* Hibernate Dirty Checking
* JPQL & Derived Query Methods
* ResponseEntity
* Lombok

---

# 🎯 Learning Outcome

This project demonstrates:

* Building RESTful APIs using Spring Boot
* Database operations using Spring Data JPA
* Entity relationship mapping
* DTO-based response handling
* Exception handling
* CRUD operations
* Repository query methods
* JPQL queries
* Layered Architecture (Controller → Service → Repository)

---

# 👨‍💻 Author

**Ajay Pal**

If you found this project helpful, feel free to ⭐ the repository.
