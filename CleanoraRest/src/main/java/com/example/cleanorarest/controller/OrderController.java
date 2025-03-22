package com.example.cleanorarest.controller;


import com.example.cleanorarest.model.customer.CustomerResponse;
import com.example.cleanorarest.model.order.OrderRequest;
import com.example.cleanorarest.model.order.OrderResponse;
import com.example.cleanorarest.service.CustomerService;
import com.example.cleanorarest.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Order", description = "Order management APIs")
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final CustomerService customerService;
    private final OrderService orderService;

    @Operation(summary = "Get orders for a customer", description = "Retrieves a paginated list of orders for the authenticated customer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = {@Content(mediaType = "application/json", schema = @Schema())})
    })
    @GetMapping
    public ResponseEntity<?> getOrdersByUser(
            @Parameter(description = "Page number for pagination", example = "0")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Number of items per page", example = "5")
            @RequestParam(defaultValue = "5") Integer size,
            @AuthenticationPrincipal UserDetails userDetails

    ) {
        CustomerResponse customer = customerService.getCustomerResponseByEmail(userDetails.getUsername());
        return ResponseEntity.ok(orderService.getPageAllOrdersByCustomerId(page, size, customer.getId()));
    }

    @Operation(summary = "Get order by ID", description = "Retrieves a specific order by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Order not found", content = {@Content(mediaType = "application/json", schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "application/json", schema = @Schema())})
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable Long id) {
        try {
            OrderResponse orderResponse = orderService.getOrderById(id);
            return ResponseEntity.ok(orderResponse);
        } catch (Exception e) {
            log.error("Error getting order: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("An unexpected error occurred.");
        }
    }

    @Operation(summary = "Delete order by ID", description = "Deletes a specific order by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = {@Content(mediaType = "application/json", schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Order not found", content = {@Content(mediaType = "application/json", schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "application/json", schema = @Schema())})
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting order: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("An unexpected error occurred.");
        }
    }

    @Operation(summary = "Create a new order", description = "Creates a new order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = {@Content(mediaType = "application/json", schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "application/json", schema = @Schema())})
    })
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderRequest orderRequest,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        try {
            OrderResponse createdOrder = orderService.createOrder(orderRequest, userDetails.getUsername());
            return ResponseEntity.ok(createdOrder);
        } catch (DataIntegrityViolationException e) {
            log.error("Database error creating order: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error: Data integrity violation.");
        } catch (Exception e) {
            log.error("Error creating order: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("An unexpected error occurred.");
        }
    }

    @Operation(summary = "Update an existing order", description = "Updates an existing order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = {@Content(mediaType = "application/json", schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "application/json", schema = @Schema())})
    })
    @PutMapping
    public ResponseEntity<?> updateOrder(@Valid @RequestBody OrderRequest orderRequest,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        try {
            OrderResponse updatedOrder = orderService.updateOrder(orderRequest, userDetails.getUsername());
            return ResponseEntity.ok(updatedOrder);
        } catch (DataIntegrityViolationException e) {
            log.error("Database error updating order: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error: Data integrity violation.");
        } catch (Exception e) {
            log.error("Error updating order: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("An unexpected error occurred.");
        }
    }
}