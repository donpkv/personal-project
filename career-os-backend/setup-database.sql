-- Career OS Database Setup Script
-- Run this script as a PostgreSQL superuser (usually 'postgres')

-- Create database
CREATE DATABASE career_os;

-- Create user
CREATE USER career_os_user WITH PASSWORD 'career_os_pass';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE career_os TO career_os_user;

-- Connect to the career_os database and grant schema privileges
\c career_os;

-- Grant schema privileges
GRANT ALL ON SCHEMA public TO career_os_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO career_os_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO career_os_user;

-- Set default privileges for future tables
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO career_os_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO career_os_user;

-- Verify setup
SELECT current_database(), current_user;
\dt
