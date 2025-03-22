package com.example.cleanorarest.controller;

import com.example.cleanorarest.entity.AddressOrder;
import com.example.cleanorarest.model.order.CustomerAddressRequest;
import com.example.cleanorarest.service.GeoapifyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Location", description = "Location management APIs")
@RestController
@RequestMapping("/api/v1/location")
@AllArgsConstructor
@Slf4j
public class LocationController {

    private final GeoapifyService geoapifyService;

    @GetMapping("/coordinates")
    @Operation(summary = "Find location by coordinates", description = "Retrieves location information based on provided latitude and longitude.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "application/json", schema = @Schema())})
    })
    public ResponseEntity<AddressOrder> findByCoordinates(@ModelAttribute CustomerAddressRequest address) {
        try {
            AddressOrder response = geoapifyService.processAddress(address);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error occurred while searching by coordinates", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/address")
    @Operation(summary = "Find location by address", description = "Retrieves location information based on provided address.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Location not found", content = {@Content(mediaType = "application/json", schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {@Content(mediaType = "application/json", schema = @Schema())})
    })
    public ResponseEntity<AddressOrder> findByAddress(
            @ModelAttribute CustomerAddressRequest address) {
        try {
            AddressOrder response = geoapifyService.processAddress(address);
            return response != null
                    ? ResponseEntity.ok(response)
                    : ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error occurred while searching by address", e);
            return ResponseEntity.internalServerError().build();
        }
    }


}