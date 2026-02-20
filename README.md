# 🔗 ByteLink — URL Shortener

A lightweight, production-ready URL shortener REST API built with **Spring Boot 4** and **MySQL**. ByteLink converts long URLs into compact, Base62-encoded short codes and tracks every click with analytics data.

---

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Prerequisites](#-prerequisites)
- [Getting Started](#-getting-started)
- [Configuration](#-configuration)
- [API Reference](#-api-reference)
- [How It Works](#-how-it-works)
- [Project Structure](#-project-structure)
- [Security Notes](#-security-notes)
- [Contributing](#-contributing)
- [License](#-license)

---

## ✨ Features

- 🔗 Shorten any long URL into a compact 6-character Base62 code
- 📊 Click analytics — tracks click count and User-Agent per visit
- ⏳ Automatic URL expiration (90 days)
- 🔄 Update the destination URL for an existing short code
- 🗑️ Delete short URLs
- 🔍 Look up all short URLs mapped to a given long URL
- 🚦 Global exception handling with structured error responses
- ✅ Input validation via Jakarta Bean Validation
- 🏗️ Clean layered architecture (Controller → Service → Repository)

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 4.0.2 |
| Language | Java 17 |
| Database | MySQL 8.0+ |
| ORM | Spring Data JPA / Hibernate |
| Build Tool | Maven (Maven Wrapper included) |
| Utilities | Lombok, Jakarta Validation |

---

## 📦 Prerequisites

- Java 17+
- Maven 3.6+ *(or use the included `./mvnw` wrapper)*
- MySQL 8.0+

---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/99mint/ByteLink.git
cd ByteLink
```

### 2. Create the MySQL Database

```sql
CREATE DATABASE bytelink;
```

### 3. Configure Environment Variables

Copy the example env file and fill in your values:

```bash
cp env.example .env
```

```env
DB_HOST=localhost
DB_PORT=3306
DB_NAME=bytelink
DB_USERNAME=root
DB_PASSWORD=your_password_here
SERVER_PORT=8080
```

Then update `src/main/resources/application.yaml` with your database credentials (or wire in the env vars).

### 4. Build the Project

```bash
./mvnw clean install
```

### 5. Run the Application

```bash
./mvnw spring-boot:run
```

The server starts at `http://localhost:8080`.

---

## ⚙️ Configuration

`src/main/resources/application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/bytelink
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
```

> ⚠️ Set `show-sql: false` and use environment-injected credentials before going to production.

---

## 📡 API Reference

### Link Management — `/links`

| Method | Endpoint | Description | Body |
|---|---|---|---|
| `POST` | `/links/create` | Create a short URL | `{ "longUrl": "https://..." }` |
| `GET` | `/links?longUrl=...` | Get all short URLs for a long URL | Query param |
| `PUT` | `/links/{shortUrl}` | Update destination URL | `{ "longUrl": "https://..." }` |
| `DELETE` | `/links/{shortUrl}` | Delete a short URL | — |

### Redirect — `/r`

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/r/{shortCode}` | Redirect to original URL (HTTP 307) + record click |

---

## 💡 Usage Examples

### Create a Short URL

```bash
curl -X POST http://localhost:8080/links/create \
  -H "Content-Type: application/json" \
  -d '{"longUrl": "https://www.example.com/some/very/long/url"}'
```

**Response:**
```json
{
  "longUrl": "https://www.example.com/some/very/long/url",
  "shortUrl": "00000B",
  "createdAt": "2025-02-20T10:30:00",
  "expiration": "2025-05-21T10:30:00"
}
```

### Redirect via Short Code

```bash
curl -L http://localhost:8080/r/00000B
```

### Get All Short URLs for a Long URL

```bash
curl "http://localhost:8080/links?longUrl=https://www.example.com/some/very/long/url"
```

### Update Destination URL

```bash
curl -X PUT http://localhost:8080/links/00000B \
  -H "Content-Type: application/json" \
  -d '{"longUrl": "https://www.newdestination.com"}'
```

### Delete a Short URL

```bash
curl -X DELETE http://localhost:8080/links/00000B
```

---

## ⚙️ How It Works

### Short Code Generation

ByteLink uses **Base62 encoding** on the auto-incremented database ID to generate a unique, URL-safe 6-character code.

**Character set:** `0–9`, `A–Z`, `a–z` → 62 characters  
**Capacity:** 62⁶ = over **56 billion** unique codes

```
ID: 1     → Short Code: 000001
ID: 62    → Short Code: 000010
ID: 3844  → Short Code: 000100
```

### Click Analytics

Each redirect request to `/r/{shortCode}`:
1. Resolves the long URL from the database
2. Increments the `clickCounts` counter on the URL record
3. Creates a `Click` record with the timestamp and `User-Agent` header
4. Returns HTTP 307 (Temporary Redirect) to the original URL

---

## 📁 Project Structure

```
ByteLink/
├── src/
│   └── main/
│       ├── java/com/mint/bytelink/
│       │   ├── ByteLinkApplication.java       # Entry point
│       │   ├── controller/
│       │   │   ├── RedirectController.java    # Handles redirects + click tracking
│       │   │   └── UrlDetailsController.java  # CRUD endpoints
│       │   ├── dto/
│       │   │   ├── UrlDetailsRequestDTO.java
│       │   │   └── UrlDetailsResponseDTO.java
│       │   ├── entity/
│       │   │   ├── Click.java                 # Click analytics entity
│       │   │   └── UrlDetails.java            # URL record entity
│       │   ├── exception/
│       │   │   ├── ErrorResponse.java
│       │   │   ├── GlobalExceptionHandler.java
│       │   │   └── ResourceNotFoundException.java
│       │   ├── repository/
│       │   │   ├── ClickRepository.java
│       │   │   └── UrlDetailsRepository.java
│       │   ├── service/
│       │   │   ├── ClickService.java          # Click recording logic
│       │   │   └── UrlDetailsService.java     # URL business logic
│       │   └── util/
│       │       ├── ShortCodeGenerator.java    # Base62 encoder
│       │       └── UrlDetailsMapper.java      # Entity ↔ DTO mapping
│       └── resources/
│           └── application.yaml
├── env.example
├── pom.xml
├── mvnw / mvnw.cmd
└── README.md
```

---

## 🔒 Security Notes

Before deploying to production:

- **Never commit credentials** — use environment variables or a secrets manager
- **Add rate limiting** — protect the `/links/create` endpoint from abuse
- **Validate URLs** — check that submitted URLs are well-formed and safe (block known malicious domains)
- **Add authentication** — protect management endpoints (`POST`, `PUT`, `DELETE`) with JWT or API keys
- **Enable HTTPS** — always serve over TLS in production
- **Set `show-sql: false`** — avoid leaking query details in logs

---

## 🤝 Contributing

Contributions are welcome!

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m 'Add your feature'`
4. Push to the branch: `git push origin feature/your-feature`
5. Open a Pull Request

---

## 📝 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author

**Abhay Rajput**
- GitHub: [@99mint](https://github.com/99mint)
- Email: abhayyy2204@gmail.com

---

*Made with ❤️ using Spring Boot*