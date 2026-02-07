
# ☕ JavaBite: Coffee Hub Management System

> A comprehensive full-stack coffee shop management system with role-based access control, real-time order tracking, and table booking functionality.

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0+-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18+-blue.svg)](https://reactjs.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

---

## 📖 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Screenshots](#screenshots)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Installation](#installation)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Contributing](#contributing)
- [Author](#author)

---

##  Overview

JavaBite is a production-ready coffee shop management system designed for Infosys Springboard Virtual Internship 6.0. It provides a complete solution for managing orders, staff, inventory, table bookings, and customer feedback with four distinct role-based dashboards.

### Key Highlights

- **4 Role-Based Dashboards**: Customer, Chef, Waiter, and Admin interfaces
- **Real-Time Order Management**: Automatic chef-waiter assignment with 5-state order tracking
- **Secure Authentication**: JWT-based authentication with BCrypt password hashing
- **Table Booking System**: 20 tables with real-time availability and conflict prevention
- **Customer Feedback**: Rating system with analytics and insights

---

##  Features

### 🔐 Authentication & Authorization
- JWT token-based authentication
- BCrypt password encryption
- Role-based access control (RBAC)
- Protected API endpoints using Spring Security
- Session management

### 👥 Customer Features
- Browse menu by category (Coffee, Pastries, Beverages)
- Add items to cart with quantity selection
- Place orders with automatic table assignment
- Real-time table booking with date/time selection
- View order history (current and past orders)
- Provide feedback and ratings
- View order status tracking

### 👨‍🍳 Chef Dashboard
- View assigned orders in real-time
- Filter orders by status (New, In Progress, Completed)
- Mark orders as "Ready" when prepared
- Track daily preparation metrics
- Order details with customer and table information

### 🍽️ Waiter Dashboard
- Monitor "Being Prepared" and "Ready to Serve" orders
- View assigned tables and orders
- Mark orders as "Served" when delivered
- Real-time updates on order status
- Customer and chef assignment visibility

### 👤 Admin Panel
- **Dashboard Analytics**
  - Total revenue tracking
  - Order statistics (Pending, Preparing, Ready, Completed)
  - Today's sales metrics
  - Active staff monitoring

- **Order Management**
  - View all orders with advanced filtering
  - Search by customer, email, or order ID
  - Filter by status, table, chef, waiter, payment status
  - Date range filtering
  - Export orders to CSV/Excel

- **Menu Management**
  - Add, edit, and delete menu items
  - Upload item images
  - Categorize items (Coffee, Pastries, Beverages)
  - Set prices and descriptions

- **Staff Management**
  - Invite chefs and waiters via email
  - Activate/deactivate staff accounts
  - View pending invitations
  - Manage staff permissions

- **Booking Management**
  - View all table bookings
  - Real-time availability status
  - Cancel or modify bookings

- **Reports & Analytics**
  - Revenue reports with date range filtering
  - Order distribution by status
  - Top-selling items analysis
  - Export functionality

- **Customer Feedback**
  - Overall rating (out of 5)
  - Category-wise ratings (Food, Service, Ambiance, Value)
  - Rating distribution charts
  - Individual feedback reviews
  - Recommendation percentage

---

## Screenshots

### Customer Interface

#### Landing Page
<img width="1470" height="821" alt="Image" src="https://github.com/user-attachments/assets/d0714ecf-3aec-4189-8a3c-7775da02b4e9" />
*Welcome page with menu exploration and table booking options*

#### Menu & Products
<img width="1470" height="835" alt="Image" src="https://github.com/user-attachments/assets/cdfc8634-4a5a-4153-9257-8f90addf70e5" />
*Browse coffee, pastries, and beverages with filtering options*

#### Table Booking
<img width="1470" height="835" alt="Image" src="https://github.com/user-attachments/assets/82d1c4a7-f5c0-4ebc-8dca-a2fcee040d93" />
<img width="1470" height="835" alt="Image" src="https://github.com/user-attachments/assets/dc2bd65d-26ea-4b17-8ee2-582ef735969c" />
*Real-time table availability with 19 out of 20 tables available*

#### Order History
<img width="1470" height="835" alt="Image" src="https://github.com/user-attachments/assets/8feb4acc-30a3-46f9-a298-3c8085a74d5a" />
*View current and past orders with payment status and order details*

---

### Chef Dashboard
<img width="1470" height="835" alt="Image" src="https://github.com/user-attachments/assets/1d5ee0f1-221b-4341-9c13-c4f717149f3a" />
<img width="1470" height="835" alt="Image" src="https://github.com/user-attachments/assets/80709aab-17b8-4144-9613-6a0e7ebb4fc0" />
*Chef interface showing orders in progress and ready to prepare*

---

### Waiter Dashboard

#### Order Monitoring
<img width="1470" height="835" alt="Image" src="https://github.com/user-attachments/assets/24ae6eba-c02f-4155-83f2-09632d2a4781" />
*Track orders being prepared (1) and ready to serve (0)*

---

### Admin Panel

#### Booking Management
<img width="1470" height="835" alt="Image" src="https://github.com/user-attachments/assets/d9baedd3-440b-46fc-b6d9-ab17ac3b9693" />
*Manage customer table booking *

#### Menu Management
*Manage 6 menu items with edit/delete functionality*

#### Staff Management
<img width="1470" height="835" alt="Image" src="https://github.com/user-attachments/assets/bb297297-5dcf-41f7-bd90-d13a36f2d1b0" />
*Manage 3 active chefs and 4 active waiters*

#### Orders History
<img width="1470" height="835" alt="Image" src="https://github.com/user-attachments/assets/110433b5-e69d-4a62-9722-064d31440def" />
*Advanced filtering: 12 orders shown with status, customer, and amount details*

#### Reports & Analytics
<img width="1470" height="835" alt="Image" src="https://github.com/user-attachments/assets/8e530774-2d12-4173-a9ad-fc28d00b4d66" />
*Analytics: $88.02 revenue, 12 orders, $7.34 average order value, 10 completed*

#### Customer Feedback
<img width="1470" height="835" alt="Image" src="https://github.com/user-attachments/assets/3bfcb1a0-7bf3-4583-bb69-3c89d83388e8" />
*Overall rating: 4.5/5.0, 4 total feedbacks, 100% recommendation rate*

*Detailed customer reviews with category ratings*

---

## Tech Stack

### Backend
- **Java 17+** - Core programming language
- **Spring Boot 3.0+** - Application framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database access layer
- **Hibernate** - ORM framework
- **MySQL** - Relational database
- **Maven** - Dependency management

### Frontend
- **React.js 18+** - UI library
- **JavaScript (ES6+)** - Programming language
- **HTML5 & CSS3** - Markup and styling
- **React Router** - Navigation

### Security
- **BCrypt** - Password hashing
- **Spring Security** - Endpoint protection

### Tools
- **Git** - Version control
- **GitHub** - Code repository
- **Postman** - API testing
- **MySQL Workbench** - Database management
- **IntelliJ IDEA** - Java IDE
- **VS Code** - Code editor

---

## Architecture

### System Architecture
```
┌─────────────────┐
│   React Client  │
│   (Port 3000)   │
└────────┬────────┘
         │ REST API
         │ (JSON)
┌────────▼────────┐
│  Spring Boot    │
│   Application   │
│   (Port 8080)   │
└────────┬────────┘
         │ JDBC
┌────────▼────────┐
│  MySQL Database │
│   (Port 3306)   │
└─────────────────┘
```

### Order Flow State Machine
```
PENDING → PREPARING → READY → SERVING → COMPLETED
                              ↓
                          CANCELLED
```

### Database Design
- **15+ normalized tables**
- Foreign key constraints
- Indexes for performance
- Audit fields (created_at, updated_at)

---

## Installation

### Prerequisites
- Java 17 or higher
- Node.js 16+ and npm
- MySQL 8.0+
- Maven 3.6+
- Git

### Backend Setup

1. **Clone the repository**
```bash
git clone https://github.com/Ayush-kat/Javabite.git
cd Javabite/backend
```

2. **Configure MySQL Database**
```bash
# Create database
mysql -u root -p
CREATE DATABASE javabite;
EXIT;
```

3. **Update application.properties**
```properties
# src/main/resources/application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/javabite
spring.datasource.username=your_username
spring.datasource.password=your_password

# JWT Configuration
jwt.secret=your_secret_key_here
jwt.expiration=86400000

# Flyway
spring.flyway.enabled=true
```

4. **Build and run**
```bash
mvn clean install
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### Frontend Setup

1. **Navigate to frontend directory**
```bash
cd ../frontend
```

2. **Install dependencies**
```bash
npm install
```

3. **Configure API endpoint**
```javascript
// src/config.js
export const API_BASE_URL = 'http://localhost:8080/api';
```

4. **Start the development server**
```bash
npm start
```

The frontend will start on `http://localhost:3000`

---

## Usage

### Default Credentials

**Admin**
- Email: `admin@javabite.com`
- Password: `admin123`

**Customer**
- Email: `customer@javabite.com`
- Password: `customer123`

**Chef**
- Email: `chef@javabite.com`
- Password: `chef123`

**Waiter**
- Email: `waiter@javabite.com`
- Password: `waiter123`

### Workflow

1. **Customer Journey**
   - Browse menu items
   - Add items to cart
   - Book a table
   - Place order
   - Track order status
   - Provide feedback

2. **Chef Workflow**
   - Login to chef dashboard
   - View new orders
   - Start preparing order
   - Mark as ready when done

3. **Waiter Workflow**
   - Monitor ready orders
   - Pick up from kitchen
   - Serve to customer
   - Mark as served

4. **Admin Operations**
   - Monitor dashboard metrics
   - Manage menu items
   - Invite and manage staff
   - View reports and analytics
   - Review customer feedback

---

## API Documentation

### Authentication Endpoints

```http
POST /api/auth/register
POST /api/auth/login
POST /api/auth/refresh-token
POST /api/auth/logout
```

### Menu Endpoints

```http
GET    /api/menu/items
GET    /api/menu/items/{id}
POST   /api/menu/items          [ADMIN]
PUT    /api/menu/items/{id}     [ADMIN]
DELETE /api/menu/items/{id}     [ADMIN]
GET    /api/menu/categories
```

### Order Endpoints

```http
GET    /api/orders
GET    /api/orders/{id}
POST   /api/orders
PUT    /api/orders/{id}/status
GET    /api/orders/customer/{customerId}
GET    /api/orders/chef/{chefId}
GET    /api/orders/waiter/{waiterId}
```

### Booking Endpoints

```http
GET    /api/bookings
POST   /api/bookings
PUT    /api/bookings/{id}/cancel
GET    /api/bookings/customer/{customerId}
GET    /api/tables/availability
```

### Admin Endpoints

```http
GET    /api/admin/dashboard/stats
GET    /api/admin/reports/revenue
GET    /api/admin/staff
POST   /api/admin/staff/invite
PUT    /api/admin/staff/{id}/status
GET    /api/admin/feedback
```

### Example Request

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "customer@javabite.com",
    "password": "customer123"
  }'

# Place Order (with JWT token)
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "items": [
      {"menuItemId": 1, "quantity": 2},
      {"menuItemId": 3, "quantity": 1}
    ],
    "tableId": 4
  }'
