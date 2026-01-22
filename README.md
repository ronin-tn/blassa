# Blassa - Carpooling Platform

**Blassa** is a modern carpooling platform designed for Tunisia, connecting drivers with passengers for shared rides. Built with a Spring Boot backend, Next.js web frontend, and native Android application.

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green?logo=springboot)
![Next.js](https://img.shields.io/badge/Next.js-15-black?logo=nextdotjs)
![Android](https://img.shields.io/badge/Android-Kotlin-3DDC84?logo=android)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?logo=postgresql)

---

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Environment Variables](#environment-variables)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)

---

## Features

### Passenger Features
- Search rides by origin, destination, and date
- Book seats on available rides
- Rate and review drivers
- Receive push notifications for booking updates

### Driver Features
- Publish rides with customizable preferences
- Manage passenger requests (accept/reject)
- Track ride lifecycle (scheduled, in progress, completed)
- Access dashboard with ride statistics

### Admin Features
- Platform statistics dashboard
- User management (view, ban/unban)
- Report management system
- Detailed user and ride information

### Security & Authentication
- JWT-based authentication
- Google OAuth2 integration
- Email verification
- Role-based access control (USER, ADMIN)

---

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         Clients                                  │
├─────────────────┬─────────────────────┬─────────────────────────┤
│   Next.js Web   │    Android App      │     (Future: iOS)       │
│   (Port 3000)   │    (Native Kotlin)  │                         │
└────────┬────────┴──────────┬──────────┴─────────────────────────┘
         │                   │
         ▼                   ▼
┌─────────────────────────────────────────────────────────────────┐
│                   Spring Boot Backend                            │
│                      (Port 8088)                                 │
├─────────────────────────────────────────────────────────────────┤
│  REST API  │  WebSocket  │  OAuth2  │  JWT Auth  │  Mail        │
└────────────────────────────┬────────────────────────────────────┘
                             │
         ┌──────────────────────────────────────┐
         ▼                                      ▼
┌─────────────────┐                     ┌─────────────────┐
│   PostgreSQL    │                     │   Cloudinary    │
│   (Database)    │                     │  (Image Store)  │
└─────────────────┘                     └─────────────────┘ 
```

---

## Tech Stack

| Layer | Technology |
|-------|------------|
| **Backend** | Spring Boot 3, Spring Security, Spring Data JPA, Flyway |
| **Frontend** | Next.js 15, React 19, TypeScript, Tailwind CSS |
| **Android** | Kotlin, Jetpack Compose, Retrofit, Hilt |
| **Database** | PostgreSQL 15 with PostGIS (geospatial) |
| **Authentication** | JWT, Google OAuth2 |
| **Real-time Communication** | WebSocket (STOMP) |
| **Storage** | Cloudinary (images) |

---

## Prerequisites

Before running the project, ensure you have the following installed:

- **Java 21+** (for Spring Boot)
- **Node.js 18+** and **npm** (for Next.js)
- **PostgreSQL 15+** with PostGIS extension
- **Android Studio** (for Android development)
- **Git**

---

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/ronin-tn/blassa.git
cd blassa
```

### 2. Database Setup

Create a PostgreSQL database with PostGIS extension:

```sql
CREATE DATABASE blassa_db;
\c blassa_db
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
```

### 3. Backend Setup

The backend requires environment variables for configuration. Set them in your environment or IDE:

```bash
# Database Configuration
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=blassa_db
export DB_USERNAME=postgres
export DB_PASSWORD=your_password

# OAuth Configuration
export GOOGLE_CLIENT_ID=your_google_client_id
export GOOGLE_CLIENT_SECRET=your_google_client_secret

# JWT Configuration
export JWT_SECRET_KEY=your_256_bit_secret_key

# Email Configuration
export MAIL_USERNAME=your_email@gmail.com
export MAIL_PASSWORD=your_app_password

# Cloudinary Configuration
export CLOUDINARY_CLOUD_NAME=your_cloud_name
export CLOUDINARY_API_KEY=your_api_key
export CLOUDINARY_API_SECRET=your_api_secret

# Application URLs
export FRONTEND_URL=http://localhost:3000
export BACKEND_BASE_URL=http://localhost:8088
```

Run the backend:

```bash
./mvnw spring-boot:run
```

Alternatively, run `BlassaApplication.java` from your IDE.

The backend will start on **http://localhost:8088**.

### 4. Frontend Setup

```bash
cd front

# Copy environment template
cp .env.example .env.local

# Edit .env.local if needed (defaults work for local development)

# Install dependencies
npm install

# Run development server
npm run dev
```

The frontend will start on **http://localhost:3000**.

#### Frontend Environment Variables

Create `front/.env.local`:

```env
# API URL (backend)
NEXT_PUBLIC_API_URL=http://localhost:8088/api/v1

# WebSocket URL
NEXT_PUBLIC_WS_URL=http://localhost:8088/ws
```

### 5. Android Setup

1. Open the `android/` folder in **Android Studio**
2. Sync Gradle dependencies
3. Update `local.properties` if needed
4. Configure the API base URL in `RetrofitClient.kt`:
   ```kotlin
   private const val BASE_URL = "http://10.0.2.2:8088/api/v1/" // For emulator
   // Or use your machine's IP for physical device
   ```
5. Run on emulator or device

---

## Environment Variables

### Backend (Spring Boot)

All sensitive configuration values must be provided via environment variables.

| Variable | Required | Description | Default |
|----------|----------|-------------|---------|
| `DB_HOST` | No | Database host | `localhost` |
| `DB_PORT` | No | Database port | `5432` |
| `DB_NAME` | No | Database name | `blassa_db` |
| `DB_USERNAME` | No | Database user | `postgres` |
| `DB_PASSWORD` | **Yes** | Database password | - |
| `GOOGLE_CLIENT_ID` | **Yes** | Google OAuth client ID | - |
| `GOOGLE_CLIENT_SECRET` | **Yes** | Google OAuth client secret | - |
| `JWT_SECRET_KEY` | **Yes** | 256-bit secret key for JWT signing | - |
| `JWT_EXPIRATION` | No | Token expiration in milliseconds | `86400000` |
| `MAIL_USERNAME` | **Yes** | SMTP email address | - |
| `MAIL_PASSWORD` | **Yes** | SMTP app password | - |
| `MAIL_HOST` | No | SMTP host | `smtp.gmail.com` |
| `MAIL_PORT` | No | SMTP port | `587` |
| `CLOUDINARY_CLOUD_NAME` | **Yes** | Cloudinary cloud name | - |
| `CLOUDINARY_API_KEY` | **Yes** | Cloudinary API key | - |
| `CLOUDINARY_API_SECRET` | **Yes** | Cloudinary API secret | - |
| `FRONTEND_URL` | No | Frontend URL | `http://localhost:3000` |
| `BACKEND_BASE_URL` | No | Backend URL | `http://localhost:8088` |
| `SERVER_PORT` | No | Server port | `8088` |

### Frontend (Next.js)

Create `front/.env.local` from `front/.env.example`:

| Variable | Description |
|----------|-------------|
| `NEXT_PUBLIC_API_URL` | Backend API URL |
| `NEXT_PUBLIC_WS_URL` | WebSocket URL |

---

## Project Structure

```
blassa/
├── src/                          # Spring Boot Backend
│   ├── main/
│   │   ├── java/com/blassa/
│   │   │   ├── controller/       # REST Controllers
│   │   │   ├── service/          # Business Logic
│   │   │   ├── repository/       # Data Access
│   │   │   ├── model/            # Entities & Enums
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── security/         # Authentication & JWT
│   │   │   └── config/           # Configuration
│   │   └── resources/
│   │       ├── application.yaml  # Application Configuration
│   │       └── db/migration/     # Flyway Migrations
│   └── test/                     # Unit Tests
│
├── front/                        # Next.js Frontend
│   ├── src/
│   │   ├── app/                  # App Router Pages
│   │   ├── components/           # React Components
│   │   ├── lib/                  # Utilities & API
│   │   ├── contexts/             # React Contexts
│   │   └── types/                # TypeScript Types
│   ├── .env.example              # Environment Template
│   └── package.json
│
├── android/                      # Android Application
│   └── app/src/main/java/com/tp/blassa/
│       ├── features/             # Feature Modules
│       ├── core/                 # Network, DI, Utils
│       └── ui/                   # Theme & Components
│
├── pom.xml                       # Maven Configuration
└── README.md
```

---


## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -m 'Add some feature'`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Open a Pull Request

Please ensure your code follows the project's coding standards and includes appropriate tests.

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## Support

For issues, questions, or contributions, please open an issue on the [GitHub repository](https://github.com/ronin-tn/blassa).
