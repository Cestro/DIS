-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: localhost    Database: dis
-- ------------------------------------------------------
-- Server version	5.7.21-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `address` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(45) COLLATE utf8_danish_ci NOT NULL,
  `city` varchar(45) COLLATE utf8_danish_ci NOT NULL,
  `zipcode` varchar(45) COLLATE utf8_danish_ci NOT NULL,
  `street_address` varchar(255) COLLATE utf8_danish_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COLLATE=utf8_danish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
INSERT INTO `address` VALUES (1,'Henrik Thorn','København','2200','Amagerbrogade'),(2,'ITK','København','2400','Lygten'),(3,'Henrik Thorn','København','2200','Amagerbrogade'),(4,'ITK','København','2400','Lygten'),(5,'Henrik Thorn','København','2200','Amagerbrogade'),(6,'ITK','København','2400','Lygten'),(7,'Henrik Thorn','København','2200','Amagerbrogade'),(8,'ITK','København','2400','Lygten'),(9,'Henrik Thorn','København','2200','Amagerbrogade'),(10,'ITK','København','2400','Lygten'),(11,'Henrik Thorn','København','2200','Amagerbrogade'),(12,'ITK','København','2400','Lygten'),(13,'Henrik Thorn','København','2200','Amagerbrogade'),(14,'ITK','København','2400','Lygten');
/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `line_item`
--

DROP TABLE IF EXISTS `line_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `line_item` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `product_id` int(10) unsigned NOT NULL,
  `order_id` int(10) unsigned NOT NULL,
  `price` float NOT NULL,
  `quantity` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 COLLATE=utf8_danish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `line_item`
--

LOCK TABLES `line_item` WRITE;
/*!40000 ALTER TABLE `line_item` DISABLE KEYS */;
INSERT INTO `line_item` VALUES (1,1,1,20,3),(2,2,1,25,1),(3,0,2,20,3),(4,0,2,25,1),(5,1,3,20,3),(6,2,3,25,1),(7,1,4,20,3),(8,2,4,25,1),(9,1,5,20,3),(10,2,5,25,1),(11,1,6,20,3),(12,2,6,25,1),(13,1,7,10,5);
/*!40000 ALTER TABLE `line_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `orders` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL,
  `billing_address_id` int(10) unsigned NOT NULL,
  `shipping_address_id` int(10) unsigned NOT NULL,
  `order_total` float NOT NULL DEFAULT '0',
  `created_at` int(11) NOT NULL,
  `updated_at` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COLLATE=utf8_danish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,7,1,2,80,1,1),(2,8,3,4,45,1539277496,1539277496),(3,9,5,6,45,1539277742,1539277742),(4,10,7,8,45,1539277791,1539277791),(5,11,9,10,45,1539277902,1539277902),(6,12,11,12,45,1539335661,1539335661),(7,15,13,14,35,1541519774,1541519774);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `product_name` varchar(255) COLLATE utf8_danish_ci NOT NULL,
  `sku` varchar(255) COLLATE utf8_danish_ci NOT NULL,
  `price` float NOT NULL DEFAULT '0',
  `description` varchar(255) COLLATE utf8_danish_ci DEFAULT NULL,
  `stock` int(11) unsigned NOT NULL DEFAULT '0',
  `created_at` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `sku_UNIQUE` (`sku`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_danish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,'Cola','coke',20,'Coca Cola',100,1),(2,'Fanta','fanta',25,'Fanta',50,2);
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `first_name` varchar(255) COLLATE utf8_danish_ci NOT NULL,
  `last_name` varchar(255) COLLATE utf8_danish_ci NOT NULL,
  `password` varchar(255) COLLATE utf8_danish_ci NOT NULL,
  `email` varchar(255) COLLATE utf8_danish_ci NOT NULL,
  `created_at` int(11) unsigned NOT NULL,
  `token` varchar(255) COLLATE utf8_danish_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8 COLLATE=utf8_danish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'Henrik','Thorn','abc','1@abc.dk',0,NULL),(2,' UpdateTest','Will it work?','ABCD',' 2@mail.dk',0,NULL),(3,' Malthe','The man','abc',' 3@mail.dk',1539260226,NULL),(4,'Henrik','Thorn','abc','4@abc.dk',1539260302,NULL),(5,'Henrik','Thorn','abc','5@abc.dk',1539260590,NULL),(6,'Henrik','Thorn','900150983cd24fb0d6963f7d28e17f72','6@abc.dk',1539260683,NULL),(7,'Henrik','Thorn','ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad','7@abc.dk',1539260935,NULL),(8,'Henrik','Thorn','ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad','8@abc.dk',1539277503,NULL),(9,'Henrik','Thorn','ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad','9@abc.dk',1539277743,NULL),(10,'Henrik','Thorn','ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad','10@abc.dk',1539277791,NULL),(11,'Henrik','Thorn','ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad','11@abc.dk',1539277902,NULL),(12,'Henrik','Thorn','ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad','12@abc.dk',1539335661,NULL),(14,'carl','strøm','1234','13@abc.dk',1541076290,NULL),(15,'Henrik','Thorn','e55f2b82d7f00835aa02cffebbdbff0e','14@abc.dk',1541519774,NULL),(16,' nytnavn','efternavn','73c8fc0080f8bed19e0d25c6887414b7',' 16@abc.dk',1542102521,'63498f8ad11b12791e5cc2bb77719b1b');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-11-20 14:35:25
