package com.evacuation.service.init;

import com.evacuation.entity.*;
import com.evacuation.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Automatically seeds the sample building, 10 rooms, 8 corridors, 4 exits,
 * and 6 emergency scenarios on application launch if database is unseeded.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final BuildingRepository buildingRepository;
    private final FloorRepository floorRepository;
    private final RoomRepository roomRepository;
    private final CorridorRepository corridorRepository;
    private final ExitRepository exitRepository;
    private final ScenarioRepository scenarioRepository;
    private final BlockedPassageRepository blockedPassageRepository;

    public DataInitializer(BuildingRepository buildingRepository,
                           FloorRepository floorRepository,
                           RoomRepository roomRepository,
                           CorridorRepository corridorRepository,
                           ExitRepository exitRepository,
                           ScenarioRepository scenarioRepository,
                           BlockedPassageRepository blockedPassageRepository) {
        this.buildingRepository = buildingRepository;
        this.floorRepository = floorRepository;
        this.roomRepository = roomRepository;
        this.corridorRepository = corridorRepository;
        this.exitRepository = exitRepository;
        this.scenarioRepository = scenarioRepository;
        this.blockedPassageRepository = blockedPassageRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (buildingRepository.count() > 0) {
            log.info("Database already populated with {} building(s). Skipping initialization.", buildingRepository.count());
            return;
        }

        log.info("Populating database with sample emergency evacuation demonstration building and scenarios...");

        // 1. Building
        Building building = new Building("BLD-HQ-01", "TechnoCenter Innovation Complex",
                "Multi-tenant commercial technology complex with research laboratories, open workspaces, server infrastructure, and executive offices.",
                1, 1450.0);
        building = buildingRepository.save(building);

        // 2. Floor 1
        Floor floor = new Floor(building, 1, "Ground Operations Level", 1000, 600);
        floor = floorRepository.save(floor);

        // 3. 10 Rooms
        Room rm101 = new Room(floor, "RM-101", "Executive Boardroom",       "MEETING",    60.0,   60.0, 160.0, 110.0, 25, 15);
        Room rm102 = new Room(floor, "RM-102", "Software Engineering Lab",   "LAB",       260.0,   60.0, 200.0, 110.0, 40, 30);
        Room rm103 = new Room(floor, "RM-103", "Conference Room Alpha",     "MEETING",   500.0,   60.0, 160.0, 110.0, 20, 12);
        Room rm104 = new Room(floor, "RM-104", "Server & Cyber Operations",  "CRITICAL",  700.0,   60.0, 150.0, 110.0,  8,  4);
        Room rm105 = new Room(floor, "RM-105", "Central Cafeteria & Lounge", "COMMUNAL",  260.0,  430.0, 220.0, 110.0, 50, 35);
        Room rm106 = new Room(floor, "RM-106", "Operations Control Center", "CONTROL",    60.0,  430.0, 160.0, 110.0, 20, 12);
        Room rm107 = new Room(floor, "RM-107", "Open Workspace Alpha",      "OFFICE",    520.0,  430.0, 180.0, 110.0, 35, 24);
        Room rm108 = new Room(floor, "RM-108", "Open Workspace Beta",       "OFFICE",    740.0,  430.0, 160.0, 110.0, 25, 18);
        Room rm109 = new Room(floor, "RM-109", "First Aid & Medical Bay",    "MEDICAL",    60.0,  250.0, 130.0, 100.0, 10,  4);
        Room rm110 = new Room(floor, "RM-110", "Archives & Record Vault",    "STORAGE",   770.0,  250.0, 130.0, 100.0,  8,  3);

        roomRepository.save(rm101); roomRepository.save(rm102); roomRepository.save(rm103);
        roomRepository.save(rm104); roomRepository.save(rm105); roomRepository.save(rm106);
        roomRepository.save(rm107); roomRepository.save(rm108); roomRepository.save(rm109);
        roomRepository.save(rm110);

        // 4. 8 Corridors
        Corridor corN = new Corridor(floor, "COR-N", "North Concourse",        140.0, 200.0, 775.0, 200.0, 32.0, 2.5, 60, false);
        Corridor corC = new Corridor(floor, "COR-C", "Central Grand Spine",    460.0, 200.0, 460.0, 400.0, 20.0, 3.2, 80, false);
        Corridor corS = new Corridor(floor, "COR-S", "South Concourse",        140.0, 400.0, 820.0, 400.0, 34.0, 2.5, 60, false);
        Corridor corW = new Corridor(floor, "COR-W", "West Entrance Corridor",  50.0, 300.0, 220.0, 300.0, 15.0, 3.0, 50, false);
        Corridor corE = new Corridor(floor, "COR-E", "East Fire Corridor",     720.0, 300.0, 950.0, 300.0, 18.0, 2.2, 45, false);
        Corridor cor1 = new Corridor(floor, "COR-1", "North-West Branch Hall", 140.0, 170.0, 140.0, 300.0, 13.0, 2.0, 40, false);
        Corridor cor2 = new Corridor(floor, "COR-2", "South-West Branch Hall", 140.0, 300.0, 140.0, 430.0, 13.0, 2.0, 40, false);
        Corridor cor3 = new Corridor(floor, "COR-3", "East Connector Spine",   775.0, 200.0, 775.0, 400.0, 20.0, 2.2, 45, false);

        corN = corridorRepository.save(corN);
        corC = corridorRepository.save(corC);
        corS = corridorRepository.save(corS);
        corW = corridorRepository.save(corW);
        corE = corridorRepository.save(corE);
        cor1 = corridorRepository.save(cor1);
        cor2 = corridorRepository.save(cor2);
        cor3 = corridorRepository.save(cor3);

        // 5. 4 Exits (3 Emergency Exits + 1 Main Entrance)
        Exit ex1 = new Exit(floor, "EX-1", "Exit A - North Stairwell",        "EMERGENCY_EXIT", 460.0,  20.0, 2.4, 50, true);
        Exit ex2 = new Exit(floor, "EX-2", "Exit B - South Fire Door",        "EMERGENCY_EXIT", 460.0, 580.0, 2.4, 50, true);
        Exit ex3 = new Exit(floor, "EX-3", "Exit C - East Fire Escape",       "EMERGENCY_EXIT", 970.0, 300.0, 2.0, 40, true);
        Exit ex4 = new Exit(floor, "EX-4", "Main Entrance - West Lobby Gate", "MAIN_ENTRANCE",   20.0, 300.0, 3.5, 75, true);

        ex1 = exitRepository.save(ex1);
        ex2 = exitRepository.save(ex2);
        ex3 = exitRepository.save(ex3);
        ex4 = exitRepository.save(ex4);

        // 6. Predefined Scenarios
        Scenario s1 = new Scenario("Normal Baseline Conditions",
                "Standard operating baseline with all corridors, emergency doors, and main exits fully unobstructed.",
                "NORMAL", 1.2, 1.0, 5, true);
        s1 = scenarioRepository.save(s1);

        Scenario s2 = new Scenario("Central Spine Fire (Single Corridor Blocked)",
                "Hazardous fire outbreak in Central Grand Spine (COR-C); path sealed off by automated fire curtains.",
                "CORRIDOR_BLOCKED", 1.1, 1.0, 8, false);
        s2 = scenarioRepository.save(s2);
        blockedPassageRepository.save(new BlockedPassage(s2, corC, null, "CORRIDOR", "Active structural fire and heavy smoke in Central Spine"));

        Scenario s3 = new Scenario("Multiple Corridors Blocked (Dense Smoke Spread)",
                "Dense toxic smoke spreading through North Concourse (COR-N) and East Connector (COR-3).",
                "MULTI_BLOCKED", 0.9, 1.0, 10, false);
        s3 = scenarioRepository.save(s3);
        blockedPassageRepository.save(new BlockedPassage(s3, corN, null, "CORRIDOR", "Heavy smoke obscuring North Concourse"));
        blockedPassageRepository.save(new BlockedPassage(s3, cor3, null, "CORRIDOR", "Electrical hazard warning in East Connector"));

        Scenario s4 = new Scenario("North Stairwell Inaccessible (Exit A Blocked)",
                "Debris collapse blocking North Stairwell Fire Exit (EX-1); all North wing occupants redirected.",
                "EXIT_BLOCKED", 1.2, 1.0, 6, false);
        s4 = scenarioRepository.save(s4);
        blockedPassageRepository.save(new BlockedPassage(s4, null, ex1, "EXIT", "Debris fall blocking North Stairwell Exit A"));

        Scenario s5 = new Scenario("Peak Occupancy Event (High Crowd Density)",
                "Annual tech conference taking place; occupant headcount doubled across all conference and workspace zones.",
                "HIGH_DENSITY", 1.0, 2.0, 12, false);
        s5 = scenarioRepository.save(s5);

        Scenario s6 = new Scenario("Narrowed Capacity (Structural Renovation)",
                "Ongoing facility renovations restrict corridor throughput and movement speeds across wings.",
                "LOW_CAPACITY", 0.85, 1.0, 7, false);
        s6 = scenarioRepository.save(s6);

        log.info("Database seeding complete: 1 building, 10 rooms, 8 corridors, 4 exits, 6 scenarios.");
    }
}
