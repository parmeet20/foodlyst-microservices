-- Create user first
CREATE USER IF NOT EXISTS 'foodlyst_user'@'%' IDENTIFIED BY 'password';

-- Create databases
CREATE DATABASE IF NOT EXISTS `user` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `balance` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `transaction` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `restaurants` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `rating` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `orders` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `offer` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `foodOffer` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `zipkin` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Grant privileges
GRANT ALL PRIVILEGES ON `user`.* TO 'foodlyst_user'@'%';
GRANT ALL PRIVILEGES ON `balance`.* TO 'foodlyst_user'@'%';
GRANT ALL PRIVILEGES ON `transaction`.* TO 'foodlyst_user'@'%';
GRANT ALL PRIVILEGES ON `restaurants`.* TO 'foodlyst_user'@'%';
GRANT ALL PRIVILEGES ON `rating`.* TO 'foodlyst_user'@'%';
GRANT ALL PRIVILEGES ON `orders`.* TO 'foodlyst_user'@'%';
GRANT ALL PRIVILEGES ON `offer`.* TO 'foodlyst_user'@'%';
GRANT ALL PRIVILEGES ON `foodOffer`.* TO 'foodlyst_user'@'%';
GRANT ALL PRIVILEGES ON `zipkin`.* TO 'foodlyst_user'@'%';

FLUSH PRIVILEGES;
