# WasteReborn - Waste Management System

WasteReborn is a comprehensive waste management system that allows users to book waste pickups, purchase recycled products, and earn points for their environmental contributions. The system includes both an Android mobile app and a Spring Boot backend API.

## Features

### User Features
- 📱 **User Authentication** - Secure login and registration
- 🗑️ **Waste Pickup Booking** - Schedule waste collection based on street routes
- 🛒 **Recycled Products Marketplace** - Browse and purchase eco-friendly products
- 🎯 **Points Reward System** - Earn 5 points per successful pickup
- 📦 **Order Tracking** - Track marketplace orders and pickup requests
- 📊 **Personal Dashboard** - View recycling stats and order history
- 🔍 **Search & Filter** - Find products by category or keywords
- 💬 **Chat Bot** - Get help and support
- 📱 **Mobile Money Integration** - Pay using Orange Money/Mobile Money
- 📈 **Progress Tracking** - Monitor recycling percentage and achievements

### Admin Features
- 👥 **User Management** - View and manage all users
- 📦 **Order Management** - Process and track all orders
- 🗑️ **Pickup Management** - Assign and track waste pickups
- 💰 **Revenue Analytics** - View total revenue and financial reports
- 🛍️ **Product Management** - Add, edit, and manage recycled products
- 📊 **Dashboard Analytics** - Comprehensive system overview

## Technology Stack

### Backend
- **Spring Boot 3.2.0** - Main framework
- **PostgreSQL** - Database
- **Spring Security + JWT** - Authentication
- **Spring Data JPA** - Data persistence
- **Maven** - Dependency management

### Android App
- **Java** - Programming language
- **Retrofit 2** - HTTP client
- **Material Design** - UI components
- **Glide** - Image loading
- **SharedPreferences** - Local storage

## Color Scheme
- **Primary Green** (#2e7d32) - Logo and main buttons
- **Sky Blue** (#87CEEB) - Accent highlights
- **Black on White** - Text and background

## Setup Instructions

### Prerequisites
- Java 17 or higher
- PostgreSQL 12 or higher
- Android Studio
- Maven 3.6 or higher

### Backend Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd WasteReborn
   ```

2. **Setup PostgreSQL Database**
   ```sql
   CREATE DATABASE wastereborn_db;
   CREATE USER wastereborn_user WITH PASSWORD 'wastereborn_pass';
   GRANT ALL PRIVILEGES ON DATABASE wastereborn_db TO wastereborn_user;
   ```

3. **Configure Database Connection**
   Update `backend/src/main/resources/application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/wastereborn_db
       username: wastereborn_user
       password: wastereborn_pass
   ```

4. **Run the Backend**
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   The API will be available at `http://localhost:8080/api`

### Android App Setup

1. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the project root directory

2. **Update API Base URL**
   In `app/src/main/java/com/example/wastereborn/api/ApiClient.java`:
   - For emulator: Use `http://10.0.2.2:8080/api/`
   - For physical device: Use `http://YOUR_COMPUTER_IP:8080/api/`

3. **Build and Run**
   - Connect your Android device or start an emulator
   - Click "Run" in Android Studio

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/signup` - User registration
- `GET /api/auth/validate` - Validate JWT token

### Products
- `GET /api/products/public/all` - Get all available products
- `GET /api/products/public/categories` - Get product categories
- `GET /api/products/public/search?keyword=` - Search products
- `POST /api/products` - Create product (Admin only)

### User Management
- `GET /api/users/profile` - Get user profile
- `PUT /api/users/profile` - Update user profile

### Orders & Pickups
- `POST /api/orders` - Create new order
- `GET /api/orders/user` - Get user orders
- `POST /api/pickups` - Create pickup request
- `GET /api/pickups/user` - Get user pickups

## Database Schema

### Key Tables
- **users** - User accounts and profiles
- **products** - Recycled products catalog
- **orders** - Marketplace orders
- **order_items** - Order line items
- **pickup_requests** - Waste pickup bookings
- **notifications** - System notifications
- **reviews** - User feedback

## Development Roadmap

### Phase 1 ✅ (Current)
- Basic authentication system
- Product marketplace
- User dashboard
- Backend API foundation

### Phase 2 🚧 (In Progress)
- Waste pickup scheduling
- Points reward system
- Order tracking
- Admin panel

### Phase 3 📋 (Planned)
- Mobile money integration
- Push notifications
- Chat bot implementation
- Advanced analytics
- Review system

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contact

For questions or support, please contact the development team.

---

**WasteReborn** - Transforming waste into opportunities for a sustainable future! 🌱♻️
