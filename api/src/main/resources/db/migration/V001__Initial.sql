-- MariaDB dump 10.19  Distrib 10.6.12-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: flussmark
-- ------------------------------------------------------
-- Server version	10.6.12-MariaDB-0ubuntu0.22.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `app_setting`
--

DROP TABLE IF EXISTS `app_setting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `app_setting` (
  `name` varchar(255) NOT NULL,
  `the_value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `roles` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_roles` (
  `user_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  KEY `FKh8ciramu9cc9q3qcqiv4ue8a6` (`role_id`),
  KEY `FKhfh9dx7w3ubf1co1vdev94g3f` (`user_id`),
  CONSTRAINT `FKh8ciramu9cc9q3qcqiv4ue8a6` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`),
  CONSTRAINT `FKhfh9dx7w3ubf1co1vdev94g3f` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `birth_date` datetime(6) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `comment` varchar(255) DEFAULT NULL,
  `company_name` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `email_newsletter` bit(1) NOT NULL,
  `email_recovery` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_email_change` datetime(6) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `last_password_reset` datetime(6) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `password_reset` varchar(255) DEFAULT NULL,
  `post_code` varchar(255) DEFAULT NULL,
  `street` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `uid` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `verify` bit(1) NOT NULL,
  `verify_code` varchar(255) DEFAULT NULL,
  `locked_until` datetime(6) DEFAULT NULL,
  `telegram_code` varchar(255) DEFAULT NULL,
  `telegram_chat_Id` BIGINT DEFAULT NULL,
  `two_factor_code` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UK_efqukogbk7i0poucwoy2qie74` (`uid`),
  UNIQUE KEY `UK_r43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-11-07 22:32:41
