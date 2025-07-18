#!/bin/bash

# WasteReborn Backend Startup Script

echo "🌱 Starting WasteReborn Backend..."

# Check if PostgreSQL is running
if ! pgrep -x "postgres" > /dev/null; then
    echo "❌ PostgreSQL is not running. Please start PostgreSQL first."
    echo "   On macOS: brew services start postgresql"
    echo "   On Ubuntu: sudo service postgresql start"
    echo "   On Windows: Start PostgreSQL service"
    exit 1
fi

# Check if database exists
DB_EXISTS=$(psql -U postgres -lqt | cut -d \| -f 1 | grep -qw wastereborn_db; echo $?)

if [ $DB_EXISTS -ne 0 ]; then
    echo "📊 Creating database..."
    psql -U postgres -c "CREATE DATABASE wastereborn_db;"
    psql -U postgres -c "CREATE USER wastereborn_user WITH PASSWORD 'wastereborn_pass';"
    psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE wastereborn_db TO wastereborn_user;"
    echo "✅ Database created successfully!"
else
    echo "✅ Database already exists"
fi

# Navigate to backend directory
cd backend

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven first."
    echo "   Visit: https://maven.apache.org/install.html"
    exit 1
fi

echo "📦 Installing dependencies..."
mvn clean install -DskipTests

echo "🚀 Starting Spring Boot application..."
mvn spring-boot:run

echo "🎉 WasteReborn Backend is now running on http://localhost:8080/api"
echo "📖 API Documentation will be available at http://localhost:8080/api/swagger-ui.html"
