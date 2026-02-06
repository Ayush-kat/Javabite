-- MySQL Database Complete Dump for javabite_coffee
-- Includes schema and data


SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================
-- Table: users
-- =============================================
DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
                         `id` BIGINT NOT NULL AUTO_INCREMENT,
                         `name` VARCHAR(255) NOT NULL,
                         `email` VARCHAR(255) NOT NULL,
                         `password` VARCHAR(255) NOT NULL,
                         `role` ENUM('ADMIN', 'CUSTOMER', 'CHEF', 'WAITER') NOT NULL,
                         `enabled` TINYINT(1) NOT NULL DEFAULT 1,
                         `max_active_orders` INT DEFAULT 10,
                         `current_active_orders` INT DEFAULT 0,
                         `is_available` TINYINT(1) DEFAULT 1,
                         `invitation_token` VARCHAR(255) DEFAULT NULL,
                         `invitation_sent_at` TIMESTAMP NULL DEFAULT NULL,
                         `invitation_accepted_at` TIMESTAMP NULL DEFAULT NULL,
                         `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `email` (`email`),
                         UNIQUE KEY `invitation_token` (`invitation_token`),
                         KEY `idx_email` (`email`),
                         KEY `idx_role` (`role`),
                         KEY `idx_enabled` (`enabled`),
                         KEY `idx_invitation_token` (`invitation_token`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Data for users
INSERT INTO `users` VALUES
                        (18,'Admin User','admin@javabite.com','$2a$10$pckBxkeBGuA2L6Vfcc5TregrmqpCv6xE0gPL2f3RVybCM11emGsbO','ADMIN',1,999,0,1,NULL,NULL,NULL,'2025-12-29 13:44:57'),
                        (19,'John Chef','chef@javabite.com','$2a$10$nGea/B.6BAdxBNL7ZQxtfex1Qy0FpCE7G64uvyawgxUZypQn1VFMe','CHEF',1,10,0,1,NULL,NULL,NULL,'2025-12-29 13:44:57'),
                        (20,'Sarah Waiter','waiter@javabite.com','$2a$10$vMc5WUB2yzPd98B3Yt5EguDDkai7LamFSKhpDok98OsaVLnfvrnRm','WAITER',1,1,1,1,NULL,NULL,NULL,'2025-12-29 13:44:57'),
                        (21,'Jane Customer','customer@javabite.com','$2a$10$Vhb9bzuBFwO/Zfa06hQyiOGnW/ZlfoXfWOusJqEr4a/fvmXMkRPGS','CUSTOMER',1,5,0,1,NULL,NULL,NULL,'2025-12-29 13:44:57'),
                        (22,'you','you@javabite.com','$2a$10$ZexmpI/9ImbXuWRTKZ5z/uaaH3Oq2AjTCTNqs1GgJHZjDu2W3gyKW','CUSTOMER',1,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-30 06:55:35'),
                        (23,'test','test@javabite.com','$2a$10$QwBXzV6RztHPseH5i9UrienT563bcBp45QxfimrIn75HvxVPVQdh2','CUSTOMER',1,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-30 07:11:47'),
                        (24,'Kim','kim@javabite.com','$2a$10$kwLwl316STn6auy1X8X5yu2siRvDsORhT1dDVpgm39bNLOvyNBPpO','WAITER',1,1,1,1,NULL,'2025-12-30 14:00:05','2025-12-30 14:04:59','2025-12-30 14:00:05'),
                        (25,'Alan Chef','Alan@javabite.com','$2a$10$wVqJ94HQ4X.bKTvtvs4HHexceNrJneTUMH2c64BoX1DahH.riyqce','CHEF',1,10,0,1,NULL,'2025-12-30 14:01:36','2025-12-30 14:08:47','2025-12-30 14:01:36'),
                        (26,'Kate','Kate@javabite.com','$2a$10$aCzneBhZN1M8EOpTKeu4PO.MvJKdTaHCH.zmGbf5JQBxAWPhomoPK','WAITER',0,1,0,1,NULL,'2025-12-30 14:11:19','2025-12-30 14:14:22','2025-12-30 14:11:19'),
                        (27,'Eren Chef','Eren@javabite.com','$2a$10$hYzD6iaNg/qdubStVxaJcugG8Ez6NJgHVGOm1KgbIQ4ayaMIAxerq','CHEF',1,10,0,1,NULL,'2025-12-30 14:13:01','2025-12-30 14:15:17','2025-12-30 14:13:01'),
                        (31,'rei','rei@javabite.com','$2a$10$ti8ufyChQvPSWaI2WOMp4ePSrwaj/8PfP8ddtGzdxBD8qBpw6.pga','WAITER',0,1,0,1,NULL,'2025-12-30 15:56:03','2025-12-30 15:56:58','2025-12-30 15:56:03'),
                        (33,'ayush','ayush@javabite.com','$2a$10$/6e3yFXnIri1cM0fiP71a.euqUFiiZxqPDHn1zz4tcAdrytUUWEK.','WAITER',1,1,0,1,NULL,'2026-01-02 08:16:46','2026-01-02 08:17:28','2026-01-02 08:16:46'),
                        (34,'kartik','kartik@javabite.com','$2a$10$9.K2I.mF8qglfkMg921HZ.8ZZfaxndI2Vn19lL/Lu5eScmdt/Iaei','CHEF',1,10,0,1,NULL,'2026-01-02 08:19:21','2026-01-02 08:19:46','2026-01-02 08:19:21');

-- =============================================
-- Table: menu_items
-- =============================================
DROP TABLE IF EXISTS `menu_items`;

CREATE TABLE `menu_items` (
                              `id` BIGINT NOT NULL AUTO_INCREMENT,
                              `name` VARCHAR(255) NOT NULL,
                              `description` VARCHAR(500) DEFAULT NULL,
                              `price` DECIMAL(10,2) NOT NULL,
                              `category` ENUM('COFFEE', 'PASTRIES', 'BEVERAGES', 'SNACKS') NOT NULL,
                              `image_url` VARCHAR(255) DEFAULT NULL,
                              `available` TINYINT(1) DEFAULT 1,
                              `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
                              `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              PRIMARY KEY (`id`),
                              KEY `idx_category` (`category`),
                              KEY `idx_available` (`available`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Data for menu_items
INSERT INTO `menu_items` VALUES
                             (1,'Espresso','Strong and bold Italian',3.00,'COFFEE','https://images.unsplash.com/photo-1510591509098-f4fdc6d0ff04',1,'2025-12-23 14:38:15','2025-12-29 11:32:03'),
                             (2,'Cappuccino','Espresso with steamed milk and foam',4.50,'COFFEE','https://images.unsplash.com/photo-1572442388796-11668a67e53d',1,'2025-12-23 14:38:15','2025-12-23 14:38:15'),
                             (3,'Latte','Smooth espresso with steamed milk',4.75,'COFFEE','https://images.unsplash.com/photo-1561047029-3000c68339ca',1,'2025-12-23 14:38:15','2025-12-23 14:38:15'),
                             (4,'Croissant','Buttery French ',3.25,'PASTRIES','https://images.unsplash.com/photo-1555507036-ab1f4038808a',1,'2025-12-23 14:38:15','2026-01-02 08:10:39'),
                             (5,'Blueberry Muffin','Fresh baked with real blueberries',3.75,'PASTRIES','https://images.unsplash.com/photo-1607958996333-41aef7caefaa',1,'2025-12-23 14:38:15','2025-12-23 14:38:15'),
                             (6,'Green Tea','Refreshing Japanese green tea',2.50,'BEVERAGES','https://images.unsplash.com/photo-1556679343-c7306c1976bc',1,'2025-12-23 14:38:15','2025-12-23 14:38:15');

-- =============================================
-- Table: table_bookings
-- =============================================
DROP TABLE IF EXISTS `table_bookings`;

CREATE TABLE `table_bookings` (
                                  `id` BIGINT NOT NULL AUTO_INCREMENT,
                                  `customer_id` BIGINT NOT NULL,
                                  `booking_date` DATE NOT NULL,
                                  `booking_time` VARCHAR(255) NOT NULL,
                                  `number_of_guests` INT NOT NULL,
                                  `table_number` INT DEFAULT NULL,
                                  `status` ENUM('CONFIRMED', 'ACTIVE', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'CONFIRMED',
                                  `special_requests` VARCHAR(500) DEFAULT NULL,
                                  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
                                  `updated_at` DATETIME(6) DEFAULT NULL,
                                  `cancelled_at` TIMESTAMP NULL DEFAULT NULL COMMENT 'Timestamp when booking was cancelled',
                                  `cancellation_reason` VARCHAR(500) DEFAULT NULL COMMENT 'Reason provided when booking was cancelled',
                                  `cancelled_by` BIGINT DEFAULT NULL COMMENT 'User ID who cancelled the booking (admin or customer)',
                                  `refund_status` VARCHAR(20) DEFAULT NULL COMMENT 'Refund processing status: NONE, PENDING, COMPLETED',
                                  `refund_amount` DECIMAL(10,2) DEFAULT NULL COMMENT 'Amount to be refunded to customer',
                                  `refunded_at` TIMESTAMP NULL DEFAULT NULL COMMENT 'Timestamp when refund was completed',
                                  `user_id` BIGINT NOT NULL,
                                  PRIMARY KEY (`id`),
                                  KEY `idx_customer` (`customer_id`),
                                  KEY `idx_booking_date` (`booking_date`),
                                  KEY `idx_booking_time` (`booking_time`),
                                  KEY `idx_status` (`status`),
                                  KEY `idx_table_number` (`table_number`),
                                  KEY `idx_date_time` (`booking_date`, `booking_time`),
                                  KEY `fk_booking_cancelled_by` (`cancelled_by`),
                                  KEY `idx_table_bookings_refund_status` (`refund_status`),
                                  KEY `idx_table_bookings_cancelled_at` (`cancelled_at`),
                                  KEY `FKa8ty4fbftmd7dvjd994wmpw9t` (`user_id`),
                                  CONSTRAINT `fk_booking_cancelled_by` FOREIGN KEY (`cancelled_by`) REFERENCES `users` (`id`) ON DELETE SET NULL,
                                  CONSTRAINT `FKa8ty4fbftmd7dvjd994wmpw9t` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
                                  CONSTRAINT `table_bookings_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
                                  CONSTRAINT `chk_refund_status` CHECK (`refund_status` IN ('NONE', 'PENDING', 'COMPLETED'))
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Data for table_bookings
INSERT INTO `table_bookings` VALUES
                                 (34,22,'2025-12-31','13:30',2,14,'COMPLETED',NULL,'2025-12-30 09:44:13','2025-12-30 15:22:35.338477',NULL,NULL,NULL,'NONE',0.00,NULL,22),
                                 (35,22,'2025-12-31','12:00',2,3,'COMPLETED',NULL,'2025-12-30 14:16:34','2025-12-30 20:16:33.413149',NULL,NULL,NULL,'NONE',0.00,NULL,22),
                                 (36,23,'2025-12-31','12:00',2,3,'CANCELLED',NULL,'2025-12-30 14:33:51','2025-12-30 20:06:09.281637','2025-12-30 14:36:09','can\'t reach at time',23,'PENDING',0.00,NULL,23),
                                 (37,23,'2025-12-31','15:30',2,5,'COMPLETED',NULL,'2025-12-30 14:40:54','2025-12-30 20:23:15.615486',NULL,NULL,NULL,'NONE',0.00,NULL,23),
                                 (38,21,'2025-12-31','12:30',2,10,'COMPLETED',NULL,'2025-12-30 14:49:01','2025-12-30 20:23:33.850945',NULL,NULL,NULL,'NONE',0.00,NULL,21),
                                 (39,22,'2025-12-31','13:00',2,5,'CANCELLED',NULL,'2025-12-31 01:15:07','2025-12-31 06:48:02.909055','2025-12-31 01:18:03','Can\'t reach on time',22,'PENDING',0.00,NULL,22),
                                 (40,22,'2025-12-31','12:30',1,6,'COMPLETED',NULL,'2025-12-31 01:19:44','2025-12-31 07:17:09.922524',NULL,NULL,NULL,'NONE',0.00,NULL,22),
                                 (41,23,'2025-12-31','12:30',2,15,'COMPLETED',NULL,'2025-12-31 01:26:24','2025-12-31 07:15:53.785069',NULL,NULL,NULL,'NONE',0.00,NULL,23),
                                 (42,23,'2025-12-31','16:00',2,20,'CANCELLED',NULL,'2025-12-31 02:53:37','2025-12-31 08:23:54.992088','2025-12-31 02:53:55',NULL,NULL,'NONE',0.00,NULL,23),
                                 (43,23,'2025-12-31','17:00',2,5,'COMPLETED',NULL,'2025-12-31 05:19:45','2025-12-31 10:54:25.562881',NULL,NULL,NULL,'NONE',0.00,NULL,23),
                                 (44,21,'2026-01-01','12:00',2,4,'COMPLETED',NULL,'2025-12-31 11:48:36','2025-12-31 17:37:13.531804',NULL,NULL,NULL,'NONE',0.00,NULL,21),
                                 (45,22,'2026-01-01','12:00',2,13,'ACTIVE',NULL,'2025-12-31 12:02:50','2025-12-31 17:33:57.579862',NULL,NULL,NULL,'NONE',0.00,NULL,22),
                                 (46,21,'2026-01-01','16:30',2,12,'ACTIVE',NULL,'2026-01-01 03:42:45','2026-01-01 09:13:27.377243',NULL,NULL,NULL,'NONE',0.00,NULL,21),
                                 (47,21,'2026-01-03','13:00',2,4,'ACTIVE',NULL,'2026-01-02 08:07:14','2026-01-02 13:37:50.567385',NULL,NULL,NULL,'NONE',0.00,NULL,21);

