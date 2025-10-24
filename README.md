# 🛒 Spring Boot eCommerce Platform

An eCommerce web application built using **Spring Boot**, providing user authentication, session management, and a full shopping experience. The platform features real-time product listings, a shopping cart, and order management with modern tech integrations.

---

## 🚀 Features

- 🔐 **User Authentication** using Firestore and HTTP Sessions  
- 🛍️ **Product Display** from MongoDB  
- 🛒 **Cart & Order Management** using Firestore  
- 🧾 Session-based login system with role handling  
- 🖼️ Frontend built with HTML, CSS, JS, jQuery, and Thymeleaf  
- 🌐 UI/UX inspiration from [React Bits.dev](https://reactbits.dev/)

---

## 🧰 Tech Stack

| Layer        | Technologies Used                                      |
|--------------|--------------------------------------------------------|
| **Backend**  | Java, Spring Boot                                      |
| **Frontend** | HTML, CSS, JavaScript, jQuery, Thymeleaf               |
| **Database** | MongoDB (Product data), Firestore (Users, Cart, Orders)|

---

## 📦 Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/notmod913/e-commerce.git
cd e-commerce
```

### 2. Configure Firestore & MongoDB

- Add your **Firestore** and **MongoDB** credentials to `application.properties`.

### 3. Run the Application

```bash
./mvnw spring-boot:run
```

### 4. Access the Application

Open in your browser:

```
http://localhost:8080
```

---

## 👤 User Roles & Session

- **Customer**:
  - Register and log in
  - View available products
  - Add products to cart
  - Place orders

- **Session Management**:
  - Handled via Spring Boot `HttpSession` for secure session tracking

---

## 🌍 Deployment

You can deploy this project on cloud platforms such as:

- **Render** (Recommended for Spring Boot apps)

> 🔧 Make sure to set up environment variables for Firestore and MongoDB credentials on the deployment platform.
---

## 📚 References

- **UI/UX** inspiration from [React Bits.dev](https://reactbits.dev/)
- **Firestore** integration guide: [Firebase Docs](https://firebase.google.com/docs/firestore)
- **MongoDB** documentation: [MongoDB Docs](https://www.mongodb.com/docs/)

---

## 🤝 Contribution

Pull requests and forks are welcome. For significant changes, open an issue first to discuss them.

---

## 📜 License

This project is open source and available under the [MIT License](LICENSE).

