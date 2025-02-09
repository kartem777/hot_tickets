package com.examtest.demo.controller;

import com.examtest.demo.dto.userorder.UserOrderRequestDto;
import com.examtest.demo.dto.userorder.UserOrderResponseDto;
import com.examtest.demo.service.UserOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user-orders")
public class UserOrderController {

    private final UserOrderService userOrderService;

    public UserOrderController(UserOrderService userOrderService) {
        this.userOrderService = userOrderService;
    }

    @Operation(
            summary = "Get all user orders",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of user orders retrieved"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @GetMapping
    public ResponseEntity<List<UserOrderResponseDto>> getAllUserOrders() {
        List<UserOrderResponseDto> userOrders = userOrderService.getAllUserOrders();
        return ResponseEntity.ok(userOrders);
    }

    @Operation(
            summary = "Get user order by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User order retrieved"),
                    @ApiResponse(responseCode = "404", description = "User order not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserOrderResponseDto> getUserOrderById(@PathVariable UUID id) {
        UserOrderResponseDto userOrder = userOrderService.getUserOrderById(id);
        return ResponseEntity.ok(userOrder);
    }

    @Operation(
            summary = "Create a new user order",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User order created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data")
            }
    )
    @PostMapping
    public ResponseEntity<UserOrderResponseDto> addUserOrder(@Valid @RequestBody UserOrderRequestDto userOrderRequestDto) {
        UserOrderResponseDto newUserOrder = userOrderService.addUserOrder(userOrderRequestDto);
        return ResponseEntity.status(201).body(newUserOrder);
    }

    @Operation(
            summary = "Delete a user order by ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User order deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "User order not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserOrder(@PathVariable UUID id) {
        userOrderService.deleteUserOrder(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Update a user order by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User order updated successfully"),
                    @ApiResponse(responseCode = "404", description = "User order not found")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<UserOrderResponseDto> updateUserOrder(@PathVariable UUID id, @Valid @RequestBody UserOrderRequestDto userOrderRequestDto) {
        UserOrderResponseDto updatedUserOrder = userOrderService.updateUserOrder(id, userOrderRequestDto);
        return ResponseEntity.ok(updatedUserOrder);
    }
}
