package org.example.controller;

import org.example.dto.RentalRequest;
import org.example.model.Rental;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.service.RentalService;
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
    private final UserRepository userRepository;
    private RentalService rentalService;

    @Autowired
    public RentalController(RentalService rentalService, UserRepository userRepository) {
        this.rentalService = rentalService;
        this.userRepository = userRepository;
    }

    @GetMapping("/all")
    public List<Rental> getRentals() {
        return rentalService.findAll();
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
