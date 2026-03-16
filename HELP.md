# Coupon Management API

A Spring Boot + MySQL based REST API for managing and applying coupons for an e-commerce platform.

## Features
- Coupon CRUD APIs
- Coupon types:
    - Cart-wise
    - Product-wise
    - BxGy
- Applicable coupon calculation
- Apply a specific coupon on cart
- Expiration date support
- Basic validation and error handling
- Strategy pattern for future extensibility

---

## Tech Stack
- Java 17
- Spring Boot 3
- Spring Data JPA
- MySQL
- Maven

---

## How to Run

### 1. Create database
```sql
CREATE DATABASE coupon_db;