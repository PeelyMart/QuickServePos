# QuickServePOS  
**Information Management Database Application**

QuickServePOS is a database-driven application developed as part of an **Information Management** course. The project applies **relational database design**, **SQL-based transaction handling**, and **record management principles** to a real-world domain: **restaurant front-end operations**.

The system focuses on structured data storage, transactional integrity, auditability, and reporting rather than full production-level UI complexity.

---

## 📚 Academic Context
- **Course:** Information Management (CCINFOM)
- **Institution:** De La Salle University
- **Term:** 2025 · First Year · Third Term
- **Group:** S27 · Group 12

---

### ⏱ Project Scope & Time Constraints

This project was developed within an **intensive one-month timeframe** as part of the course requirements. The instructor acknowledged that, given the limited development period, the goal was not to produce a fully production-ready system, but to demonstrate a **strong understanding of information management concepts**, relational database design, and transactional workflows.

Design decisions and feature scope were therefore intentionally balanced to prioritize **correctness, data integrity, and conceptual clarity** over completeness.

## 1.0 Group Composition
- **Philip A.**
- **Gian E.**
- **Sky P.**
- **Andie W.**

---

## 2.0 Project Overview

Restaurants rely on **Point of Sale (POS) systems** to record customer orders, manage table availability, and process payments. These systems streamline **front-end operations** such as reservations, billing, and order tracking, while also supporting back-end processes.

For this project, the group focused on **front-end restaurant operations**, as these present complex and meaningful **relational data interactions** well suited for database implementation.

The project scope includes:
- Menu management
- Employee information management
- Table management
- Customer and loyalty records
- Billing and payment processing

This scope allows exploration of relational database concepts while remaining achievable within academic constraints.

---

## 3.0 Records Management

The database is structured around **core record tables** that represent stable entities within the system.

### Menu Items
- **Attributes:** `menu_id (PK)`, name, description, price, status
- **Status values:** Available, Unavailable
- **Assigned to:** Andie W.

### Tables
- **Attributes:** `table_id (PK)`, capacity, status
- **Status values:** Available, Reserved, Occupied
- **Assigned to:** Gian E.

### Staff
- **Attributes:** `staff_id (PK)`, firstName, lastName, contactNumber, pin
- Used for authentication and authorization of privileged actions
- **Assigned to:** Sky P.

### Loyalty Members
- **Attributes:** `member_id (PK)`, firstName, lastName, contactNumber, joinDate, points, status
- **Status values:** Active, Expired
- **Assigned to:** Philip A.

---

## 4.0 Transactional Workflows

The system implements transactional workflows that reflect real-world restaurant operations.

### Transaction 1: Table Status Management
- **Assigned to:** Sky P.
- Retrieves table details and verifies current status
- Updates table status (Available, Reserved, Occupied)
- Associates occupied tables with appropriate staff

---

### Transaction 2: Billing and Payment
- **Assigned to:** Philip A.
- Retrieves order details and timestamps
- Computes final bill including taxes, discounts, and service charges
- Supports optional loyalty membership identification
- Processes multiple payment methods (cash, card, digital wallet)
- Updates payment status (Pending, Completed)
- Generates transaction receipts

---

### Transaction 3: Loyalty Member Discount
- **Assigned to:** Andie W.
- Automatically executes during billing when a valid loyalty member is detected
- Applies percentage-based discounts
- Accumulates loyalty points based on transaction value

---

### Transaction 4: Order Recording
- **Assigned to:** Gian E.
- Links orders to the currently active table
- Records individual order items linked to menu entries
- Computes subtotals and maintains running order totals

---

### Transaction 5: Reservation Management
- **Assigned to:** Philip A.
- Creates reservation records with date, time, and contact details
- Links reservations to tables via foreign keys
- Tracks reservation status (Upcoming, Ongoing, Completed, Cancelled)

---

## 5.0 Reports & Analytics

SQL-driven reports are provided to support auditing and operational analysis.

### Session Tracker / Audit Report
- **Assigned to:** Philip A.
- Records login and logout timestamps
- Associates all actions with authenticated user sessions
- Provides a verifiable audit trail

---

### Loyalty Member Enrollment & Retention Report
- **Assigned to:** Andie W.
- Tracks new loyalty members
- Supports filtering by day, week, month, and year

---

### Sales Report (Filtered by Payment Method)
- **Assigned to:** Sky P.
- Groups completed transactions by payment type
- Displays revenue totals and transaction counts
- Supports reconciliation and auditing

---

### Menu Performance Report
- **Assigned to:** Gian E.
- Reports sales quantity and value per menu item
- Supports weekly, monthly, and yearly filtering
- Assists inventory planning

---

### 🏅 Academic Recognition

The project earned a **perfect score (100%)** and was recognized by the course instructor as a **flagship project** demonstrating strong application of course concepts.


## ⚠️ Known Limitations mentioned during the final project defense 
- Reservation records are stored but not fully enforced in table availability
- Loyalty points are recorded but not redeemable
- Some non-breaking GUI callback issues exist
- Reporting interface could be expanded
- Capacity and pax matching is not strictly enforced

---



## 📄 AI Assistance Disclosure

This README was generated with the assistance of an AI language model (ChatGPT) and reviewed, edited, and verified by the project authors.  
All database design, SQL logic, and system implementation were fully developed by the team.