```

---

## Database Schema

### Key Tables

**users**
- id, email, password, role, firstName, lastName
- createdAt, updatedAt

**menu_items**
- id, name, description, price, category
- imageUrl, available

**orders**
- id, customerId, tableId, chefId, waiterId
- status, totalAmount, paymentStatus
- createdAt, completedAt

**bookings**
- id, customerId, tableId, bookingDate, bookingTime
- numberOfPeople, status, specialRequests

**feedback**
- id, orderId, customerId
- foodRating, serviceRating, ambianceRating, valueRating
- comment, wouldRecommend

### Relationships
- One Customer → Many Orders
- One Order → Many OrderItems
- One Chef → Many Orders
- One Waiter → Many Orders
- One Table → Many Bookings
- One Order → One Feedback

---

## Contributing

Contributions are welcome! Please follow these steps:

1. **Fork the repository**
```bash
git clone https://github.com/YOUR_USERNAME/Javabite.git
```

2. **Create a feature branch**
```bash
git checkout -b feature/AmazingFeature
```

3. **Commit your changes**
```bash
git commit -m 'Add some AmazingFeature'
```

4. **Push to the branch**
```bash
git push origin feature/AmazingFeature
```

5. **Open a Pull Request**

### Code Style Guidelines
- Follow Java naming conventions
- Write meaningful commit messages
- Add comments for complex logic
- Update documentation for new features
- Write unit tests for new code

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## Author

**Ayush Katiyar**

- GitHub: [@Ayush-kat](https://github.com/Ayush-kat)
- LinkedIn: [Ayush Katiyar](https://www.linkedin.com/in/Ayush-katiyar-04aa92287)
- Email: katiyar.ash21@gmail.com
---

## Acknowledgments

- **Infosys Springboard** - For the Virtual Internship 6.0 opportunity
- **Spring Boot Community** - For excellent documentation
- **React Community** - For the amazing frontend library
- Coffee Shop Owners - For domain insights

---

## 📈 Future Enhancements

- [ ] Mobile app (React Native)
- [ ] Payment gateway integration (Stripe/Razorpay)
- [ ] Email notifications for order updates
- [ ] SMS notifications
- [ ] Inventory management system
- [ ] Employee shift scheduling
- [ ] Customer loyalty program
- [ ] Multi-language support
- [ ] Dark mode
- [ ] Real-time WebSocket updates
- [ ] Advanced reporting with charts
- [ ] QR code-based table ordering

---

## 🐛 Known Issues

- None at the moment. Please report issues on GitHub!

---

## 📞 Support

If you have any questions or need help:

1. Open an issue on GitHub
2. Email: katiyar.ash21@gmail.com
3. Check the documentation

---

<div align="center">

⭐ **Star this repository if you found it helpful!** ⭐

Made by Ayush Katiyar

</div>
