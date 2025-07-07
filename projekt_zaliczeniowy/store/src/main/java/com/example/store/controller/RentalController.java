package com.example.store.controller;

import com.example.store.dto.RentalRequest;
import com.example.store.model.Rental;
import com.example.store.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {
    private final RentalService rentalService;

    @PostMapping("/rent")
    public Rental rentBook(@RequestBody RentalRequest rentalRequest, @AuthenticationPrincipal UserDetails userDetails) {
        return rentalService.rent(rentalRequest.getBookId(), userDetails.getUsername());
    }

    @PostMapping("/return")
    public Rental returnBook(@RequestBody RentalRequest rq, @AuthenticationPrincipal UserDetails ud) {
        return rentalService.returnRental(rq.getBookId(), ud.getUsername());
    }

    @GetMapping("/myRentals")
    public List<Rental> getMyRentals(@AuthenticationPrincipal UserDetails userDetails) {
        return rentalService.getUserActiveRentals(userDetails.getUsername());
    }
}
