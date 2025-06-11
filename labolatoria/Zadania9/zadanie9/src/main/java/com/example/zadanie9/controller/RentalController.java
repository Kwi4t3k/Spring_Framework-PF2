package com.example.zadanie9.controller;

import com.example.zadanie9.dto.RentalRequest;
import com.example.zadanie9.model.Rental;
import com.example.zadanie9.model.User;
import com.example.zadanie9.repository.UserRepository;
import com.example.zadanie9.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {
    private final RentalService rentalService;
    private final UserRepository userRepository;

    @Autowired
    public RentalController(RentalService rentalService, UserRepository userRepository) {
        this.rentalService = rentalService;
        this.userRepository = userRepository;
    }

    @GetMapping("/all")
    public List<Rental> getAllRentals() {
        return rentalService.findAll();
    }

    @GetMapping("/allActive")
    public List<Rental> getActiveRentals() {
        return rentalService.findAll().stream().filter(rental -> rental.getReturnDate() == null).toList();
    }

    @GetMapping("/getRented")
    public List<Rental> getRentedRentals(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByLogin(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return rentalService.findActiveRentalByUserId(user.getId());
    }

    @GetMapping("/isRented/{vehicleId}")
    public ResponseEntity<Boolean> isVehicleRented(@PathVariable String vehicleId) {
        boolean rented = rentalService.isVehicleRented(vehicleId);
        return ResponseEntity.ok(rented);
    }

    @GetMapping("/active/{vehicleId}")
    public ResponseEntity<Rental> getActiveRentalByVehicle(@PathVariable String vehicleId) {
        return rentalService
                .findActiveRentalByVehicleId(vehicleId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/rent")
    public ResponseEntity<Rental> rentVehicle(@RequestBody RentalRequest rentalRequest, @AuthenticationPrincipal UserDetails userDetails) {
        String login = userDetails.getUsername();
        User user = userRepository.findByLogin(login).orElseThrow(() -> new UsernameNotFoundException("User not found " + login));
        Rental rental = rentalService.rent(rentalRequest.getVehicleId(), user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(rental);
    }

    @PostMapping("/return")
    public ResponseEntity<Rental> returnVehicle(@RequestBody RentalRequest rentalRequest, @AuthenticationPrincipal UserDetails userDetails) {
        String login = userDetails.getUsername();
        User user = userRepository.findByLogin(login).orElseThrow(() -> new UsernameNotFoundException("User not found " + login));
        Rental rental = rentalService.returnRental(rentalRequest.getVehicleId(), user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(rental);
    }
}
