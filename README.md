# Enterprise Inventory Analytics & Management System

A high-performance, custom-built REST API and relational database architecture designed to manage enterprise warehouse assets. This system enforces strict data integrity, handles secure data ingestion, and computes live business analytics.

## 🚀 Tech Stack
* **Core Language:** Java (JDK 17+)
* **Server:** Native `com.sun.net.httpserver` (Dependency-free HTTP Server)
* **Database:** PostgreSQL
* **Driver:** JDBC (Java Database Connectivity)
* **Testing & Pipeline:** Postman, Git

## ⚙️ Core Architecture & Features
* **Robust Relational Schema:** Designed with strict transactional constraints (`FOREIGN KEY`, `NOT NULL`, `UNIQUE`) across interconnected tables to maintain absolute data consistency.
* **ACID-Compliant Data Access Layer:** Developed a Java-based backend utilizing JDBC to handle relational queries, enforcing manual `commit()` and `rollback()` blocks to eliminate data anomalies and ensure safe, synchronous transactions.
* **Analytical SQL Processing:** Optimized queries involving multi-table `JOIN`s, aggregations, and subqueries to compute live business metrics, including low-stock alerts and category-wise asset valuation.
* **Backend Testing Pipeline:** Built a clean RESTful pipeline verified via Postman to simulate client-side data ingestion, validating incoming payloads natively before executing database insertions.

## 🛠️ Database Schema 
The PostgreSQL architecture utilizes a normalized relational model:
* `categories`: Manages master category reference data (e.g., Electronics, Furniture).
* `products`: Stores individual asset metrics, strictly linked via Foreign Key (`category_id`) with `CHECK` constraints to prevent negative stock quantities.

## 🔌 API Documentation

### **1. Add New Product**
Ingests a new warehouse asset into the PostgreSQL database.

* **URL:** `http://localhost:8081/add-product`
* **Method:** `POST`
* **Headers:** `Content-Type: application/json`

**Request Payload Example:**
```json
{
    "sku": "ELEC-LAP-100",
    "product_name": "MacBook Pro M4 Workstations",
    "category_id": 1,
    "price": 89000.00,
    "stock_quantity": 45,
    "low_stock_threshold": 3
}
