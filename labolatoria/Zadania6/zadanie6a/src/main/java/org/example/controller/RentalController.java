package org.example.controller;

import org.example.dto.RentalRequest;
import org.example.model.Rental;
import org.example.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {
    private RentalService rentalService;

    @Autowired
    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping("/all")
    public List<Rental> getRentals() {
        return rentalService.findAll();
    }

    @PostMapping("/rent")
    public ResponseEntity<Rental> rentVehicle(@RequestBody RentalRequest rentalRequest) {
        if(rentalRequest.vehicleId == null || rentalRequest.userId == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Rental rental = rentalService.rent(rentalRequest.vehicleId, rentalRequest.userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(rental);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/return")
    public ResponseEntity<String> returnVehicle(@RequestBody RentalRequest rentalRequest) {
        if(rentalRequest.vehicleId == null || rentalRequest.userId == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            boolean returned = rentalService.returnRental(rentalRequest.vehicleId, rentalRequest.userId);
            if(returned) {
                return ResponseEntity.ok("Vehicle with ID: " + rentalRequest.vehicleId + " returned.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vehicle with ID: " + rentalRequest.vehicleId + " is not rented.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{vehicleId}")
    public ResponseEntity<Rental> getActiveRentalByVehicleId(@PathVariable String vehicleId){
        return this.rentalService.findActiveRentalByVehicleId(vehicleId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
