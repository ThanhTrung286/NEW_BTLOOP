/*M!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.19-12.0.2-MariaDB, for Linux (x86_64)
--
-- Host: localhost    Database: library
-- ------------------------------------------------------
-- Server version	12.0.2-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*M!100616 SET @OLD_NOTE_VERBOSITY=@@NOTE_VERBOSITY, NOTE_VERBOSITY=0 */;

--
-- Table structure for table `book`
--

DROP TABLE IF EXISTS `book`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `book` (
  `ID` varchar(10) NOT NULL,
  `Publisher` varchar(255) DEFAULT NULL,
  `NumberOfPages` int(11) DEFAULT NULL,
  `Genre` varchar(100) DEFAULT NULL,
  `ISBN` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  CONSTRAINT `book_ibfk_1` FOREIGN KEY (`ID`) REFERENCES `document` (`ID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `book`
--

LOCK TABLES `book` WRITE;
/*!40000 ALTER TABLE `book` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `book` VALUES
('BOK000001','NXB',0,'TL','9999999999999'),
('BOK000002','SACH',0,'SACH','9999999999999'),
('BOK000003','A',0,'A','9999999999999');
/*!40000 ALTER TABLE `book` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `borrow_record`
--

DROP TABLE IF EXISTS `borrow_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `borrow_record` (
  `RecordID` varchar(10) NOT NULL,
  `UserID` varchar(10) NOT NULL,
  `DocumentID` varchar(10) NOT NULL,
  `BorrowDate` date NOT NULL,
  `ExpectedReturnDate` date NOT NULL,
  `ActualReturnDate` date DEFAULT NULL,
  PRIMARY KEY (`RecordID`),
  KEY `UserID` (`UserID`),
  KEY `DocumentID` (`DocumentID`),
  CONSTRAINT `borrow_record_ibfk_1` FOREIGN KEY (`UserID`) REFERENCES `user` (`UserID`) ON DELETE CASCADE,
  CONSTRAINT `borrow_record_ibfk_2` FOREIGN KEY (`DocumentID`) REFERENCES `document` (`ID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `borrow_record`
--

LOCK TABLES `borrow_record` WRITE;
/*!40000 ALTER TABLE `borrow_record` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `borrow_record` VALUES
('BRW000001','USR000001','BOK000001','2025-08-19','2025-08-29','2025-08-19'),
('BRW000002','USR000001','BOK000001','2025-08-19','2025-08-29',NULL),
('BRW000003','USR000001','BOK000002','2025-08-19','2025-08-29',NULL),
('BRW000004','USR000001','BOK000002','2025-08-19','2025-08-29',NULL),
('BRW000005','USR000001','BOK000001','2025-08-19','2025-08-20','2025-08-19'),
('BRW000006','USR000001','BOK000001','2025-08-19','2025-08-20',NULL),
('BRW000007','USR000001','BOK000001','2025-08-19','2025-08-21',NULL);
/*!40000 ALTER TABLE `borrow_record` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `document`
--

DROP TABLE IF EXISTS `document`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `document` (
  `ID` varchar(10) NOT NULL,
  `Title` varchar(255) NOT NULL,
  `Author` varchar(255) NOT NULL,
  `Total` int(11) NOT NULL,
  `Available` int(11) NOT NULL,
  `Year` int(11) DEFAULT NULL,
  `DocType` enum('BOOK','THESIS') NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `document`
--

LOCK TABLES `document` WRITE;
/*!40000 ALTER TABLE `document` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `document` VALUES
('BOK000001','SACH','TAC GIA',0,-3,0,'BOOK'),
('BOK000002','SACH','SACH',0,-2,0,'BOOK'),
('BOK000003','A','A',0,0,0,'BOOK'),
('THS000001','Luận văn mới','Tác giả luận văn',10,10,2020,'THESIS');
/*!40000 ALTER TABLE `document` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `thesis`
--

DROP TABLE IF EXISTS `thesis`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `thesis` (
  `ID` varchar(10) NOT NULL,
  `Supervisor` varchar(127) DEFAULT NULL,
  `Department` varchar(127) DEFAULT NULL,
  `University` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  CONSTRAINT `thesis_ibfk_1` FOREIGN KEY (`ID`) REFERENCES `document` (`ID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `thesis`
--

LOCK TABLES `thesis` WRITE;
/*!40000 ALTER TABLE `thesis` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `thesis` VALUES
('THS000001','Người hướng dẫn','Khoa Công nghệ thông tin','Đại học Bách Khoa');
/*!40000 ALTER TABLE `thesis` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `UserID` varchar(10) NOT NULL,
  `Name` varchar(20) NOT NULL,
  `Email` varchar(30) NOT NULL,
  `BorrowedDoc` int(11) NOT NULL,
  `BorrowedLimit` int(11) NOT NULL,
  PRIMARY KEY (`UserID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `user` VALUES
('USR000001','L','lol',0,1),
('USR000002','a','a',0,1);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
commit;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*M!100616 SET NOTE_VERBOSITY=@OLD_NOTE_VERBOSITY */;

-- Dump completed on 2025-08-22  1:02:00
