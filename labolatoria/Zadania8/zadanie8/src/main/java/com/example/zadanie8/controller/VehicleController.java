package com.example.zadanie8.controller;

import com.example.zadanie8.model.Vehicle;
import com.example.zadanie8.service.VehicleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {
    private final VehicleService vehicleService;
    Logger logger = LoggerFactory.getLogger(VehicleController.class);

    @Autowired
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping("/all")
    public List<Vehicle> getAllVehicles() {
        return vehicleService.findAll();
    }

    @GetMapping("/allActive")
    public List<Vehicle> getAllActiveVehicles() {
        return vehicleService.findAllActive();
    }

    @GetMapping("/allAvailable")
    public List<Vehicle> getAllAvailableVehicles() {
        return vehicleService.findAvailableVehicles();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable String id) {
        logger.info("Request received for vehicle with ID: {}", id);
        return vehicleService.findById(id)
                .map(vehicle -> {
                    logger.info("Found vehicle with ID: {}", id);
                    return ResponseEntity.ok(vehicle);
                })
                .orElseGet(() -> {
                    logger.info("Vehicle with ID: {} not found", id);
                    return ResponseEntity.notFound().build(); // 404
                });
    }

    @PostMapping
    public ResponseEntity<Vehicle> addVehicle(@RequestBody Vehicle vehicle) {
        try {
            Vehicle savedVehicle = vehicleService.save(vehicle);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedVehicle);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVehicle(@PathVariable String id) {
        try {
            vehicleService.deleteById(id);
            return ResponseEntity.ok("Vehicle with ID: " + id + " deleted.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
