# 🛒 Shopping Cart Module – E-Commerce Platform
## Module 3.2 | Spring Boot + Thymeleaf + MySQL

---

## 📌 What This Module Does
This is the **Shopping Cart Module** of the E-Commerce Platform.
It allows users to:
- Add products to their cart
- View all items in their cart
- Remove individual items
- Clear the entire cart
- See the total price

---

## 🏗️ Project Structure (MVC Pattern)
```
com.ecommerce.shoppingcart
│
├── controller/
│   └── CartController.java     ← Handles HTTP requests (URLs)
│
├── model/
│   └── Cart.java               ← Database entity (maps to cart table)
│
├── repository/
│   └── CartRepository.java     ← Talks to the database via JPA
│
├── service/
│   └── CartService.java        ← Business logic lives here
│
└── ShoppingcartApplication.java ← Entry point (main method)

resources/
├── templates/
│   ├── cart.html               ← View cart page
│   └── add-to-cart.html        ← Add item form page
├── static/css/
│   └── style.css               ← Styling for UI
└── application.properties      ← DB config
```

---

## 🔗 URL Endpoints

| URL | Method | What it does |
|-----|--------|--------------|
| `/cart/{userId}` | GET | View cart for a user |
| `/cart/add/{userId}` | GET | Show add-to-cart form |
| `/cart/add` | POST | Submit and save cart item |
| `/cart/remove/{cartId}/{userId}` | GET | Remove one item |
| `/cart/clear/{userId}` | GET | Clear entire cart |

---

## 🗄️ Database Table (Auto-created by Hibernate)
```sql
CREATE TABLE cart (
  cartId       INT PRIMARY KEY AUTO_INCREMENT,
  userId       BIGINT,
  productId    BIGINT,
  productName  VARCHAR(255),
  price        DOUBLE,
  quantity     INT
);
```

---

## ▶️ How to Run

1. Make sure **MySQL** is running on port `3306`
2. Database `ecp` will be created automatically
3. Run the app:
```bash
mvn spring-boot:run
```
4. Open browser → `http://localhost:8082/cart/1`

---

## 💡 Interview Explanation (Simple)

> "This module follows the MVC pattern.
> The **Controller** receives the request from the browser.
> It calls the **Service** which contains the business logic.
> The **Service** uses the **Repository** to save or fetch data from MySQL.
> The result is sent to the **HTML template** (Thymeleaf) which the user sees.
> Each layer has one job — this makes it easy to test and maintain."

---
