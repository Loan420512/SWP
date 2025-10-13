# EVSwap – Electric Vehicle Battery Swap System

## Overview

**EVSwap** is an Electric Vehicle Battery Swap Management System designed to simplify how users manage their EV batteries.  
The platform enables users to **register and manage accounts**, **link vehicles**, **view battery information**, **schedule battery swaps**, and **track transaction history**.  
Administrators can manage users, vehicles, stations, and monitor the entire swapping network.

---

## Key Features

### User
- Register, log in, and update personal information.  
- Manage registered vehicles.  
- View current battery status and swap history.  
- Book battery swaps at nearby stations.  

### Admin
- Manage users, vehicles, and stations.  
- Add, update, or remove battery swap stations.  
- Monitor swap transactions and system reports.  
- Assign roles (User/Admin).  

---

## Database Structure

The system uses **Microsoft SQL Server** with the following main tables:
- `Users` – stores user account information  
- `Vehicles` – stores vehicle details (VIN, model, battery type, owner, etc.)  
- `Stations` – stores swap station information  
- `Batteries` – stores details and status of batteries  
- `Swaps` – stores swap history and transaction records  

---

## Technologies Used

| Component | Technology |
|------------|-------------|
| **Backend** | Spring Boot (Java) |
| **Frontend** | React + Vite |
| **Database** | Microsoft SQL Server |
| **API** | RESTful API (JSON) |
| **Authentication** | JWT (JSON Web Token) |
| **IDE** | IntelliJ IDEA / VS Code |
| **Version Control** | Git + GitHub |

