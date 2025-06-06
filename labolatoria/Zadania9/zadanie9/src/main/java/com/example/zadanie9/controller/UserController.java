package com.example.zadanie9.controller;

import com.example.zadanie9.model.User;
import com.example.zadanie9.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/allUsers")
    public List<User> getUsers() {
        return userService.findAll();
    }

    @PostMapping("/{userId}/roles/add")
    public ResponseEntity<String> addRoleToUser(@RequestParam String roleName, @PathVariable String userId) {
        try {
            if (roleName.isEmpty() || userId == null) {
                return ResponseEntity.badRequest().body("Role name cannot be empty");
            }

            userService.addRoleToUser(userId, roleName);
            return ResponseEntity.ok("Role " + roleName + " added to user " + userId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{userId}/roles/remove")
    public ResponseEntity<String> removeRoleFromUser(@RequestParam String roleName, @PathVariable String userId) {
        try {
            if (roleName.isEmpty() || userId == null) {
                return ResponseEntity.badRequest().body("Role name cannot be empty");
            }

            userService.removeRoleFromUser(userId, roleName);
            return ResponseEntity.ok("Role " + roleName + " removed from user " + userId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/checkRole")
    public String checkRole(@AuthenticationPrincipal UserDetails userDetails) {
        return "Rola " + userDetails.getUsername() + ": " + userDetails.getAuthorities().toString();
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        try {
            userService.deleteById(userId);
            return ResponseEntity.ok("User " + userId + " deleted");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
