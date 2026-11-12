package com.evacuation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

/**
 * Emergency Evacuation Route Simulation and Risk Analysis
 * Main Spring Boot Application Entry Point
 */
@SpringBootApplication
public class EvacuationApplication {

    public static void main(String[] args) {
        SpringApplication.run(EvacuationApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        System.out.println();
        System.out.println("===============================================================================");
        System.out.println("   EMERGENCY EVACUATION ROUTE SIMULATION AND RISK ANALYSIS");
        System.out.println("   Status: APPLICATION RUNNING SUCCESSFULLY");
        System.out.println();
        System.out.println("   👉 LOCAL ACCESS LINK: http://localhost:8080");
        System.out.println("   (Ctrl + Click the link above to open in your browser)");
        System.out.println("===============================================================================");
        System.out.println();
    }
}
