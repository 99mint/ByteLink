# 🔗 ByteLink - Secure URL Shortener

A production-ready URL shortener service built with Spring Boot, MySQL, and JWT authentication.

## ✨ Features

- ✅ URL shortening with Base62 encoding
- ✅ Click analytics and tracking
- ✅ JWT authentication with refresh tokens
- ✅ User-specific URL management
- ✅ Automatic URL expiration (90 days)
- ✅ Role-based access control (USER/ADMIN)
- 📚 **Interactive Swagger API Documentation**

## 🛠 Tech Stack

- **Backend:** Spring Boot 3.4.0, Java 25
- **Database:** MySQL 8.0
- **Security:** Spring Security 7.0 + JWT
- **ORM:** Spring Data JPA / Hibernate
- **API Docs:** Swagger/OpenAPI 3.0
- **Build Tool:** Maven

## 📦 Prerequisites

- Java 17+
- Maven 3.6+
- MySQL 8.0+

## 🚀 Quick Start

### 1. Clone & Setup Database
```bash
git clone https://github.com/99mint/ByteLink.git
cd ByteLink
```

```sql
CREATE DATABASE bytelink;
```

### 2. Configure Environment Variables

**Linux/Mac:**
```bash
export BYTELINK_DB_URL=jdbc:mysql://localhost:3306/bytelink
export DB_USERNAME=your_mysql_username
export DB_PASSWORD=your_mysql_password
export SECRET_KEY=your_base64_encoded_secret_key
```

**Windows (PowerShell):**
```powershell
$env:BYTELINK_DB_URL="jdbc:mysql://localhost:3306/bytelink"
$env:DB_USERNAME="your_mysql_username"
$env:DB_PASSWORD="your_mysql_password"
$env:SECRET_KEY="your_base64_encoded_secret_key"
```

**Generate SECRET_KEY:**
```bash
# OpenSSL
openssl rand -base64 32

# Python
python -c "import secrets; print(secrets.token_urlsafe(32))"

# Test key (dev only)
dGhpc2lzYXRlc3RzZWNyZXRrZXlmb3JieXRlbGluazEyMzQ1Ng==
```

### 3. Build & Run
```bash
./mvnw clean install
./mvnw spring-boot:run
```

Application starts at `http://localhost:8080`

## 📚 API Documentation

**Swagger UI:** `http://localhost:8080/swagger-ui.html`

Complete interactive documentation with:
- All endpoints and schemas
- Authentication requirements
- Request/response examples
- Try-it-out functionality

## 🔐 Authentication

JWT-based stateless authentication:

1. Register → Create account
2. Login → Get access token (1h) + refresh token (7d)
3. Use APIs → Include `Authorization: Bearer <token>` header
4. Refresh → Get new access token when expired
5. Logout → Revoke refresh token

## 🏗️ Architecture

### URL Shortening Process
```
User creates URL → Saved to DB (auto-generated ID)
→ ID encoded to Base62 → Short code returned

Example: ID 123 → "1Z" | ID 1000 → "g8"
```

### Project Structure
```
ByteLink/
├── src/main/java/com/mint/bytelink/
│   ├── config/          # Security & scheduling
│   ├── controller/      # REST endpoints
│   ├── dto/            # Data transfer objects
│   ├── entity/         # JPA entities
│   ├── exception/      # Exception handling
│   ├── repository/     # Data access layer
│   ├── security/       # JWT & authentication
│   ├── service/        # Business logic
│   └── util/           # Helpers (Base62, validation)
└── src/main/resources/
    └── application.yaml
```

## ⚙️ Configuration

Edit `src/main/resources/application.yaml`:

```yaml
jwt:
  expiration: 3600000              # 1 hour
  refresh-expiration: 604800000    # 7 days
```

## 🔒 Security Features

- JWT stateless authentication
- BCrypt password hashing
- Refresh token rotation
- Role-based access control
- Input validation
- User data isolation
- Automated token cleanup

## 🛡️ Production Checklist

- [ ] Strong JWT secret (256-bit+)
- [ ] Enable HTTPS/TLS
- [ ] Configure CORS
- [ ] Implement rate limiting
- [ ] Database connection pooling & backups
- [ ] Environment variables for secrets
- [ ] Monitoring & alerting
- [ ] Production logging levels

## 🗺️ Roadmap

- [ ] Custom short URL aliases
- [ ] QR code generation
- [ ] Enhanced analytics dashboard
- [ ] Bulk URL shortening
- [ ] Email verification
- [ ] Two-factor authentication

## 🤝 Contributing

1. Fork repository
2. Create feature branch
3. Commit changes (conventional commits format)
4. Push and open Pull Request

## 📝 License

MIT License - see [LICENSE](LICENSE)

## 👨‍💻 Author

**Abhay Rajput**
- GitHub: [@99mint](https://github.com/99mint)
- Email: abhayyy2204@gmail.com

## 📞 Support

- [Issues Page](https://github.com/99mint/ByteLink/issues)
- Swagger UI for API documentation

---

**Built with Spring Boot and Spring Security**