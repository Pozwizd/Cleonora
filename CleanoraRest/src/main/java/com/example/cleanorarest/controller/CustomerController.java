package com.example.cleanorarest.controller;


import com.example.cleanorarest.model.customer.CustomerProfileRequest;
import com.example.cleanorarest.model.customer.CustomerResponse;
import com.example.cleanorarest.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Profile")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/profile")
@Slf4j
public class CustomerController {


    private final CustomerService customerService;

    @Operation(summary = "Get customer profile", description = "Getting customer profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CustomerResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Customer unauthorized", content = {@Content(mediaType = "application/json", schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = {@Content(mediaType = "application/json", schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = {@Content(mediaType = "application/json", schema = @Schema())})})
    @GetMapping
    public ResponseEntity<CustomerResponse> getCustomerProfile(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Request to get profile for user: {}", userDetails.getUsername());
        CustomerResponse customerDto = customerService.getCustomerResponseByEmail(userDetails.getUsername());
        return ResponseEntity.ok(customerDto);
    }

    @Operation(summary = "Update profile", description = "Updating customer profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CustomerResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Customer unauthorized", content = {@Content(mediaType = "application/json", schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = {@Content(mediaType = "application/json", schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "Failed validation", content = {@Content(mediaType = "application/json", schema = @Schema())})})
    @PutMapping
    public ResponseEntity<CustomerResponse> updateCustomerProfile(@Valid @RequestBody CustomerProfileRequest customerRequest, @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Request to update profile for user: {}", userDetails.getUsername());
        CustomerResponse customerResponse = customerService.updateCustomerFromCustomerRequest(customerRequest);
        return ResponseEntity.ok(customerResponse);
    }

    @Operation(summary = "Delete profile", description = "Soft delete customer profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "401", description = "Customer unauthorized"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "400", description = "Bad request")})
    @DeleteMapping
    public ResponseEntity<Void> deleteCustomerProfile(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Request to delete profile for user: {}", userDetails.getUsername());
        customerService.deleteByEmail(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
