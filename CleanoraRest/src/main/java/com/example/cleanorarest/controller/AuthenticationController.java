package com.example.cleanorarest.controller;


import com.example.cleanorarest.model.customer.CustomerRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.cleanorarest.model.authentication.*;
import com.example.cleanorarest.service.*;


@Tag(name = "Authentication")
@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
@SecurityRequirement(name = "empty")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final PasswordResetTokenCustomerService passwordResetTokenCustomerService;

    private final CustomerService customerService;
    private final MailService mailService;



    @PostMapping("/register")
    @Operation(summary = "Customer registration", description = "Register new Customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Failed validation", content = {@Content(mediaType = "application/json", schema = @Schema())})
    })
    public ResponseEntity<AuthenticationResponse> registerCustomer(
            @Valid @RequestBody CustomerRequest customerRequest) {
        AuthenticationResponse response = authenticationService.register(customerRequest);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }




    @PostMapping("/login")
    @Operation(summary = "Customer authentication", description = "Authenticate Customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",content = {@Content(mediaType = "application/json",schema = @Schema(implementation = AuthenticationResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Wrong email or password",content = {@Content(mediaType = "application/json",schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "Failed validation",content = {@Content(mediaType = "application/json",schema = @Schema())})})
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody AuthenticationRequest authenticationRequest){
            return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
    }

    @Operation(summary = "Refresh access token",description = "Get new access token by refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",content = {@Content(mediaType = "application/json",schema = @Schema(implementation = AuthenticationResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Not found customer by refresh token",content = {@Content(mediaType = "application/json",schema = @Schema())}),
            @ApiResponse(responseCode = "401", description = "Wrong refresh token",content = {@Content(mediaType = "application/json",schema = @Schema())})})
    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshToken refreshToken){
        if(refreshToken.getRefreshToken().equals("")){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        AuthenticationResponse authenticationResponse = authenticationService.refreshToken(refreshToken);
        if (authenticationResponse != null) {
            return new ResponseEntity<>(authenticationResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Sending email to customer to change password",description = "Request email and send password reset token to this email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",content = {@Content(mediaType = "application/json",schema = @Schema(implementation = PasswordResetTokenResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Customer with such email not found",content = {@Content(mediaType = "application/json",schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "Failed validation",content = {@Content(mediaType = "application/json",schema = @Schema())})})
    @PostMapping("/forgotPassword")
    public ResponseEntity<?> forgotPassword(HttpServletRequest httpRequest,@Valid @RequestBody EmailRequest emailRequest){
        String token = passwordResetTokenCustomerService.createOrUpdatePasswordResetToken(emailRequest);
        mailService.sendToken(token,emailRequest.getEmail(),httpRequest);
        return new ResponseEntity<>(new PasswordResetTokenResponse(token),HttpStatus.OK);
    }

    @Operation(summary = "Change password", description = "Set new password after customer received email with password reset token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Failed password validation or password reset token expired",content = {@Content(mediaType = "application/json",schema = @Schema())})})
    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@Parameter(name = "token", description = "Password reset token", example = "b8aa464c-7375-464e-9d8f-83cdae970921")
                                     @RequestParam("token") String token,
                                     @Valid @RequestBody ChangePasswordRequest changePasswordRequest){
        if(passwordResetTokenCustomerService.validatePasswordResetToken(token)){
            passwordResetTokenCustomerService.updatePassword(changePasswordRequest, token);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
