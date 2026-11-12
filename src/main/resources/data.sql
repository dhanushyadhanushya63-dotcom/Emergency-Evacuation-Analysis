-- ==============================================================================
-- Emergency Evacuation Route Simulation and Risk Analysis
-- Demonstration Data: Sample Building (10 Rooms, 8 Corridors, 3 Exits, 1 Entrance)
-- ==============================================================================

-- 1. Sample Building
INSERT INTO buildings (id, code, name, description, total_floors, total_area_sqm)
VALUES (1, 'BLD-HQ-01', 'TechnoCenter Innovation Complex', 'Multi-tenant commercial technology complex with research laboratories, open workspaces, server infrastructure, and executive offices.', 1, 1450.0)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 2. Floor 1
INSERT INTO floors (id, building_id, floor_number, floor_name, canvas_width, canvas_height)
VALUES (1, 1, 1, 'Ground Operations Level', 1000, 600)
ON DUPLICATE KEY UPDATE floor_name=VALUES(floor_name);

-- 3. 10 Rooms
-- Viewport: 1000 x 600
-- Layout: North wing, South wing, Central spine, East lab annex, West entrance lobby
INSERT INTO rooms (id, floor_id, room_code, name, room_type, x, y, width, height, capacity, default_occupants) VALUES
(1,  1, 'RM-101', 'Executive Boardroom',       'MEETING',    60,   60, 160, 110, 25, 15),
(2,  1, 'RM-102', 'Software Engineering Lab',   'LAB',       260,   60, 200, 110, 40, 30),
(3,  1, 'RM-103', 'Conference Room Alpha',     'MEETING',   500,   60, 160, 110, 20, 12),
(4,  1, 'RM-104', 'Server & Cyber Operations',  'CRITICAL',  700,   60, 150, 110,  8,  4),
(5,  1, 'RM-105', 'Central Cafeteria & Lounge', 'COMMUNAL',  260,  430, 220, 110, 50, 35),
(6,  1, 'RM-106', 'Operations Control Center', 'CONTROL',    60,  430, 160, 110, 20, 12),
(7,  1, 'RM-107', 'Open Workspace Alpha',      'OFFICE',    520,  430, 180, 110, 35, 24),
(8,  1, 'RM-108', 'Open Workspace Beta',       'OFFICE',    740,  430, 160, 110, 25, 18),
(9,  1, 'RM-109', 'First Aid & Medical Bay',    'MEDICAL',    60,  250, 130, 100, 10,  4),
(10, 1, 'RM-110', 'Archives & Record Vault',    'STORAGE',   770,  250, 130, 100,  8,  3)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 4. 8 Corridors
-- Connecting North rooms, Central spine, South rooms, and Exits
INSERT INTO corridors (id, floor_id, corridor_code, name, start_x, start_y, end_x, end_y, length_meters, width_meters, max_capacity, is_blocked) VALUES
(1, 1, 'COR-N', 'North Concourse',        140, 200, 775, 200, 32.0, 2.5, 60, FALSE),
(2, 1, 'COR-C', 'Central Grand Spine',    460, 200, 460, 400, 20.0, 3.2, 80, FALSE),
(3, 1, 'COR-S', 'South Concourse',        140, 400, 820, 400, 34.0, 2.5, 60, FALSE),
(4, 1, 'COR-W', 'West Entrance Corridor',  50, 300, 220, 300, 15.0, 3.0, 50, FALSE),
(5, 1, 'COR-E', 'East Fire Corridor',     720, 300, 950, 300, 18.0, 2.2, 45, FALSE),
(6, 1, 'COR-1', 'North-West Branch Hall', 140, 170, 140, 300, 13.0, 2.0, 40, FALSE),
(7, 1, 'COR-2', 'South-West Branch Hall', 140, 300, 140, 430, 13.0, 2.0, 40, FALSE),
(8, 1, 'COR-3', 'East Connector Spine',   775, 200, 775, 400, 20.0, 2.2, 45, FALSE)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 5. 4 Exits (3 Emergency Exits + 1 Main Entrance)
INSERT INTO exits (id, floor_id, exit_code, name, exit_type, x, y, width_meters, flow_rate_capacity, is_available) VALUES
(1, 1, 'EX-1', 'Exit A - North Stairwell',        'EMERGENCY_EXIT', 460,  20, 2.4, 50, TRUE),
(2, 1, 'EX-2', 'Exit B - South Fire Door',        'EMERGENCY_EXIT', 460, 580, 2.4, 50, TRUE),
(3, 1, 'EX-3', 'Exit C - East Fire Escape',       'EMERGENCY_EXIT', 970, 300, 2.0, 40, TRUE),
(4, 1, 'EX-4', 'Main Entrance - West Lobby Gate', 'MAIN_ENTRANCE',   20, 300, 3.5, 75, TRUE)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 6. Predefined Scenarios
INSERT INTO scenarios (id, name, description, scenario_type, walking_speed, crowd_density_multiplier, reaction_delay_seconds, is_default) VALUES
(1, 'Normal Conditions', 'Standard operating baseline with all corridors, emergency doors, and main exits fully unobstructed.', 'NORMAL', 1.2, 1.0, 5, TRUE),
(2, 'Central Spine Fire (Single Corridor Blocked)', 'Hazardous fire outbreak in Central Grand Spine (COR-C); path sealed off by automated fire curtains.', 'CORRIDOR_BLOCKED', 1.1, 1.0, 8, FALSE),
(3, 'Multiple Corridors Blocked (Severe Smoke)', 'Dense toxic smoke spreading through North Concourse (COR-N) and East Connector (COR-3).', 'MULTI_BLOCKED', 0.9, 1.0, 10, FALSE),
(4, 'North Stairwell Inaccessible (Exit A Blocked)', 'Debris collapse blocking North Stairwell Fire Exit (EX-1); all North wing occupants redirected.', 'EXIT_BLOCKED', 1.2, 1.0, 6, FALSE),
(5, 'Peak Occupancy Event (High Crowd Density)', 'Annual tech conference taking place; occupant headcount doubled across all conference and workspace zones.', 'HIGH_DENSITY', 1.0, 2.0, 12, FALSE),
(6, 'Narrowed Capacity (Structural Repair)', 'Ongoing renovations restrict corridor widths and flow capacity across East and West wings.', 'LOW_CAPACITY', 0.85, 1.0, 7, FALSE)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 7. Blocked Passages for Predefined Scenarios
-- Scenario 2: Central Grand Spine (COR-C, ID 2) blocked
INSERT INTO blocked_passages (id, scenario_id, corridor_id, exit_id, blockage_type, reason) VALUES
(1, 2, 2, NULL, 'CORRIDOR', 'Active structural fire and heavy smoke in Central Spine');

-- Scenario 3: North Concourse (COR-N, ID 1) and East Connector (COR-3, ID 8) blocked
INSERT INTO blocked_passages (id, scenario_id, corridor_id, exit_id, blockage_type, reason) VALUES
(2, 3, 1, NULL, 'CORRIDOR', 'Heavy smoke obscuring North Concourse'),
(3, 3, 8, NULL, 'CORRIDOR', 'Electrical fire warning in East Connector');

-- Scenario 4: Exit A (EX-1, ID 1) blocked
INSERT INTO blocked_passages (id, scenario_id, corridor_id, exit_id, blockage_type, reason) VALUES
(4, 4, NULL, 1, 'EXIT', 'Debris fall blocking North Stairwell Exit A');
