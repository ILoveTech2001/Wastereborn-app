#!/bin/bash

# WasteReborn Backend Startup Script (Fixed for your system)

echo "🌱 Starting WasteReborn Backend..."

# Add PostgreSQL to PATH
export PATH="/Library/PostgreSQL/17/bin:$PATH"

# Check if PostgreSQL is running
if ! pgrep -x "postgres" > /dev/null; then
    echo "❌ PostgreSQL is not running. Starting PostgreSQL..."
    # Try to start PostgreSQL service
    sudo /Library/PostgreSQL/17/bin/pg_ctl -D /Library/PostgreSQL/17/data start || {
        echo "Failed to start PostgreSQL automatically."
        echo "Please start PostgreSQL manually:"
        echo "   sudo /Library/PostgreSQL/17/bin/pg_ctl -D /Library/PostgreSQL/17/data start"
        echo "   Or use the PostgreSQL app if you have it installed"
        exit 1
    }
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

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "📦 Maven not found. Installing Maven..."
    
    # Create a local Maven directory
    mkdir -p ~/tools
    cd ~/tools
    
    # Download Maven
    echo "Downloading Maven..."
    curl -O https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz
    
    # Extract Maven
    echo "Extracting Maven..."
    tar -xzf apache-maven-3.9.6-bin.tar.gz
    
    # Add Maven to PATH for this session
    export PATH="$HOME/tools/apache-maven-3.9.6/bin:$PATH"
    
    echo "✅ Maven installed locally"
    
    # Go back to project directory
    cd - > /dev/null
else
    echo "✅ Maven is already installed"
fi

# Navigate to backend directory
cd backend

echo "📦 Installing dependencies..."
mvn clean install -DskipTests

echo "🚀 Starting Spring Boot application..."
mvn spring-boot:run

echo "🎉 WasteReborn Backend is now running on http://localhost:8080/api"
echo "📖 API Documentation will be available at http://localhost:8080/api/swagger-ui.html"
