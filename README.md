# Airbnb Clone – Backend (Spring Boot 4, Java 21)

## Overview
- Backend-only implementation of an Airbnb-like platform
- Focuses on real-world backend engineering concepts
- Implements REST API design, authentication, authorization, booking workflows, inventory management, and payment integration
- Built using Spring Boot 4 with Java 21
- Exposes RESTful APIs consumable by web or mobile clients
- No frontend included; repository strictly focuses on backend development
- All APIs are exposed under the base path: /api/v1

## Key Backend Responsibilities
- Secure user authentication and authorization using JWT
- Property (hotel and room) management
- Availability and inventory tracking
- Booking lifecycle management
- Stripe-based payment processing
- Webhook handling for payment confirmation
- Centralized exception and response handling

## Tech Stack
- Java 21
- Spring Boot 4.x
- Spring Security with JWT authentication
- Spring Data JPA with Hibernate
- Maven
- Relational databases such as MySQL or PostgreSQL
- Stripe API for payments and webhooks

## Core Backend Features

### Authentication and Authorization
- User registration and login
- JWT token generation and validation
- Role-based access control for Admin and User roles
- Secured APIs using Spring Security

### User Management
- APIs for managing user profiles
- Secure access to user-specific resources

### Property and Room Management
- Creation, update, and deletion of hotels
- Admin-only room and inventory management
- Public APIs for fetching hotel data

### Browse and Search
- Public APIs to browse available hotels
- Availability-based filtering of results

### Inventory Management
- Tracking room availability by date
- Prevention of overbooking
- Centralized inventory validation logic

### Booking System
- End-to-end booking workflow
- Date validation and availability checks
- Persistent storage of booking records

### Payments
- Stripe payment intent creation
- Secure payment processing
- Webhook-based payment confirmation handling

### Global Error and Response Handling
- Consistent API response structure
- Centralized exception handling
- Custom error models

## API Design (Sample Endpoints)

### Authentication (`/auth`)
- POST /auth/signup
- POST /auth/login
- POST /auth/refresh

### User (`/users`)
- GET /users/profile
- PATCH /users/profile
- GET /users/myBookings
- GET /users/guests
- POST /users/guests
- PUT /users/guests/{guestId}
- DELETE /users/guests/{guestId}

### Hotel Browse (Public) (`/hotels`)
- GET /hotels/search
- GET /hotels/{hotelId}/info

### Hotel Management (Admin) (`/admin/hotels`)
- POST /admin/hotels
- GET /admin/hotels
- GET /admin/hotels/{hotelId}
- PUT /admin/hotels/{hotelId}
- PATCH /admin/hotels/{hotelId}
- DELETE /admin/hotels/{hotelId}
- GET /admin/hotels/{hotelId}/bookings
- GET /admin/hotels/{hotelId}/hotelReport

### Room Management (Admin) (`/admin/hotels/{hotelId}/rooms`)
- POST /admin/hotels/{hotelId}/rooms
- GET /admin/hotels/{hotelId}/rooms
- GET /admin/hotels/{hotelId}/rooms/{roomId}
- PUT /admin/hotels/{hotelId}/rooms/{roomId}
- DELETE /admin/hotels/{hotelId}/rooms/{roomId}

### Inventory (Admin) (`/admin/inventory`)
- GET /admin/inventory/rooms/{roomId}
- PATCH /admin/inventory/rooms/{roomId}

### Booking (`/bookings`)
- POST /bookings/init
- POST /bookings/{bookingId}/addGuest
- POST /bookings/{bookingId}/payments
- POST /bookings/{bookingId}/cancel
- GET /bookings/{bookingId}/status

### Payments / Webhooks (`/webhook`)
- POST /webhook/payment


## Java and Spring Boot Version
- Built and tested using Java 21
- Uses Spring Boot 4.x
- Maven build configured to use Java version 21

## Running the Project

- Ensure the following are installed:
  - Java 21
  - Maven

- Clone the repository:
```bash
git clone <repository-url>
cd <project-directory>
```
- Run the application using Maven:
```bash
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```
- Build a production-ready JAR:
```bash
./mvnw clean package
```
- Run the generated JAR:
```bash
java -jar target/*.jar
```
- The application will be available at:
  - http://localhost:8080/api/v1


## Environment Variables

- Application
  - SPRING_PROFILES_ACTIVE (e.g., dev, prod)
  - PORT

- Database
  - DB_USERNAME
  - DB_PASSWORD

- Security
  - JWT_SECRET

- Payments
  - STRIPE_API_KEY
  - STRIPE_WEBHOOK_KEY

- Frontend
  - FRONTEND_URL

