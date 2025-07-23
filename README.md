# WhatsApp Messaging & Communication Services

A robust, modular backend platform for WhatsApp messaging, user management, chat, campaigns, and reporting.  
**Author:** Ashish Meena

---

## 🚀 Overview

This project provides a scalable backend built with **Spring Boot** for managing WhatsApp messaging, user authentication, chat rooms, campaigns, templates, and reporting. It supports both relational (JPA) and NoSQL (MongoDB) storage, and exposes RESTful APIs for integration.

---

## 🧩 Main Modules

- **Authentication & Authorization:**  
  User registration, login, roles, permissions, and admin management.

- **WhatsApp Messaging:**  
  Send WhatsApp template messages, manage WhatsApp accounts, forms, and handle Facebook template approval.

- **Campaigns & Broadcasts:**  
  Create, schedule, and track messaging campaigns and broadcasts with detailed logs.

- **Chat System:**  
  Real-time chat rooms, private/group messaging, and message status tracking.

- **Reporting:**  
  Powerful reporting APIs for users and admins, including filtering, summaries, and bulk operations.

- **Contacts & Tags:**  
  Manage phonebooks, tags, and segmentation for targeted messaging.

- **File Uploads:**  
  Image and media upload endpoints for message attachments.

---

## 📁 Project Structure


```
services/
├── auth/
│   ├── constants/
│   ├── dto/
│   ├── enums/
│   ├── exception/
│   ├── mapper/
│   ├── model/
│   ├── repo/
│   └── service/
├── chat/
│   ├── constants/
│   ├── dto/
│   ├── enums/
│   ├── exception/
│   ├── mapper/
│   ├── model/
│   ├── repo/
│   └── service/
├── chatbot/
│   ├── constants/
│   ├── dto/
│   ├── enums/
│   ├── exception/
│   ├── mapper/
│   ├── model/
│   ├── repo/
│   └── service/
├── common/
│   ├── constants/
│   ├── dto/
│   ├── enums/
│   ├── exception/
│   ├── mapper/
│   ├── model/
│   ├── repo/
│   └── service/
├── graphql/
│   ├── constants/
│   ├── dto/
│   ├── enums/
│   ├── exception/
│   ├── mapper/
│   ├── model/
│   ├── repo/
│   └── service/
├── messaging/
│   ├── constants/
│   ├── dto/
│   ├── enums/
│   ├── exception/
│   ├── mapper/
│   ├── model/
│   ├── repo/
│   └── service/
├── payment/
│   ├── constants/
│   ├── dto/
│   ├── enums/
│   ├── exception/
│   ├── mapper/
│   ├── model/
│   ├── repo/
│   └── service/
├── template/
│   ├── constants/
│   ├── dto/
│   ├── enums/
│   ├── exception/
│   ├── mapper/
│   ├── model/
│   ├── repo/
│   └── service/
└── whatsapp/
    ├── constants/
    ├── dto/
    ├── enums/
    ├── exception/
    ├── mapper/
    ├── model/
    ├── repo/
    └── service/
```


---

## ⚙️ Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- MongoDB & MySQL/Postgres (as configured)

### Build & Run

```sh
./mvnw clean install
./mvnw spring-boot:run

🔑 Key Endpoints
Auth:
/auth/register<vscode_annotation details='%5B%7B%22title%22%3A%22hardcoded-credentials%22%2C%22description%22%3A%22Embedding%20credentials%20in%20source%20code%20risks%20unauthorized%20access%22%7D%5D'>,</vscode_annotation> /auth/login, /admin/permission/*, /admin/role/*

WhatsApp:
/api/whatsapp/send-template — Send WhatsApp template messages
/api/whatsapp/* — Manage WhatsApp accounts, forms

Campaigns & Broadcasts:
/api/broadcast/*, /api/campaign/*

Reports:
/api/report/user/filtered, /api/report/my/summary, /admin/reports/*

Chat:
/api/chat/* — Chat room and message management

Contacts & Tags:
/api/contacts/*, /api/tag/*

Templates:
/api/templates/* — WhatsApp template creation and approval

Image Upload:
/api/image/upload

📊 Reporting & Admin
Filter, summarize, and bulk-manage reports via /admin/reports/* endpoints.
Admin APIs for user, permission, and role management.
🛡️ Security
Uses Spring Security for authentication and role-based access.
JWT token-based authentication for API endpoints.
🧪 Testing
Run all tests:

./mvnw test

📄 License
This project is licensed under the Apache License 2.0.

👤 Author
Ashish Meena

