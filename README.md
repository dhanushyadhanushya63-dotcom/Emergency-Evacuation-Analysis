# 🚨 Emergency Evacuation Route Simulation and Risk Analysis System

An intelligent, full-stack building safety simulation platform designed to model human evacuation dynamics, compute optimal egress routes during disasters, identify high-risk bottlenecks, and generate comprehensive safety audit reports.

---

## 🌟 Key Features

- **Interactive 2D Floor Plan with Real-Time Particle Animation**:
  - Live HTML5 Canvas rendering of rooms, hallways, doors, and emergency exits.
  - Animated evacuee agents moving along computed safe paths in real time.
- **Dual Pathfinding Algorithms**:
  - **Dijkstra's Algorithm**: Weighted shortest path optimization taking corridor lengths, capacity, hazard proximity, and dynamic congestion into account.
  - **Breadth-First Search (BFS)**: Unweighted shortest-hop baseline routing.
- **Dynamic Risk & Bottleneck Analysis**:
  - Real-time detection of high-risk corridors where density exceeds capacity thresholds.
  - Automatic calculation of exit utilization balance and congestion risk scores.
- **Multi-Hazard Emergency Scenarios**:
  - Pre-configured realistic scenarios: *Fire in Main Corridor*, *Chemical Hazard in Science Lab*, *Earthquake Exit Blockage*, *Active Threat Lockdown*, *Power Outage*, and *Standard Evacuation Drill*.
- **Scenario Comparison Matrix**:
  - Side-by-side performance benchmarking (Total Clearance Time, Peak Congestion, High-Risk Corridors, Exit Overload).
- **Printable Emergency Safety Audit Reports**:
  - Comprehensive safety compliance reports formatted for direct PDF export or printing.
- **Building Management & Operator Portal**:
  - Admin controls to inspect rooms, modify corridor capacities, add simulated obstacles/blockages, and trigger immediate evacuations.

---

## 🛠️ Technology Stack

- **Backend**:
  - **Java 21**
  - **Spring Boot 3.3.5**
  - **Spring Data JPA / Hibernate**
  - **H2 In-Memory Database** (Default zero-config runtime) / **MySQL 8.x** (Production ready)
  - **Maven** build automation
- **Frontend**:
  - **HTML5 & CSS3** with modern responsive design
  - **Bootstrap 5.3** & **FontAwesome Icons**
  - **Vanilla JavaScript (ES6 Modules)**
  - **Chart.js** for risk analytics & scenario benchmarking charts
  - **HTML5 Canvas 2D API** for high-performance agent particle rendering
- **API Architecture**:
  - RESTful JSON endpoints (`/api/buildings`, `/api/floors`, `/api/rooms`, `/api/corridors`, `/api/exits`, `/api/scenarios`, `/api/simulation`, `/api/risk`, `/api/comparison`, `/api/reports`)

---

## 📂 Project Structure

```
├── frontend/                     # Standalone frontend UI files
│   ├── index.html                # Main single-page application
│   ├── css/                      # Stylesheets (style.css, print.css)
│   └── js/                       # Modular scripts (api, floorplan, simulation, risk, etc.)
├── src/
│   ├── main/
│   │   ├── java/com/evacuation/
│   │   │   ├── config/           # Web & Database configurations
│   │   │   ├── controller/       # Spring MVC REST Controllers
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── entity/           # JPA Entities (Building, Floor, Room, Corridor, Exit, Scenario)
│   │   │   ├── exception/        # Global Exception Handlers
│   │   │   ├── repository/       # Spring Data JPA Repositories
│   │   │   ├── service/          # Business logic services
│   │   │   │   ├── algorithm/    # Dijkstra, BFS, and EvacuationEngine simulation
│   │   │   │   └── init/         # Database Seeder (DataInitializer)
│   │   │   └── EvacuationApplication.java # Spring Boot Main Class
│   │   └── resources/
│   │       ├── application.properties # Application configuration
│   │       ├── schema.sql        # MySQL table definitions
│   │       ├── data.sql          # Sample building seed data
│   │       └── static/           # Spring Boot packaged UI assets
│   └── test/                     # Unit & Integration Tests (JUnit 5)
├── pom.xml                       # Maven build descriptor
└── README.md                     # Project documentation
```

---

## 🚀 Getting Started

### Prerequisites
- **Java Development Kit (JDK 17 or 21+)** installed
- **Apache Maven 3.8+** (or bundled Maven wrapper)
- Modern web browser (Chrome, Edge, Firefox, or Safari)

### Quick Run

1. **Clone the repository**:
   ```bash
   git clone https://github.com/dhanushyadhanushya63-dotcom/Emergency-Evacuation-Analysis.git
   cd Emergency-Evacuation-Analysis
   ```

2. **Run with Maven**:
   ```bash
   mvn spring-boot:run
   ```

3. **Open the Application**:
   Navigate to [http://localhost:8080](http://localhost:8080) in your web browser.

---

## 📊 Simulation Algorithms Overview

### 1. Dijkstra Pathfinder (`DijkstraPathfinder.java`)
Calculates the optimal evacuation path from any room to the nearest safe emergency exit:
$$\text{Cost}(e) = \text{Distance}(e) \times \left(1 + \frac{\text{Current Occupancy}}{\text{Capacity}}\right) \times \text{HazardMultiplier}$$
Blocked corridors or hazardous zones are assigned infinite weight ($\infty$), forcing the algorithm to route evacuees through safe alternatives.

### 2. Evacuation Engine (`EvacuationEngine.java`)
Performs iterative discrete-time evacuation simulation:
- Simulates human velocity (default $1.2 \text{ m/s}$).
- Calculates corridor flow rates based on width and human physical clearance.
- Detects bottleneck queues and computes dynamic congestion delay times.

---

## 📄 License
This project is developed for educational, academic, and safety research purposes.
