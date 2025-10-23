E-Commerce Order Processing System (Spring Boot + JPA + Scheduler)

A backend system for managing customer orders in an e-commerce platform.  
Built using Spring Boot 3**, JPA, H2 Database, ModelMapper, and a configurable **background scheduler.

---

Features

 Create orders with multiple items  
 Retrieve order details by ID  
 List all orders (with optional status filter)  
 Update order status (PENDING → PROCESSING → SHIPPED → DELIVERED)  
 Cancel orders (only if PENDING)  
 Automatic background scheduler to move PENDING → PROCESSING  
 H2 in-memory database for easy testing  
 Configurable scheduler interval via `application.properties`  
 DTO-based mapping using ModelMapper  
 Ready-to-use Postman Collection  

---
Tech Stack

| Layer | Technology |
|-------|-------------|
| Framework | Spring Boot 3.5.x |
| ORM | Spring Data JPA (Hibernate) |
| Database | H2 (In-memory) |
| Mapper | ModelMapper |
| Validation | Jakarta Bean Validation |
| Scheduler | Spring Task Scheduling |
| Testing | JUnit 5, Mockito, MockMvc |
| Build Tool | Maven |


