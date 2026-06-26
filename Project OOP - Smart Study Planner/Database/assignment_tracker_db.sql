-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3307
-- Generation Time: Jun 26, 2026 at 08:59 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `assignment_tracker_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `audit_logs`
--

CREATE TABLE `audit_logs` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `action` varchar(100) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `audit_logs`
--

INSERT INTO `audit_logs` (`id`, `user_id`, `action`, `timestamp`) VALUES
(1, 4, 'Deleted plan: Software', '2026-06-25 17:36:40'),
(2, 5, 'Recovered plan: Software Engineering Project', '2026-06-26 03:40:23'),
(3, 5, 'Recovered plan: Software Engineering Project', '2026-06-26 03:40:53'),
(4, 5, 'Deleted plan: Software Engineering Project', '2026-06-26 03:42:12');

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`id`, `name`) VALUES
(1, 'Academic'),
(2, 'Project'),
(3, 'Personal'),
(4, 'Urgent');

-- --------------------------------------------------------

--
-- Table structure for table `plans`
--

CREATE TABLE `plans` (
  `id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `plan_category` varchar(50) NOT NULL,
  `title` varchar(200) NOT NULL,
  `details` text DEFAULT NULL,
  `due_date` datetime NOT NULL,
  `reminder_duration` varchar(50) DEFAULT NULL,
  `is_active` int(11) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `plans`
--

INSERT INTO `plans` (`id`, `user_id`, `plan_category`, `title`, `details`, `due_date`, `reminder_duration`, `is_active`) VALUES
(1, 1, 'Homework', 'Lab 11 OOP', 'Database Connection', '2026-06-24 23:59:59', 'Weekly', 1),
(2, 1, 'GroupWork', 'Create Title For Mini Project', 'Open a whatsapp group first', '2026-07-03 23:59:59', 'Weekly', 1),
(3, 1, 'Assignment', 'Individu Assignment Technology Entrepreneurship', 'Sales Up to 300', '2026-06-30 23:59:59', 'Weekly', 1),
(4, 1, 'Final Project', 'Mini Project Smart Assignment Tracker System OOP', 'Java Code', '2026-07-06 23:59:59', 'None', 1),
(6, 1, 'Homework', 'HCI Portfolio', 'Design web', '2026-07-19 23:59:59', 'Daily', 1),
(11, 4, 'Final Project', 'OOP', 'Java Coding', '2026-06-30 23:59:59', 'Weekly', 0),
(12, 1, 'GroupWork', 'SE', 'Present', '2026-07-03 23:59:59', 'None', 1),
(13, 4, 'Personal', 'Revision OOP', 'Chapter 6 and 7', '2026-06-28 23:59:59', 'Daily', 1),
(14, 5, 'Project', 'OOP Project 2', 'Create Java Code and Report', '2026-07-08 23:59:59', 'Daily', 1),
(15, 5, 'Personal', 'Study', 'Read Chapter 1', '2026-06-30 23:59:59', 'Daily', 1);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `matrix_number` varchar(20) NOT NULL,
  `name` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `matrix_number`, `name`, `password`) VALUES
(1, '0001', 'Cheng XIn Hao', 'test!'),
(3, '0002', 'Ali Aku', 'test!'),
(4, '0003', 'ABANG', 'ttttt!'),
(5, '0004', 'Cheng', 'testing!');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `audit_logs`
--
ALTER TABLE `audit_logs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `plans`
--
ALTER TABLE `plans`
  ADD PRIMARY KEY (`id`),
  ADD KEY `plans_ibfk_1` (`user_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `matrix_number` (`matrix_number`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `audit_logs`
--
ALTER TABLE `audit_logs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `plans`
--
ALTER TABLE `plans`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `audit_logs`
--
ALTER TABLE `audit_logs`
  ADD CONSTRAINT `audit_logs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `plans`
--
ALTER TABLE `plans`
  ADD CONSTRAINT `plans_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
