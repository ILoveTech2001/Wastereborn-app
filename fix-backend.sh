#!/bin/bash

echo "🔧 WasteReborn Backend Fix Script"
echo "=================================="

# Check if we're in the right directory
if [ ! -d "backend" ]; then
    echo "❌ Error: Please run this script from the WasteReborn project root directory"
    exit 1
fi

echo "📝 Fixing pom.xml..."
cd backend

# Create a proper pom.xml file
cat > pom.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>
    <groupId>com.wastereborn</groupId>
    <artifactId>wastereborn-backend</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>wastereborn-backend</name>
    <description>Backend API for WasteReborn waste management system</description>
    <properties>
        <java.version>17</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.11.5</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
EOF

echo "✅ pom.xml fixed!"

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo ""
    echo "⚠️  Maven is not installed. Please choose an option:"
    echo ""
    echo "Option 1: Install Homebrew and Maven"
    echo "  /bin/bash -c \"\$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)\""
    echo "  brew install maven"
    echo ""
    echo "Option 2: Use IntelliJ IDEA (Recommended)"
    echo "  1. Open IntelliJ IDEA"
    echo "  2. File → Open → Select the 'backend' folder"
    echo "  3. Wait for Maven import"
    echo "  4. Run WasterebornBackendApplication.java"
    echo ""
else
    echo "✅ Maven is installed!"
    
    # Check if PostgreSQL is running
    if ! command -v psql &> /dev/null; then
        echo "⚠️  PostgreSQL is not installed. Installing..."
        brew install postgresql
        brew services start postgresql
        createdb wastereborn_db
    fi
    
    echo "🚀 Starting backend..."
    mvn spring-boot:run
fi

echo ""
echo "🎉 Backend setup complete!"
echo "📖 The API will be available at: http://localhost:8080/api"
echo "🔑 Admin login: admin@wastereborn.com / admin123"
