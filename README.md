# 🔗 ByteLink - Secure URL Shortener

A secure and efficient URL shortener service built with Spring Boot, MySQL, and JWT authentication.

## 📋 Table of Contents
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Authentication](#authentication)
- [API Endpoints](#api-endpoints)
- [Usage Examples](#usage-examples)
- [Security Features](#security-features)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)

## ✨ Features

### Core Features
- ✅ Shorten long URLs to compact codes
- ✅ Base62 encoding for short URL generation
- ✅ Automatic URL expiration (90 days)
- ✅ Click analytics and tracking
- ✅ Update long URL for existing short codes
- ✅ Delete URLs
- ✅ Retrieve all short URLs for a given long URL

### Security Features
- 🔐 JWT-based authentication
- 🔄 Refresh token system for extended sessions
- 👤 User registration and login
- 🔒 Protected endpoints
- 🚪 Secure logout with token revocation
- ⏰ Automatic token cleanup
- 🔑 User-specific URL management

### Developer Features
- ✅ Global exception handling
- ✅ Input validation
- ✅ RESTful API design
- ✅ Comprehensive logging
- ✅ Role-based access control (USER/ADMIN)

## 🛠 Tech Stack

- **Backend Framework:** Spring Boot 3.4.0
- **Language:** Java 25
- **Database:** MySQL 8.0
- **Security:** Spring Security 7.0 + JWT
- **ORM:** Spring Data JPA / Hibernate
- **Build Tool:** Maven
- **Libraries:**
    - Lombok
    - Spring Boot Validation
    - Spring Boot Web
    - Spring Security
    - JJWT (JWT implementation)
    - BCrypt (Password hashing)

## 📦 Prerequisites

Before running this project, make sure you have:

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Your favorite IDE (IntelliJ IDEA, Eclipse, VS Code)

## 🚀 Installation

### 1. Clone the repository
```bash
git clone https://github.com/99mint/ByteLink.git
cd ByteLink
```

### 2. Create MySQL database
```sql
CREATE DATABASE bytelink;
```

### 3. Set up environment variables

**Windows (Command Prompt):**
```cmd
set BYTELINK_DB_URL=jdbc:mysql://localhost:3306/bytelink
set DB_USERNAME=your_mysql_username
set DB_PASSWORD=your_mysql_password
set SECRET_KEY=your_base64_encoded_secret_key
```

**Windows (PowerShell):**
```powershell
$env:BYTELINK_DB_URL="jdbc:mysql://localhost:3306/bytelink"
$env:DB_USERNAME="your_mysql_username"
$env:DB_PASSWORD="your_mysql_password"
$env:SECRET_KEY="your_base64_encoded_secret_key"
```

**Linux/Mac:**
```bash
export BYTELINK_DB_URL=jdbc:mysql://localhost:3306/bytelink
export DB_USERNAME=your_mysql_username
export DB_PASSWORD=your_mysql_password
export SECRET_KEY=your_base64_encoded_secret_key
```

### 4. Generate a SECRET_KEY

**Using OpenSSL:**
```bash
openssl rand -base64 32
```

**Using Python:**
```bash
python -c "import secrets; print(secrets.token_urlsafe(32))"
```

**Or use this test key:**
```
dGhpc2lzYXRlc3RzZWNyZXRrZXlmb3JieXRlbGluazEyMzQ1Ng==
```

### 5. Build the project
```bash
./mvnw clean install
```

### 6. Run the application
```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

## ⚙️ Configuration

### Application Properties

Located at `src/main/resources/application.yaml`:

```yaml
spring:
  datasource:
    url: ${BYTELINK_DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

jwt:
  secret: ${SECRET_KEY}
  expiration: 3600000              # Access token: 1 hour
  refresh-expiration: 604800000    # Refresh token: 7 days
```

**Token Expiration Configuration:**
- `jwt.expiration`: Access token lifespan in milliseconds (default: 1 hour)
- `jwt.refresh-expiration`: Refresh token lifespan in milliseconds (default: 7 days)

## 🔐 Authentication

### How Authentication Works

ByteLink uses JWT (JSON Web Tokens) for stateless authentication:

1. **Register** → Create a new user account
2. **Login** → Receive access token (1 hour) + refresh token (7 days)
3. **Access Protected Endpoints** → Include access token in `Authorization` header
4. **Token Expires** → Use refresh token to get a new access token
5. **Logout** → Revoke refresh token

### Token Types

| Token Type | Lifespan | Purpose | Storage |
|------------|----------|---------|---------|
| **Access Token** | 1 hour | API authentication | Memory/LocalStorage |
| **Refresh Token** | 7 days | Get new access tokens | Database + Client |

## 📡 API Endpoints

### 🔓 Public Endpoints

#### Authentication

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| POST | `/auth/register` | Register new user | `{"name": "...", "username": "...", "email": "...", "password": "..."}` |
| POST | `/auth/login` | Login and get tokens | `{"username": "...", "password": "..."}` |
| POST | `/auth/refresh` | Refresh access token | `{"refreshToken": "..."}` |
| POST | `/auth/logout` | Logout and revoke token | `{"refreshToken": "..."}` |

#### Redirect

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/r/{shortCode}` | Redirect to original URL |

---

### 🔒 Protected Endpoints (Require Authentication)

#### URL Management

**Base URL:** `http://localhost:8080/links`

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| POST | `/create` | Create a short URL | `{"longUrl": "https://example.com"}` |
| GET | `?longUrl=...` | Get all short URLs for a long URL | Query param: `longUrl` |
| PUT | `/{shortUrl}` | Update long URL for a short code | `{"longUrl": "https://newurl.com"}` |
| DELETE | `/{shortUrl}` | Delete a short URL | - |

**Note:** All `/links/*` endpoints require `Authorization: Bearer <access-token>` header.

---

### 👑 Admin Endpoints (Require ADMIN role)

| Method | Endpoint | Description |
|--------|----------|-------------|
| * | `/admin/*` | Admin-only operations |

## 💡 Usage Examples

### 1. Register a New User

**Request:**
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "username": "johndoe",
    "email": "john@example.com",
    "password": "securePassword123"
  }'
```

**Response:**
```json
"User Registered Successfully"
```

---

### 2. Login

**Request:**
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "securePassword123"
  }'
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huZG9lIiwiaWF0IjoxNzA5NDgwNDAwLCJleHAiOjE3MDk0ODQwMDB9.xyz...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

**💡 Save both tokens!** You'll need them for subsequent requests.

---

### 3. Create Short URL (Protected)

**Request:**
```bash
curl -X POST http://localhost:8080/links/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -d '{"longUrl": "https://www.example.com/very/long/url/here"}'
```

**Response:**
```json
{
  "id": 1,
  "longUrl": "https://www.example.com/very/long/url/here",
  "shortUrl": "b",
  "createdAt": "2024-02-19T10:30:00",
  "activeTill": "2024-05-19T10:30:00",
  "clickCounts": 0
}
```

---

### 4. Access Short URL (Public)

```bash
curl http://localhost:8080/r/b
```

This will redirect to `https://www.example.com/very/long/url/here` and increment click count.

---

### 5. Refresh Access Token

When your access token expires (after 1 hour):

**Request:**
```bash
curl -X POST http://localhost:8080/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
  }'
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.newTokenHere...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

---

### 6. Get All Your Short URLs

**Request:**
```bash
curl "http://localhost:8080/links?longUrl=https://www.example.com/very/long/url/here" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

**Response:**
```json
[
  {
    "id": 1,
    "longUrl": "https://www.example.com/very/long/url/here",
    "shortUrl": "b",
    "createdAt": "2024-02-19T10:30:00",
    "activeTill": "2024-05-19T10:30:00",
    "clickCounts": 42
  }
]
```

---

### 7. Update Long URL

**Request:**
```bash
curl -X PUT http://localhost:8080/links/b \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -d '{"longUrl": "https://www.newexample.com"}'
```

---

### 8. Delete Short URL

**Request:**
```bash
curl -X DELETE http://localhost:8080/links/b \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

---

### 9. Logout

**Request:**
```bash
curl -X POST http://localhost:8080/auth/logout \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
  }'
```

**Response:**
```json
"Logged out successfully"
```

## 🔒 Security Features

### 1. JWT Authentication
- Stateless authentication using JSON Web Tokens
- Tokens are signed with HMAC-SHA256
- Short-lived access tokens (1 hour) minimize security risk

### 2. Refresh Token System
- Long-lived refresh tokens (7 days) for better UX
- One refresh token per user (automatic replacement)
- Tokens can be revoked on logout
- Expired tokens are automatically cleaned up daily

### 3. Password Security
- Passwords hashed with BCrypt
- Strong password policies (can be configured)
- Never stored in plain text

### 4. User Association
- Each shortened URL is associated with the user who created it
- Users can only manage their own URLs
- Prevents unauthorized access to other users' data

### 5. Role-Based Access Control
- **USER role:** Can create and manage their own URLs
- **ADMIN role:** Has additional administrative privileges
- Extensible for future permission requirements

### 6. Input Validation
- All inputs are validated using Jakarta Validation
- Prevents malformed data from entering the system
- Custom validation for URL formats

### 7. Exception Handling
- Global exception handler for consistent error responses
- Specific handling for authentication failures
- Detailed error messages for debugging (dev mode)

## 📁 Project Structure

```
ByteLink/
├── src/
│   ├── main/
│   │   ├── java/com/mint/bytelink/
│   │   │   ├── config/
│   │   │   │   ├── PasswordConfig.java
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── TokenCleanupScheduler.java
│   │   │   ├── controller/
│   │   │   │   ├── AuthenticationController.java
│   │   │   │   ├── RedirectController.java
│   │   │   │   └── UrlDetailsController.java
│   │   │   ├── dto/
│   │   │   │   ├── auth/
│   │   │   │   │   ├── AuthResponse.java
│   │   │   │   │   ├── LogInRequestViaUsername.java
│   │   │   │   │   ├── RefreshTokenRequest.java
│   │   │   │   │   └── RegisterRequest.java
│   │   │   │   └── url/
│   │   │   │       ├── UrlDetailsRequestDTO.java
│   │   │   │       └── UrlDetailsResponseDTO.java
│   │   │   ├── entity/
│   │   │   │   ├── Click.java
│   │   │   │   ├── RefreshToken.java
│   │   │   │   ├── UrlDetails.java
│   │   │   │   └── User.java
│   │   │   ├── exception/
│   │   │   │   ├── security/
│   │   │   │   │   ├── JwtAccessDeniedHandler.java
│   │   │   │   │   └── JwtAuthEntryPoint.java
│   │   │   │   ├── ErrorResponse.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── UrlExpiredException.java
│   │   │   │   └── UserAlreadyExistsException.java
│   │   │   ├── repository/
│   │   │   │   ├── ClickRepository.java
│   │   │   │   ├── RefreshTokenRepository.java
│   │   │   │   ├── UrlDetailsRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   ├── security/
│   │   │   │   ├── CustomUserDetails.java
│   │   │   │   ├── CustomUserDetailsService.java
│   │   │   │   ├── JwtAuthFilter.java
│   │   │   │   └── JwtService.java
│   │   │   ├── service/
│   │   │   │   ├── AuthenticationService.java
│   │   │   │   ├── ClickService.java
│   │   │   │   ├── RefreshTokenService.java
│   │   │   │   └── UrlDetailsService.java
│   │   │   ├── util/
│   │   │   │   ├── AuthMapper.java
│   │   │   │   ├── ShortCodeGenerator.java
│   │   │   │   └── UrlDetailsMapper.java
│   │   │   └── ByteLinkApplication.java
│   │   └── resources/
│   │       ├── application.yaml
│   │       ├── static/
│   │       └── templates/
│   └── test/
│       └── java/com/mint/bytelink/
│           └── ByteLinkApplicationTests.java
├── .gitignore
├── pom.xml
├── mvnw
└── README.md
```

## 🏗️ Architecture

### Authentication Flow

```
┌─────────┐         ┌──────────┐         ┌──────────┐
│ Client  │────────▶│   API    │────────▶│ Database │
└─────────┘         └──────────┘         └──────────┘
     │                    │
     │ 1. Register/Login  │
     │───────────────────▶│
     │                    │
     │ 2. Access Token    │
     │    Refresh Token   │
     │◀───────────────────│
     │                    │
     │ 3. API Request     │
     │    + Access Token  │
     │───────────────────▶│
     │                    │ 4. Validate JWT
     │                    │────────────┐
     │                    │            │
     │                    │◀───────────┘
     │                    │
     │ 5. Response        │
     │◀───────────────────│
     │                    │
     │ 6. Token Expired?  │
     │    Use Refresh     │
     │───────────────────▶│
     │                    │
     │ 7. New Access Token│
     │◀───────────────────│
```

### URL Shortening Flow

```
1. User creates short URL
   ↓
2. System associates URL with authenticated user
   ↓
3. URL saved to database (gets auto-generated ID)
   ↓
4. ID encoded to Base62 (0-9, a-z, A-Z)
   ↓
5. Short code saved and returned

Example:
- ID: 123  → Base62: "1Z"
- ID: 1000 → Base62: "g8"
```

### Database Schema

```sql
-- Users table
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('USER', 'ADMIN') DEFAULT 'USER',
    is_enable BOOLEAN DEFAULT TRUE,
    is_account_non_expired BOOLEAN DEFAULT TRUE,
    is_account_non_locked BOOLEAN DEFAULT TRUE,
    is_credentials_non_expired BOOLEAN DEFAULT TRUE
);

-- Refresh tokens table
CREATE TABLE refresh_token (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    token VARCHAR(500) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    revoked BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- URL details table
CREATE TABLE url_details (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    long_url VARCHAR(2048) NOT NULL,
    short_url VARCHAR(255) UNIQUE,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    active_till TIMESTAMP,
    click_counts BIGINT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- Clicks table (analytics)
CREATE TABLE click (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    url_details_id BIGINT NOT NULL,
    clicked_at TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    FOREIGN KEY (url_details_id) REFERENCES url_details(id)
);
```

## 🔧 Advanced Configuration

### Custom Token Expiration

Edit `application.yaml`:

```yaml
jwt:
  expiration: 900000        # 15 minutes
  refresh-expiration: 2592000000  # 30 days
```

### Scheduled Token Cleanup

Expired tokens are automatically cleaned up daily at 2 AM.

To change the schedule, edit `TokenCleanupScheduler.java`:

```java
@Scheduled(cron = "0 0 3 * * ?")  // Run at 3 AM
@Scheduled(cron = "0 0 */6 * * ?")  // Every 6 hours
```

## 🛡️ Production Deployment Checklist

Before deploying to production:

- [ ] Change `jwt.secret` to a strong, random key
- [ ] Set appropriate token expiration times
- [ ] Enable HTTPS/TLS
- [ ] Configure CORS for your frontend domain
- [ ] Add rate limiting to prevent abuse
- [ ] Set up database backups
- [ ] Configure proper logging levels
- [ ] Use environment variables for all secrets
- [ ] Add monitoring and alerting
- [ ] Review and tighten security configurations
- [ ] Implement input sanitization for URLs
- [ ] Add CAPTCHA for registration (optional)
- [ ] Set up CDN for static assets
- [ ] Configure database connection pooling

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a new branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Write/update tests if applicable
5. Commit your changes (see [Commit Message Guidelines](#commit-message-guidelines))
6. Push to the branch (`git push origin feature/amazing-feature`)
7. Open a Pull Request

### Commit Message Guidelines

Follow conventional commits format:
- `feat: add refresh token system`
- `fix: resolve 401 error in protected endpoints`
- `docs: update README with authentication guide`
- `refactor: improve JWT token validation`

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Abhay Rajput**
- GitHub: [@99mint](https://github.com/99mint)
- Email: abhayyy2204@gmail.com

## 🙏 Acknowledgments

- Spring Boot and Spring Security teams
- JJWT library developers
- Base62 encoding algorithm
- Open source community

## 📞 Support

If you encounter any issues:

1. Check the [Issues](https://github.com/99mint/ByteLink/issues) page
2. Create a new issue with detailed information
3. Include error logs and steps to reproduce

## 🗺️ Roadmap

Future enhancements planned:

- [ ] Custom short URL aliases
- [ ] QR code generation for short URLs
- [ ] Analytics dashboard
- [ ] URL expiration customization
- [ ] Bulk URL shortening
- [ ] API rate limiting
- [ ] Email verification for registration
- [ ] Two-factor authentication (2FA)
- [ ] Social media login (OAuth2)
- [ ] URL preview before redirect
- [ ] Admin panel for user management

---

**Made with ❤️ using Spring Boot and Spring Security**

*Secure. Fast. Reliable.*
