-- Database initialization script for BlogPress microservices
-- This script creates all required databases for the microservices

-- Create databases for each service
CREATE DATABASE IF NOT EXISTS user_service_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS blog_service_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS engagement_service_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Grant privileges to root user for all databases
GRANT ALL PRIVILEGES ON user_service_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON blog_service_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON engagement_service_db.* TO 'root'@'%';

-- Flush privileges to ensure changes take effect
FLUSH PRIVILEGES;

-- Use user_service_db and create basic tables
USE user_service_db;

-- Use blog_service_db
USE blog_service_db;

-- Use engagement_service_db
USE engagement_service_db;

-- Show created databases
SHOW DATABASES;
