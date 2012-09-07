-- MySQL dump 10.13  Distrib 5.1.43, for debian-linux-gnu (i486)
--
-- Host: localhost    Database: autodeploy
-- ------------------------------------------------------
-- Server version	5.1.43-1.1-log

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
-- Table structure for table `application`
--

DROP TABLE IF EXISTS `application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `application` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `alias` varchar(255) DEFAULT NULL,
  `artifact_id` varchar(255) DEFAULT NULL,
  `assemble_properties` bit(1) NOT NULL,
  `context` varchar(255) DEFAULT NULL,
  `date_created` datetime NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `filename` varchar(255) NOT NULL,
  `group_id` varchar(255) DEFAULT NULL,
  `install_dir` varchar(255) DEFAULT NULL,
  `instance_properties` bit(1) NOT NULL,
  `last_updated` datetime NOT NULL,
  `pillar_id` bigint(20) NOT NULL,
  `release_infojmxattribute` varchar(255) DEFAULT NULL,
  `release_infojmxbean` varchar(255) DEFAULT NULL,
  `start_on_deploy` bit(1) NOT NULL,
  `type` varchar(50) NOT NULL,
  `modulename` varchar(255) DEFAULT NULL,
  `balancer_type` varchar(255) DEFAULT NULL,
  `start_stop_script` varchar(255) DEFAULT NULL,
  `market_place` varchar(255) DEFAULT NULL,
  `do_probe` bit(1) DEFAULT NULL,
  `download_name` varchar(255) DEFAULT NULL,
  `properties_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5CA40550C65D6FFC` (`pillar_id`),
  CONSTRAINT `FK5CA40550C65D6FFC` FOREIGN KEY (`pillar_id`) REFERENCES `pillar` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=152 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `application_version`
--

DROP TABLE IF EXISTS `application_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `application_version` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `application_id` bigint(20) NOT NULL,
  `revision` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKC5B678E91BADAA78` (`application_id`),
  CONSTRAINT `FKC5B678E91BADAA78` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=478168 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `deployed_host`
--

DROP TABLE IF EXISTS `deployed_host`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `deployed_host` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `entry_id` bigint(20) NOT NULL,
  `host_id` bigint(20) NOT NULL,
  `message` mediumtext,
  `priority` int(11) DEFAULT '0',
  `state` varchar(255) DEFAULT 'QUEUED',
  `environment_id` bigint(20) NOT NULL,
  `duration` int(11) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `host_id` (`host_id`,`entry_id`),
  KEY `FKE65294F425912FBC` (`host_id`),
  KEY `FKE65294F48731D90` (`entry_id`),
  KEY `FKE65294F42AE6CF98` (`environment_id`),
  KEY `FK594EBA8125912FBC` (`host_id`),
  KEY `FK594EBA818731D90` (`entry_id`),
  KEY `FK594EBA812AE6CF98` (`environment_id`),
  CONSTRAINT `FK594EBA8125912FBC` FOREIGN KEY (`host_id`) REFERENCES `host` (`id`),
  CONSTRAINT `FK594EBA812AE6CF98` FOREIGN KEY (`environment_id`) REFERENCES `environment` (`id`),
  CONSTRAINT `FK594EBA818731D90` FOREIGN KEY (`entry_id`) REFERENCES `deployment_queue_entry` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=234655 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `deployment_plan`
--

DROP TABLE IF EXISTS `deployment_plan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `deployment_plan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `contribution` varchar(2000) NOT NULL,
  `date_created` datetime NOT NULL,
  `last_updated` datetime NOT NULL,
  `name` varchar(255) NOT NULL,
  `requires_database_changes` bit(1) NOT NULL,
  `requires_property_changes` bit(1) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `ticket` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `FK77A37C638E4FF79C` (`team_id`),
  CONSTRAINT `FK77A37C638E4FF79C` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=565 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `deployment_plan_application`
--

DROP TABLE IF EXISTS `deployment_plan_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `deployment_plan_application` (
  `deployment_plan_applications_id` bigint(20) NOT NULL,
  `application_id` bigint(20) DEFAULT NULL,
  KEY `FK68563741BADAA78` (`application_id`),
  KEY `FK685637456FA35EB` (`deployment_plan_applications_id`),
  CONSTRAINT `FK68563741BADAA78` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`),
  CONSTRAINT `FK685637456FA35EB` FOREIGN KEY (`deployment_plan_applications_id`) REFERENCES `deployment_plan` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `deployment_queue`
--

DROP TABLE IF EXISTS `deployment_queue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `deployment_queue` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `environment_id` bigint(20) NOT NULL,
  `frozen` bit(1) NOT NULL,
  `last_updated` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `environment_id` (`environment_id`),
  KEY `FK7CDE4F172AE6CF98` (`environment_id`),
  CONSTRAINT `FK7CDE4F172AE6CF98` FOREIGN KEY (`environment_id`) REFERENCES `environment` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `deployment_queue_entry`
--

DROP TABLE IF EXISTS `deployment_queue_entry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `deployment_queue_entry` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `queue_id` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `last_updated` datetime NOT NULL,
  `revision` varchar(255) NOT NULL,
  `comment` varchar(1000) DEFAULT NULL,
  `duration` int(11) NOT NULL DEFAULT '0',
  `state` varchar(255) NOT NULL DEFAULT 'QUEUED',
  `execution_plan_id` bigint(20) DEFAULT NULL,
  `finalized_date` datetime DEFAULT NULL,
  `creator` varchar(255) DEFAULT NULL,
  `executor` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5335CD4A42224013` (`queue_id`),
  KEY `FK5335CD4A5669C569` (`execution_plan_id`),
  CONSTRAINT `FK5335CD4A42224013` FOREIGN KEY (`queue_id`) REFERENCES `deployment_queue` (`id`),
  CONSTRAINT `FK5335CD4A5669C569` FOREIGN KEY (`execution_plan_id`) REFERENCES `execution_plan` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18822 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `environment`
--

DROP TABLE IF EXISTS `environment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `environment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `last_updated` datetime NOT NULL,
  `name` varchar(255) NOT NULL,
  `repository_id` bigint(20) DEFAULT NULL,
  `deploy_error_type` varchar(255) DEFAULT NULL,
  `use_host_class_concurrency` bit(1) DEFAULT NULL,
  `property_assembler_id` bigint(20) DEFAULT NULL,
  `secured` bit(1) DEFAULT NULL,
  `release_mail_by_default` bit(1) DEFAULT NULL,
  `auto_play_enabled` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `FKFAE1321384C5EAFC` (`repository_id`),
  KEY `FKFAE13213A875A9CD` (`property_assembler_id`),
  CONSTRAINT `FKFAE1321384C5EAFC` FOREIGN KEY (`repository_id`) REFERENCES `repository` (`id`),
  CONSTRAINT `FKFAE13213A875A9CD` FOREIGN KEY (`property_assembler_id`) REFERENCES `property_assembler` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `execution_plan`
--

DROP TABLE IF EXISTS `execution_plan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `execution_plan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `contribution` varchar(2000) NOT NULL,
  `date_created` datetime NOT NULL,
  `last_updated` datetime NOT NULL,
  `name` varchar(255) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `ticket` varchar(255) NOT NULL,
  `plan_type` varchar(255) DEFAULT 'NORMAL',
  `force_deploy` bit(1) DEFAULT NULL,
  `repository_id` bigint(20) DEFAULT NULL,
  `database_changes` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKA85967F08E4FF79C` (`team_id`),
  KEY `FKA85967F084C5EAFC` (`repository_id`),
  CONSTRAINT `FKA85967F084C5EAFC` FOREIGN KEY (`repository_id`) REFERENCES `repository` (`id`),
  CONSTRAINT `FKA85967F08E4FF79C` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18729 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `execution_plan_application_version`
--

DROP TABLE IF EXISTS `execution_plan_application_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `execution_plan_application_version` (
  `execution_plan_application_versions_id` bigint(20) DEFAULT NULL,
  `application_version_id` bigint(20) DEFAULT NULL,
  KEY `FK16CB161A4B163320` (`execution_plan_application_versions_id`),
  KEY `FK16CB161AACDA0D1B` (`application_version_id`),
  CONSTRAINT `FK16CB161A4B163320` FOREIGN KEY (`execution_plan_application_versions_id`) REFERENCES `execution_plan` (`id`),
  CONSTRAINT `FK16CB161AACDA0D1B` FOREIGN KEY (`application_version_id`) REFERENCES `application_version` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `host`
--

DROP TABLE IF EXISTS `host`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `host` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `class_name_id` bigint(20) DEFAULT NULL,
  `date_created` datetime NOT NULL,
  `environment_id` bigint(20) NOT NULL,
  `last_updated` datetime NOT NULL,
  `name` varchar(255) NOT NULL,
  `autoconf_managed` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `environment_id` (`environment_id`,`name`),
  KEY `FK30F5A82AE6CF98` (`environment_id`),
  KEY `FK30F5A8D1C1DE76` (`class_name_id`),
  CONSTRAINT `FK30F5A82AE6CF98` FOREIGN KEY (`environment_id`) REFERENCES `environment` (`id`),
  CONSTRAINT `FK30F5A8D1C1DE76` FOREIGN KEY (`class_name_id`) REFERENCES `host_class` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3430 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `host_class`
--

DROP TABLE IF EXISTS `host_class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `host_class` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `last_updated` datetime NOT NULL,
  `name` varchar(255) NOT NULL,
  `priority` int(11) NOT NULL,
  `concurrency` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=219 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `host_class_applications`
--

DROP TABLE IF EXISTS `host_class_applications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `host_class_applications` (
  `host_class_id` bigint(20) NOT NULL,
  `application_id` bigint(20) NOT NULL,
  PRIMARY KEY (`host_class_id`,`application_id`),
  KEY `FK7B6F68811BADAA78` (`application_id`),
  KEY `FK7B6F68819E7E6CE7` (`host_class_id`),
  CONSTRAINT `FK7B6F68819E7E6CE7` FOREIGN KEY (`host_class_id`) REFERENCES `host_class` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `instance`
--

DROP TABLE IF EXISTS `instance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `instance` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `host_id` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `host_id` (`host_id`,`name`),
  KEY `FK2116949525912FBC` (`host_id`),
  CONSTRAINT `FK2116949525912FBC` FOREIGN KEY (`host_id`) REFERENCES `host` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `instance_application`
--

DROP TABLE IF EXISTS `instance_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `instance_application` (
  `instance_applications_id` bigint(20) DEFAULT NULL,
  `application_id` bigint(20) DEFAULT NULL,
  KEY `FKF9ABC0A61BADAA78` (`application_id`),
  KEY `FKF9ABC0A6206378A4` (`instance_applications_id`),
  CONSTRAINT `FKF9ABC0A61BADAA78` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`),
  CONSTRAINT `FKF9ABC0A6206378A4` FOREIGN KEY (`instance_applications_id`) REFERENCES `instance` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pillar`
--

DROP TABLE IF EXISTS `pillar`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pillar` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `last_updated` datetime NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=93 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `platform`
--

DROP TABLE IF EXISTS `platform`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `platform` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `last_updated` datetime NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `production_platform_environment_map`
--

DROP TABLE IF EXISTS `production_platform_environment_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `production_platform_environment_map` (
  `date_created` datetime NOT NULL,
  `platform` varchar(100) NOT NULL,
  `environment_id` bigint(20) NOT NULL,
  PRIMARY KEY (`platform`),
  UNIQUE KEY `environment` (`platform`,`environment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_assembler`
--

DROP TABLE IF EXISTS `property_assembler`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_assembler` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `config_assembler_url` varchar(255) DEFAULT NULL,
  `date_created` datetime NOT NULL,
  `last_updated` datetime NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `repository`
--

DROP TABLE IF EXISTS `repository`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `repository` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `base_url` varchar(255) DEFAULT NULL,
  `date_created` datetime NOT NULL,
  `last_updated` datetime NOT NULL,
  `name` varchar(255) NOT NULL,
  `type` varchar(18) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sec_role`
--

DROP TABLE IF EXISTS `sec_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sec_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `authority` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `authority` (`authority`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sec_user`
--

DROP TABLE IF EXISTS `sec_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sec_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `account_expired` bit(1) NOT NULL,
  `account_locked` bit(1) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `password` varchar(255) NOT NULL,
  `password_expired` bit(1) NOT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sec_user_sec_role`
--

DROP TABLE IF EXISTS `sec_user_sec_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sec_user_sec_role` (
  `sec_role_id` bigint(20) NOT NULL,
  `sec_user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`sec_role_id`,`sec_user_id`),
  KEY `FK6630E2A3A52059B` (`sec_user_id`),
  KEY `FK6630E2A952741BB` (`sec_role_id`),
  CONSTRAINT `FK6630E2A3A52059B` FOREIGN KEY (`sec_user_id`) REFERENCES `sec_user` (`id`),
  CONSTRAINT `FK6630E2A952741BB` FOREIGN KEY (`sec_role_id`) REFERENCES `sec_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `team`
--

DROP TABLE IF EXISTS `team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `team` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `full_name` varchar(255) NOT NULL,
  `last_updated` datetime NOT NULL,
  `short_name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2012-09-07 13:53:14
