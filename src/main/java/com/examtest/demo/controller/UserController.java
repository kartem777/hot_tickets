package com.examtest.demo.controller;

import com.examtest.demo.dto.user.RoleDto;
import com.examtest.demo.dto.user.UserBasicDto;
import com.examtest.demo.dto.user.UserDetailedDto;
import com.examtest.demo.dto.user.UserRegistrationDto;
import com.examtest.demo.exception.RegistrationException;
import com.examtest.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Get all users",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of users retrieved"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserBasicDto>> getAllUsers() {
        List<UserBasicDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Get user by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User retrieved"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserDetailedDto> getUserById(@PathVariable UUID id) {
        UserDetailedDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Register a new user",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User registered successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data")
            }
    )
    @PostMapping("/register")
    public ResponseEntity<UserDetailedDto> registerUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {
        try {
            UserDetailedDto newUser = userService.register(userRegistrationDto);
            return ResponseEntity.status(201).body(newUser);
        } catch (RegistrationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
            summary = "Delete a user by ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Update a user's information by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<UserDetailedDto> updateUser(@PathVariable UUID id, @Valid @RequestBody UserRegistrationDto userRegistrationDto) {
        UserDetailedDto updatedUser = userService.updateUser(id, userRegistrationDto);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(
            summary = "Update a user's role by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User role updated successfully"),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserBasicDto> updateUserRole(@PathVariable UUID id, @Valid @RequestBody RoleDto roleDto) {
        UserBasicDto updatedUser = userService.updateUserRole(id, roleDto);
        return ResponseEntity.ok(updatedUser);
    }
}