-- =============================================
-- Table: orders
-- =============================================
DROP TABLE IF EXISTS `orders`;

CREATE TABLE `orders` (
                          `id` BIGINT NOT NULL AUTO_INCREMENT,
                          `customer_id` BIGINT NOT NULL,
                          `table_booking_id` BIGINT DEFAULT NULL,
                          `chef_id` BIGINT DEFAULT NULL,
                          `waiter_id` BIGINT DEFAULT NULL,
                          `status` ENUM('PENDING', 'PREPARING', 'READY', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
                          `special_instructions` VARCHAR(1000) DEFAULT NULL,
                          `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
                          `updated_at` TIMESTAMP NULL DEFAULT NULL COMMENT 'Last update timestamp',
                          `chef_assigned_at` TIMESTAMP NULL DEFAULT NULL,
                          `waiter_assigned_at` TIMESTAMP NULL DEFAULT NULL,
                          `preparation_started_at` TIMESTAMP NULL DEFAULT NULL,
                          `ready_at` TIMESTAMP NULL DEFAULT NULL,
                          `served_at` TIMESTAMP NULL DEFAULT NULL,
                          `completed_at` TIMESTAMP NULL DEFAULT NULL,
                          `cancelled_at` TIMESTAMP NULL DEFAULT NULL,
                          `auto_assigned` TINYINT(1) DEFAULT 0,
                          `admin_notes` VARCHAR(1000) DEFAULT NULL,
                          `payment_status` VARCHAR(255) DEFAULT NULL,
                          `payment_method` VARCHAR(255) DEFAULT NULL COMMENT 'Payment method used (CASH, CARD, UPI, etc)',
                          `transaction_id` VARCHAR(255) DEFAULT NULL COMMENT 'Transaction ID from payment gateway',
                          `paid_at` TIMESTAMP NULL DEFAULT NULL COMMENT 'Timestamp when payment was completed',
                          `subtotal` DECIMAL(10,2) DEFAULT NULL COMMENT 'Order subtotal before tax and discount',
                          `tax` DECIMAL(10,2) DEFAULT NULL COMMENT 'Tax amount',
                          `discount` DECIMAL(10,2) DEFAULT NULL COMMENT 'Discount amount applied',
                          PRIMARY KEY (`id`),
                          KEY `idx_customer` (`customer_id`),
                          KEY `idx_table_booking` (`table_booking_id`),
                          KEY `idx_chef` (`chef_id`),
                          KEY `idx_waiter` (`waiter_id`),
                          KEY `idx_status` (`status`),
                          KEY `idx_created_at` (`created_at`),
                          KEY `idx_chef_status` (`chef_id`, `status`),
                          KEY `idx_waiter_status` (`waiter_id`, `status`),
                          KEY `idx_completed_at` (`completed_at`),
                          KEY `idx_orders_payment_status` (`payment_status`),
                          KEY `idx_orders_updated_at` (`updated_at`),
                          CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
                          CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`table_booking_id`) REFERENCES `table_bookings` (`id`) ON DELETE SET NULL,
                          CONSTRAINT `orders_ibfk_3` FOREIGN KEY (`chef_id`) REFERENCES `users` (`id`) ON DELETE SET NULL,
                          CONSTRAINT `orders_ibfk_4` FOREIGN KEY (`waiter_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Data for orders
INSERT INTO `orders` VALUES
                         (47,22,34,19,20,'COMPLETED',NULL,'2025-12-30 09:44:38','2025-12-30 09:52:35','2025-12-30 09:47:19','2025-12-30 09:47:19','2025-12-30 09:50:56','2025-12-30 09:51:14','2025-12-30 09:52:35','2025-12-30 09:52:35',NULL,1,NULL,'PAID','CARD',NULL,'2025-12-30 09:44:38',4.50,0.45,0.00),
                         (48,22,35,25,24,'COMPLETED',NULL,'2025-12-30 14:19:14','2025-12-30 14:46:33','2025-12-30 14:20:53','2025-12-30 14:20:53','2025-12-30 14:26:10','2025-12-30 14:41:36','2025-12-30 14:46:33','2025-12-30 14:46:33',NULL,0,NULL,'PAID','CARD',NULL,'2025-12-30 14:19:14',20.25,2.03,0.00),
                         (49,22,35,25,24,'COMPLETED',NULL,'2025-12-30 14:30:33','2025-12-30 14:43:28','2025-12-30 14:30:33','2025-12-30 14:30:33','2025-12-30 14:41:42','2025-12-30 14:41:47','2025-12-30 14:43:28','2025-12-30 14:43:28',NULL,1,NULL,'PAID','CARD',NULL,'2025-12-30 14:30:33',3.25,0.33,0.00),
                         (50,23,37,19,20,'COMPLETED',NULL,'2025-12-30 14:41:23','2025-12-30 14:53:16','2025-12-30 14:43:51','2025-12-30 14:43:51','2025-12-30 14:51:00','2025-12-30 14:51:46','2025-12-30 14:53:16','2025-12-30 14:53:16',NULL,1,NULL,'PAID','CARD',NULL,'2025-12-30 14:41:23',8.50,0.85,0.00),
                         (51,21,38,19,24,'COMPLETED',NULL,'2025-12-30 14:49:36','2025-12-30 14:54:45','2025-12-30 14:51:51','2025-12-30 14:51:51','2025-12-30 14:53:03','2025-12-30 14:53:10','2025-12-30 14:53:34','2025-12-30 14:53:34',NULL,1,'','PAID','CARD',NULL,'2025-12-30 14:49:36',4.50,0.45,0.00),
                         (52,22,40,19,20,'COMPLETED',NULL,'2025-12-31 01:21:34','2025-12-31 01:47:10','2025-12-31 01:24:32','2025-12-31 01:24:32','2025-12-31 01:43:12','2025-12-31 01:47:02','2025-12-31 01:47:10','2025-12-31 01:47:10',NULL,1,NULL,'PAID','CARD',NULL,'2025-12-31 01:21:34',6.75,0.68,0.00),
                         (53,23,41,19,24,'COMPLETED',NULL,'2025-12-31 01:26:57','2025-12-31 01:45:54','2025-12-31 01:29:32','2025-12-31 01:29:32','2025-12-31 01:43:08','2025-12-31 01:45:38','2025-12-31 01:45:54','2025-12-31 01:45:54',NULL,1,NULL,'PAID','CARD',NULL,'2025-12-31 01:26:57',2.50,0.25,0.00),
                         (54,23,41,19,24,'COMPLETED',NULL,'2025-12-31 01:44:10','2025-12-31 01:45:50','2025-12-31 01:44:10','2025-12-31 01:44:10','2025-12-31 01:45:07','2025-12-31 01:45:42','2025-12-31 01:45:50','2025-12-31 01:45:50',NULL,1,NULL,'PAID','CARD',NULL,'2025-12-31 01:44:10',7.50,0.75,0.00),
                         (55,23,43,27,24,'COMPLETED',NULL,'2025-12-31 05:21:10','2025-12-31 05:24:26','2025-12-31 05:22:04','2025-12-31 05:22:04','2025-12-31 05:23:19','2025-12-31 05:23:48','2025-12-31 05:24:26','2025-12-31 05:24:26',NULL,0,NULL,'PAID','CARD',NULL,'2025-12-31 05:21:10',4.50,0.45,0.00),
                         (56,21,44,19,20,'COMPLETED',NULL,'2025-12-31 11:49:11','2025-12-31 12:07:14','2025-12-31 11:51:42','2025-12-31 11:51:42','2025-12-31 12:04:57','2025-12-31 12:06:39','2025-12-31 12:07:14','2025-12-31 12:07:14',NULL,1,NULL,'PAID','CARD',NULL,'2025-12-31 11:49:11',4.50,0.45,0.00),
                         (57,22,45,19,24,'PREPARING',NULL,'2025-12-31 12:03:58','2026-01-01 03:49:37','2025-12-31 12:06:42','2025-12-31 12:06:42','2026-01-01 03:49:37',NULL,NULL,NULL,NULL,1,NULL,'PAID','CARD',NULL,'2025-12-31 12:03:58',4.75,0.48,0.00),
                         (58,21,46,19,20,'READY',NULL,'2026-01-01 03:43:27','2026-01-01 03:49:04','2026-01-01 03:45:44','2026-01-01 03:45:44','2026-01-01 03:48:10','2026-01-01 03:49:04',NULL,NULL,NULL,1,NULL,'PAID','CARD',NULL,'2026-01-01 03:43:27',8.50,0.85,0.00),
                         (59,21,46,19,20,'PENDING',NULL,'2026-01-01 04:02:52','2026-01-01 04:02:52','2026-01-01 04:02:52','2026-01-01 04:02:52',NULL,NULL,NULL,NULL,NULL,1,NULL,'PAID','CARD',NULL,'2026-01-01 04:02:52',3.25,0.33,0.00),
                         (60,21,47,19,31,'COMPLETED',NULL,'2026-01-02 08:07:51','2026-01-02 08:14:30','2026-01-02 08:10:46','2026-01-02 08:10:46','2026-01-02 08:13:08','2026-01-02 08:14:17','2026-01-02 08:14:30','2026-01-02 08:14:30',NULL,1,NULL,'PAID','CARD',NULL,'2026-01-02 08:07:51',4.50,0.45,0.00),
                         (61,21,47,19,NULL,'READY',NULL,'2026-01-02 08:08:48','2026-01-02 08:14:13','2026-01-02 08:11:46',NULL,'2026-01-02 08:13:12','2026-01-02 08:14:13',NULL,NULL,NULL,1,NULL,'PAID','CARD',NULL,'2026-01-02 08:08:48',8.00,0.80,0.00);

-- =============================================
-- Table: order_items
-- =============================================
DROP TABLE IF EXISTS `order_items`;

CREATE TABLE `order_items` (
                               `id` BIGINT NOT NULL AUTO_INCREMENT,
                               `order_id` BIGINT NOT NULL,
                               `menu_item_id` BIGINT NOT NULL,
                               `quantity` INT NOT NULL DEFAULT 1,
                               `notes` VARCHAR(500) DEFAULT NULL,
                               `price_at_order` DOUBLE NOT NULL,
                               PRIMARY KEY (`id`),
                               KEY `idx_order` (`order_id`),
                               KEY `idx_menu_item` (`menu_item_id`),
                               CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
                               CONSTRAINT `order_items_ibfk_2` FOREIGN KEY (`menu_item_id`) REFERENCES `menu_items` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Data for order_items
INSERT INTO `order_items` VALUES
                              (43,47,2,1,NULL,4.5),
                              (44,48,5,3,NULL,3.75),
                              (45,48,2,2,NULL,4.5),
                              (46,49,4,1,NULL,3.25),
                              (47,50,5,1,NULL,3.75),
                              (48,50,3,1,NULL,4.75),
                              (49,51,2,1,NULL,4.5),
                              (50,52,5,1,NULL,3.75),
                              (51,52,1,1,NULL,3),
                              (52,53,6,1,NULL,2.5),
                              (53,54,5,2,NULL,3.75),
                              (54,55,2,1,NULL,4.5),
                              (55,56,2,1,NULL,4.5),
                              (56,57,3,1,NULL,4.75),
                              (57,58,5,1,NULL,3.75),
                              (58,58,3,1,NULL,4.75),
                              (59,59,4,1,NULL,3.25),
                              (60,60,2,1,NULL,4.5),
                              (61,61,3,1,NULL,4.75),
                              (62,61,4,1,NULL,3.25);

-- =============================================
-- Table: feedback
-- =============================================
DROP TABLE IF EXISTS `feedback`;

CREATE TABLE `feedback` (
                            `id` BIGINT NOT NULL AUTO_INCREMENT,
                            `order_id` BIGINT NOT NULL,
                            `customer_id` BIGINT NOT NULL,
                            `overall_rating` INT NOT NULL,
                            `food_rating` INT DEFAULT NULL,
                            `service_rating` INT DEFAULT NULL,
                            `ambiance_rating` INT DEFAULT NULL,
                            `value_rating` INT DEFAULT NULL,
                            `comment` TEXT,
                            `would_recommend` TINYINT(1) DEFAULT 1,
                            `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `order_id` (`order_id`),
                            KEY `idx_feedback_customer` (`customer_id`),
                            KEY `idx_feedback_rating` (`overall_rating`),
                            KEY `idx_feedback_created` (`created_at` DESC),
                            CONSTRAINT `feedback_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
                            CONSTRAINT `feedback_ibfk_2` FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
                            CONSTRAINT `feedback_chk_1` CHECK ((`overall_rating` >= 1) AND (`overall_rating` <= 5)),
                            CONSTRAINT `feedback_chk_2` CHECK ((`food_rating` >= 1) AND (`food_rating` <= 5)),
                            CONSTRAINT `feedback_chk_3` CHECK ((`service_rating` >= 1) AND (`service_rating` <= 5)),
                            CONSTRAINT `feedback_chk_4` CHECK ((`ambiance_rating` >= 1) AND (`ambiance_rating` <= 5)),
                            CONSTRAINT `feedback_chk_5` CHECK ((`value_rating` >= 1) AND (`value_rating` <= 5))
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Data for feedback
INSERT INTO `feedback` VALUES
                           (1,53,23,4,4,5,4,4,'Great Coffee!',1,'2025-12-31 05:05:11'),
                           (2,50,23,5,4,4,4,4,'Coffee Taste\'s really good!!',1,'2025-12-31 05:16:03'),
                           (3,55,23,4,5,4,5,4,'Great Service!!',1,'2025-12-31 05:26:25'),
                           (4,56,21,5,5,5,4,5,'Got a Place to visit often!',1,'2025-12-31 12:09:55'),
                           (5,60,21,5,4,4,4,5,'Great!',1,'2026-01-02 08:15:34'),
                           (6,51,21,4,4,4,5,4,NULL,1,'2026-01-30 00:59:44');

-- =============================================
-- Table: chef_queue
-- =============================================
DROP TABLE IF EXISTS `chef_queue`;

CREATE TABLE `chef_queue` (
                              `id` BIGINT NOT NULL AUTO_INCREMENT,
                              `order_id` BIGINT NOT NULL,
                              `chef_id` BIGINT NOT NULL,
                              `queue_position` INT NOT NULL,
                              `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `order_id` (`order_id`),
                              KEY `idx_order` (`order_id`),
                              KEY `idx_chef` (`chef_id`),
                              KEY `idx_queue_position` (`queue_position`),
                              CONSTRAINT `chef_queue_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
                              CONSTRAINT `chef_queue_ibfk_2` FOREIGN KEY (`chef_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Table: waiter_queue
-- =============================================
DROP TABLE IF EXISTS `waiter_queue`;

CREATE TABLE `waiter_queue` (
                                `id` BIGINT NOT NULL AUTO_INCREMENT,
                                `order_id` BIGINT NOT NULL,
                                `waiter_id` BIGINT NOT NULL,
                                `queue_position` INT NOT NULL,
                                `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
                                PRIMARY KEY (`id`),
                                UNIQUE KEY `order_id` (`order_id`),
                                KEY `idx_order` (`order_id`),
                                KEY `idx_waiter` (`waiter_id`),
                                KEY `idx_queue_position` (`queue_position`),
                                CONSTRAINT `waiter_queue_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
                                CONSTRAINT `waiter_queue_ibfk_2` FOREIGN KEY (`waiter_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Table: password_reset_tokens
-- =============================================
DROP TABLE IF EXISTS `password_reset_tokens`;

CREATE TABLE `password_reset_tokens` (
                                         `id` BIGINT NOT NULL AUTO_INCREMENT,
                                         `user_id` BIGINT NOT NULL,
                                         `token` VARCHAR(255) NOT NULL,
                                         `expires_at` TIMESTAMP NOT NULL,
                                         `used` TINYINT(1) DEFAULT 0,
                                         `used_at` TIMESTAMP NULL DEFAULT NULL,
                                         `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
                                         `ip_address` VARCHAR(50) DEFAULT NULL,
                                         PRIMARY KEY (`id`),
                                         UNIQUE KEY `token` (`token`),
                                         KEY `idx_password_reset_token` (`token`),
                                         KEY `idx_password_reset_user` (`user_id`),
                                         KEY `idx_password_reset_expires` (`expires_at`),
                                         KEY `idx_password_reset_used` (`used`),
                                         CONSTRAINT `password_reset_tokens_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Data for password_reset_tokens
INSERT INTO `password_reset_tokens` VALUES
                                        (1,21,'361cf9b1-e895-4f97-9d09-c9abde95af83','2025-12-31 12:30:32',1,'2025-12-31 11:34:35','2025-12-31 11:30:32','0:0:0:0:0:0:0:1'),
                                        (2,21,'2416a2d9-0585-43f3-89a5-762d101fbadd','2025-12-31 12:38:42',1,'2025-12-31 11:40:55','2025-12-31 11:38:42','0:0:0:0:0:0:0:1'),
                                        (3,21,'16072371-bc27-4392-95a1-ccabb7641d42','2025-12-31 12:40:55',1,'2025-12-31 11:43:02','2025-12-31 11:40:55','0:0:0:0:0:0:0:1');

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================
-- End of dump
-- =============================================