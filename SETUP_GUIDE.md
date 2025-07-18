# WasteReborn Setup Guide

This guide will help you set up and run the WasteReborn waste management system on your local machine.

## Prerequisites

### Required Software
- **Java 17 or higher** - [Download here](https://adoptium.net/)
- **PostgreSQL 12 or higher** - [Download here](https://www.postgresql.org/download/)
- **Maven 3.6 or higher** - [Download here](https://maven.apache.org/download.cgi)
- **Android Studio** - [Download here](https://developer.android.com/studio)
- **Git** - [Download here](https://git-scm.com/downloads)

### System Requirements
- **RAM**: 8GB minimum, 16GB recommended
- **Storage**: 5GB free space
- **OS**: Windows 10+, macOS 10.14+, or Ubuntu 18.04+

## Quick Start

### 1. Clone the Repository
```bash
git clone <your-repository-url>
cd WasteReborn
```

### 2. Database Setup

#### Option A: Automatic Setup (Recommended)
```bash
# Make the script executable and run it
chmod +x run-backend.sh
./run-backend.sh
```

#### Option B: Manual Setup
```bash
# Start PostgreSQL service
# macOS: brew services start postgresql
# Ubuntu: sudo service postgresql start
# Windows: Start PostgreSQL service from Services

# Create database and user
psql -U postgres
CREATE DATABASE wastereborn_db;
CREATE USER wastereborn_user WITH PASSWORD 'wastereborn_pass';
GRANT ALL PRIVILEGES ON DATABASE wastereborn_db TO wastereborn_user;
\q
```

### 3. Backend Setup
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

The backend will start on `http://localhost:8080/api`

### 4. Android App Setup

1. **Open Android Studio**
2. **Open Project** → Navigate to the WasteReborn folder
3. **Wait for Gradle sync** to complete
4. **Update API URL** in `app/src/main/java/com/example/wastereborn/api/ApiClient.java`:
   - For emulator: `http://10.0.2.2:8080/api/`
   - For physical device: `http://YOUR_COMPUTER_IP:8080/api/`
5. **Run the app** on your device/emulator

## Configuration

### Backend Configuration
Edit `backend/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/wastereborn_db
    username: wastereborn_user
    password: wastereborn_pass
```

### Android Configuration
Edit `app/src/main/java/com/example/wastereborn/api/ApiClient.java`:

```java
private static final String BASE_URL = "http://YOUR_IP:8080/api/";
```

## Default Accounts

### Admin Account
- **Email**: admin@wastereborn.com
- **Password**: password
- **Role**: Admin

### Test User Account
- **Email**: john@example.com
- **Password**: password
- **Role**: User

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/signup` - User registration

### Products
- `GET /api/products/public/all` - Get all products
- `GET /api/products/public/categories` - Get categories
- `GET /api/products/public/search?keyword=` - Search products

### Pickups
- `POST /api/pickups` - Create pickup request
- `GET /api/pickups/user` - Get user pickups

### Admin
- `GET /api/admin/dashboard` - Dashboard statistics
- `GET /api/admin/users` - Manage users
- `GET /api/admin/revenue` - Revenue reports

## Features Implemented

### ✅ User Features
- User registration and login
- Product marketplace browsing
- Shopping cart functionality
- Waste pickup booking
- Points reward system
- Order tracking
- Chat bot assistance
- User dashboard with stats

### ✅ Admin Features
- User management
- Product management
- Order management
- Pickup request management
- Revenue analytics
- Dashboard overview

### ✅ Technical Features
- JWT authentication
- RESTful API
- PostgreSQL database
- Material Design UI
- Offline data caching
- Error handling

## Troubleshooting

### Common Issues

#### Backend Won't Start
```bash
# Check if PostgreSQL is running
ps aux | grep postgres

# Check if port 8080 is available
lsof -i :8080

# Check Java version
java -version
```

#### Android App Can't Connect
1. Check if backend is running: `curl http://localhost:8080/api/products/public/all`
2. Update API URL in ApiClient.java
3. Check device/emulator network connectivity
4. Disable firewall temporarily for testing

#### Database Connection Issues
```bash
# Test database connection
psql -h localhost -U wastereborn_user -d wastereborn_db

# Reset database
DROP DATABASE wastereborn_db;
CREATE DATABASE wastereborn_db;
```

### Performance Tips
- Use SSD storage for better database performance
- Allocate more RAM to Android Studio (8GB+)
- Close unnecessary applications
- Use physical device for better testing experience

## Development Workflow

### Adding New Features
1. Create feature branch: `git checkout -b feature/new-feature`
2. Implement backend API endpoints
3. Update Android app to use new endpoints
4. Test thoroughly
5. Create pull request

### Database Changes
1. Update entity models in `backend/src/main/java/com/wastereborn/model/`
2. Spring Boot will auto-update schema (ddl-auto: update)
3. For production, use proper migrations

### Testing
```bash
# Backend tests
cd backend
mvn test

# Android tests
./gradlew test
```

## Deployment

### Backend Deployment
1. Build JAR: `mvn clean package`
2. Deploy to cloud service (Heroku, AWS, etc.)
3. Update database configuration for production

### Android Deployment
1. Generate signed APK in Android Studio
2. Upload to Google Play Store or distribute directly

## Support

### Getting Help
- Check the troubleshooting section above
- Review API documentation
- Check application logs
- Create an issue in the repository

### Contributing
1. Fork the repository
2. Create feature branch
3. Make changes
4. Test thoroughly
5. Submit pull request

## Next Steps

### Planned Features
- Push notifications
- Mobile money integration
- Advanced analytics
- Multi-language support
- Offline mode improvements

### Scaling Considerations
- Database optimization
- API rate limiting
- Caching strategies
- Load balancing
- Monitoring and logging

---

**Happy coding! 🌱♻️**

For questions or support, please refer to the project documentation or create an issue in the repository.
