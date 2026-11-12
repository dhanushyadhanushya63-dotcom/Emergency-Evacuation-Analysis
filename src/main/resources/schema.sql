-- ==============================================================================
-- Emergency Evacuation Route Simulation and Risk Analysis
-- MySQL Database Schema
-- Database: evacuation_db
-- ==============================================================================

CREATE DATABASE IF NOT EXISTS evacuation_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE evacuation_db;

-- 1. Buildings Table
CREATE TABLE IF NOT EXISTS buildings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    total_floors INT NOT NULL DEFAULT 1,
    total_area_sqm DOUBLE NOT NULL DEFAULT 1200.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. Floors Table
CREATE TABLE IF NOT EXISTS floors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    building_id BIGINT NOT NULL,
    floor_number INT NOT NULL,
    floor_name VARCHAR(100) NOT NULL,
    canvas_width INT NOT NULL DEFAULT 1000,
    canvas_height INT NOT NULL DEFAULT 600,
    CONSTRAINT fk_floor_building FOREIGN KEY (building_id) REFERENCES buildings(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 3. Rooms Table
CREATE TABLE IF NOT EXISTS rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    floor_id BIGINT NOT NULL,
    room_code VARCHAR(50) NOT NULL,
    name VARCHAR(150) NOT NULL,
    room_type VARCHAR(80) NOT NULL,
    x DOUBLE NOT NULL,
    y DOUBLE NOT NULL,
    width DOUBLE NOT NULL,
    height DOUBLE NOT NULL,
    capacity INT NOT NULL DEFAULT 20,
    default_occupants INT NOT NULL DEFAULT 10,
    CONSTRAINT fk_room_floor FOREIGN KEY (floor_id) REFERENCES floors(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 4. Corridors Table
CREATE TABLE IF NOT EXISTS corridors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    floor_id BIGINT NOT NULL,
    corridor_code VARCHAR(50) NOT NULL,
    name VARCHAR(150) NOT NULL,
    start_x DOUBLE NOT NULL,
    start_y DOUBLE NOT NULL,
    end_x DOUBLE NOT NULL,
    end_y DOUBLE NOT NULL,
    length_meters DOUBLE NOT NULL,
    width_meters DOUBLE NOT NULL,
    max_capacity INT NOT NULL DEFAULT 50,
    is_blocked BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_corridor_floor FOREIGN KEY (floor_id) REFERENCES floors(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 5. Exits Table
CREATE TABLE IF NOT EXISTS exits (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    floor_id BIGINT NOT NULL,
    exit_code VARCHAR(50) NOT NULL,
    name VARCHAR(150) NOT NULL,
    exit_type VARCHAR(50) NOT NULL, -- EMERGENCY_EXIT, MAIN_ENTRANCE
    x DOUBLE NOT NULL,
    y DOUBLE NOT NULL,
    width_meters DOUBLE NOT NULL DEFAULT 2.0,
    flow_rate_capacity INT NOT NULL DEFAULT 40, -- persons/minute
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_exit_floor FOREIGN KEY (floor_id) REFERENCES floors(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 6. Scenarios Table
CREATE TABLE IF NOT EXISTS scenarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    scenario_type VARCHAR(80) NOT NULL,
    walking_speed DOUBLE NOT NULL DEFAULT 1.2, -- meters per second
    crowd_density_multiplier DOUBLE NOT NULL DEFAULT 1.0,
    reaction_delay_seconds INT NOT NULL DEFAULT 5,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 7. Blocked Passages Table
CREATE TABLE IF NOT EXISTS blocked_passages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    scenario_id BIGINT NOT NULL,
    corridor_id BIGINT NULL,
    exit_id BIGINT NULL,
    blockage_type VARCHAR(50) NOT NULL, -- CORRIDOR, EXIT
    reason VARCHAR(255),
    CONSTRAINT fk_bp_scenario FOREIGN KEY (scenario_id) REFERENCES scenarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_bp_corridor FOREIGN KEY (corridor_id) REFERENCES corridors(id) ON DELETE SET NULL,
    CONSTRAINT fk_bp_exit FOREIGN KEY (exit_id) REFERENCES exits(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- 8. Simulation Results Table
CREATE TABLE IF NOT EXISTS simulation_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    scenario_id BIGINT NOT NULL,
    total_evacuation_time DOUBLE NOT NULL,
    average_evacuation_time DOUBLE NOT NULL,
    total_occupants INT NOT NULL,
    safely_evacuated INT NOT NULL,
    trapped_occupants INT NOT NULL DEFAULT 0,
    peak_congestion_ratio DOUBLE NOT NULL DEFAULT 0.0,
    overall_risk_level VARCHAR(50) NOT NULL, -- LOW, MODERATE, HIGH
    simulated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sim_scenario FOREIGN KEY (scenario_id) REFERENCES scenarios(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 9. Evacuation Routes Table
CREATE TABLE IF NOT EXISTS evacuation_routes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    simulation_result_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    exit_id BIGINT NULL,
    route_path_json TEXT NOT NULL,
    total_distance_meters DOUBLE NOT NULL,
    estimated_time_seconds DOUBLE NOT NULL,
    route_type VARCHAR(50) NOT NULL DEFAULT 'PRIMARY', -- PRIMARY, ALTERNATIVE
    risk_level VARCHAR(50) NOT NULL DEFAULT 'LOW',
    CONSTRAINT fk_route_sim FOREIGN KEY (simulation_result_id) REFERENCES simulation_results(id) ON DELETE CASCADE,
    CONSTRAINT fk_route_room FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    CONSTRAINT fk_route_exit FOREIGN KEY (exit_id) REFERENCES exits(id) ON DELETE SET NULL
) ENGINE=InnoDB;
