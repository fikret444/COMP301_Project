-- Create databases for different services
CREATE DATABASE user_db;
CREATE DATABASE rental_db;
CREATE DATABASE payment_db;

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE user_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE rental_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE payment_db TO postgres;

