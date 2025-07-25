# 📨 Messaging Service

A simple, secure messaging API built with Spring Boot, MongoDB, and JWT authentication. Includes centralized logging via ELK stack and full test coverage with JUnit and MockMvc.

---

## 📌 Features

- ✅ User registration & login (JWT based)
- ✅ Send messages between users
- ✅ Retrieve chat history with another user
- ✅ MongoDB NoSQL data store
- ✅ RESTful API design with Spring Boot
- ✅ Centralized logging with ELK (Elasticsearch, Logstash, Kibana)
- ✅ Docker Compose for full environment setup
- ✅ Unit & Integration tests with JUnit 5 and MockMvc
- ⚡ Rate limiting per tenant (requests/minute) based on subscription plan

---

## 🚀 Getting Started

### Prerequisites

Make sure the following tools are installed on your machine:

- Java 17
- Docker & Docker Compose
- Maven or IntelliJ IDEA
- Git

---

### 🔧 Running the Project

#### 1. Clone the repository

```bash
git clone https://github.com/your-username/messaging-service.git
cd messaging-service
```

#### 2. Start the services with Docker Compose

```bash
docker-compose up -d
```

Services started:

- MongoDB `localhost:27017`
- Mongo Express UI: `http://localhost:8081`
- Elasticsearch: `http://localhost:9200`
- Kibana: `http://localhost:5601`
- Logstash: `localhost:5001` (log pipeline)

#### 3. Run the Spring Boot application

```bash
./mvnw spring-boot:run
```

---

## 📬 API Endpoints

### 🔐 Auth

| Method | Endpoint           | Description        |
|--------|--------------------|--------------------|
| POST   | `/api/auth/register` | Register a new user |
| POST   | `/api/auth/login`    | Login and receive JWT |

### 💬 Messages

| Method | Endpoint                     | Description                        |
|--------|------------------------------|------------------------------------|
| POST   | `/api/messages/send`         | Send a message to another user     |
| GET    | `/api/messages/history?with=username` | Get chat history with a user        |

> All message endpoints require `Authorization: Bearer <JWT>` or `X-API-KEY` header.

### 🏷️ Tenant

| Method | Endpoint           | Description                                             |
|--------|--------------------|---------------------------------------------------------|
| GET    | `/api/tenant`      | Get current tenant configuration                        |
| PUT    | `/api/tenant/plan` | Update subscription plan of current tenant (body: JSON) |

> Tenant endpoints require `Authorization: Bearer <JWT>` or `X-API-KEY` header.

---

## 🧪 Testing

```bash
./mvnw test
```

- Unit tests for `AuthService` and `MessageService`
- Integration tests for `AuthController` and `MessageController`

---

## 📊 Logging & Monitoring

Logs are structured in JSON and sent via Logback to Logstash → Elasticsearch. You can view real-time logs in Kibana:

```
http://localhost:5601
```

Example logs include:

- User registration
- Login attempts
- Message sent
- Message history fetched
- Unauthorized access

---

## ⚙️ Technologies Used

- Java 17
- Spring Boot
- Spring Security (JWT)
- MongoDB
- Docker / Docker Compose
- Logstash, Elasticsearch, Kibana
- JUnit 5, Mockito, MockMvc

---

## 📌 Future Improvements

- [ ] WebSocket support for real-time messaging
- [ ] User block list / mute functionality
- [ ] Message deletion or "seen" status
- [ ] Admin role management
- [ ] CI/CD pipeline integration

---

## 📄 License

This project is licensed under the MIT License.
