# Banking System - Client Repository

## 📋 Repository Overview
This repository contains the **Frontend Desktop Application** for the Banking System. It includes all Graphical User Interfaces (Forms) and connects to the separate Server repository to perform operations.

## 🛠️ Tech Stack
* **Java Swing:** For the user interface (designed with `GridBagLayout` and `BorderLayout`).
* **Java RMI:** To invoke methods on the remote Server.

## 📦 Modules Included
* **Customer Management:** Registration and editing (CRUD).
* **Account Services:** creating accounts and checking balances.
* **Loan System:** Application and status tracking.
* **Transactions & Cards:** Processing transfers and issuing cards.

## 🚀 How to Run
1.  **Prerequisite:** Ensure the **Server Repository** is already running on `localhost`.
2.  Open this project in **NetBeans** or **IntelliJ**.
3.  **Clean and Build** to compile the classes.
4.  Run any of the main Form files to start the app:
    * `view.CustomerForm` (Recommended entry point)
    * `view.AccountForm`
    * `view.LoanForm`
    * `view.TransactionForm`

## 🧩 Connection Info
This client is configured to look for the server at:
* **Host:** `localhost`
* **Port:** `1100`
* **Service Name:** `BankingService`
