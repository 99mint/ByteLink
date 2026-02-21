# 🔗 ByteLink - URL Shortener

A simple and efficient URL shortener service built with Spring Boot and MySQL.

## 📋 Table of Contents
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [API Endpoints](#api-endpoints)
- [Usage Examples](#usage-examples)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)

## ✨ Features

- ✅ Shorten long URLs to compact codes
- ✅ Base62 encoding for short URL generation
- ✅ Automatic URL expiration (90 days)
- ✅ Click analytics
- ✅ Update long URL for existing short codes
- ✅ Delete URLs
- ✅ Retrieve all short URLs for a given long URL
- ✅ Global exception handling
- ✅ Input validation
- ✅ RESTful API design

## 🛠 Tech Stack

- **Backend Framework:** Spring Boot 4.0.2
- **Language:** Java 17
- **Database:** MySQL
- **ORM:** Spring Data JPA / Hibernate
- **Build Tool:** Maven
- **Libraries:** 
  - Lombok
  - Spring Boot Validation
  - Spring Boot Web

## 📦 Prerequisites

Before running this project, make sure you have:

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Your favorite IDE (IntelliJ IDEA, Eclipse, VS Code)

## 🚀 Installation

### 1. Clone the repository
```bash
git clone https://github.com/yourusername/ByteLink.git
cd ByteLink
```

### 2. Create MySQL database
```sql
CREATE DATABASE bytelink;
```

### 3. Configure database credentials
Edit `src/main/resources/application.yaml` and update:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/bytelink
    username: your_mysql_username
    password: your_mysql_password
```

### 4. Build the project
```bash
./mvnw clean install
```

### 5. Run the application
```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

## ⚙️ Configuration

### Application Properties

Located at `src/main/resources/application.yaml`:
- Change Database password and username.

## 📡 API Endpoints

### Base URL: `http://localhost:8080/links`

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| POST | `/create` | Create a short URL | `{"longUrl": "https://example.com"}` |
| GET | `?longUrl=...` | Get all short URLs for a long URL | Query param: `longUrl` |
| PUT | `/{shortUrl}` | Update long URL for a short code | `{"longUrl": "https://newurl.com"}` |
| DELETE | `/{shortUrl}` | Delete a short URL | - |

### Redirect Endpoint

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/{shortCode}` | Redirect to original long URL |

## 💡 Usage Examples

### 1. Create Short URL

**Request:**
```bash
curl -X POST http://localhost:8080/links/create \
  -H "Content-Type: application/json" \
  -d '{"longUrl": "https://www.example.com/very/long/url/here"}'
```

**Response:**
```json
{
  "id": 1,
  "longUrl": "https://www.example.com/very/long/url/here",
  "shortUrl": "b",
  "createdAt": "2024-02-19T10:30:00",
  "expiration": "2024-05-19T10:30:00"
}
```

### 2. Access Short URL

```bash
curl http://localhost:8080/b
```
This will redirect to `https://www.example.com/very/long/url/here`

### 3. Get All Short URLs for a Long URL

**Request:**
```bash
curl "http://localhost:8080/links?longUrl=https://www.example.com/very/long/url/here"
```

**Response:**
```json
[
  {
    "id": 1,
    "longUrl": "https://www.example.com/very/long/url/here",
    "shortUrl": "b",
    "createdAt": "2024-02-19T10:30:00",
    "expiration": "2024-05-19T10:30:00"
  }
]
```

### 4. Update Long URL

**Request:**
```bash
curl -X PUT http://localhost:8080/links/b \
  -H "Content-Type: application/json" \
  -d '{"longUrl": "https://www.newexample.com"}'
```

### 5. Delete Short URL

**Request:**
```bash
curl -X DELETE http://localhost:8080/links/b
```

## 📁 Project Structure

```
ByteLink/
├── src/
│   ├── main/
│   │   ├── java/com/mint/bytelink/
│   │   │   ├── controller/
│   │   │   │   ├── RedirectController.java
│   │   │   │   └── UrlDetailsController.java
│   │   │   ├── dto/
│   │   │   │   ├── UrlDetailsRequestDTO.java
│   │   │   │   └── UrlDetailsResponseDTO.java
│   │   │   ├── entity/
│   │   │   │   └── UrlDetails.java
│   │   │   ├── exception/
│   │   │   │   ├── ErrorResponse.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   └── UrlExpiredException.java
│   │   │   ├── repository/
│   │   │   │   └── UrlDetailsRepository.java
│   │   │   ├── service/
│   │   │   │   └── UrlDetailsService.java
│   │   │   ├── util/
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

### How Short URL Generation Works

1. **User submits a long URL**
2. **System saves URL to database** (gets auto-generated ID)
3. **ID is encoded to Base62** (0-9, a-z, A-Z)
4. **Short code is saved** and returned to user

**Example:**
- ID: 123 → Base62: "1Z"
- ID: 1000 → Base62: "g8"

### Base62 Encoding

ByteLink uses Base62 encoding which produces short, URL-friendly codes using:
- Numbers: 0-9 (10 characters)
- Lowercase: a-z (26 characters)
- Uppercase: A-Z (26 characters)
- **Total: 62 characters**

This allows for billions of unique short URLs with just a few characters!

## 🔒 Security Considerations

**⚠️ Important:** Before deploying to production:

1. **Remove sensitive data from `application.yaml`**
   - Never commit database passwords
   - Use environment variables instead

2. **Add rate limiting** to prevent abuse

3. **Add authentication** if you want private URL shortening

4. **Validate URLs** to prevent malicious redirects

5. **Add HTTPS** in production

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a new branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Commit your changes (`git commit -m 'Add some amazing feature'`)
5. Push to the branch (`git push origin feature/amazing-feature`)
6. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Abhay Rajput**
- GitHub: [@99mint](https://github.com/99mint)
- Email: abhayyy2204@gmail.com

## 🙏 Acknowledgments

- Spring Boot for the amazing framework
- Base62 encoding algorithm
- Open source community

---

**Made with ❤️ using Spring Boot**
