package com.example.cleanorarest.controller;

import com.example.cleanorarest.model.customer.CustomerProfileRequest;
import com.example.cleanorarest.model.customer.CustomerResponse;
import com.example.cleanorarest.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getCustomerProfile_shouldReturnCustomerProfile() throws Exception {
        // Arrange
        String email = "test@example.com";
        UserDetails userDetails = new User(email, "password", Collections.emptyList());
        CustomerResponse customerResponse = new CustomerResponse();
        customerResponse.setEmail(email);

        when(customerService.getCustomerResponseByEmail(email)).thenReturn(customerResponse);

        Principal principal = userDetails::getUsername;

        mockMvc.perform(get("/api/v1/profile").principal(principal))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.email", is(email)));
    }

    @Test
    void getCustomerProfile_shouldReturnNotFound_whenCustomerNotFound() throws Exception {

        String email = "nonexistent@example.com";
        UserDetails userDetails = new User(email, "password", Collections.emptyList());
        Principal principal = userDetails::getUsername;

        when(customerService.getCustomerResponseByEmail(anyString())).thenThrow(new RuntimeException("Customer not found"));

        mockMvc.perform(get("/api/v1/profile").principal(principal))
            .andExpect(status().isNotFound());
    }

   @Test
    void updateCustomerProfile_shouldUpdateCustomerProfile() throws Exception {

        String email = "test@example.com";
        UserDetails userDetails = new User(email, "password", Collections.emptyList());
        Principal principal = userDetails::getUsername;

        CustomerProfileRequest request = new CustomerProfileRequest();
        request.setName("Updated Name");
        request.setEmail("updated@example.com");
        request.setPhoneNumber("+380991234567");

        CustomerResponse customerResponse = new CustomerResponse();
        customerResponse.setName(request.getName());
        customerResponse.setEmail(request.getEmail());
        customerResponse.setPhoneNumber(request.getPhoneNumber());


        when(customerService.updateCustomerFromCustomerRequest(any(CustomerProfileRequest.class))).thenReturn(customerResponse);

        mockMvc.perform(put("/api/v1/profile")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(request.getName())))
                .andExpect(jsonPath("$.email", is(request.getEmail())))
                .andExpect(jsonPath("$.phoneNumber", is(request.getPhoneNumber())));
    }

    @Test
    void updateCustomerProfile_shouldReturnNotFound_whenCustomerNotFound() throws Exception {

        String email = "test@example.com";
        UserDetails userDetails = new User(email, "password", Collections.emptyList());
        Principal principal = userDetails::getUsername;

        CustomerProfileRequest request = new CustomerProfileRequest();
        request.setName("Updated Name");
        request.setEmail("updated@example.com");
        request.setPhoneNumber("+380991234567");

        when(customerService.updateCustomerFromCustomerRequest(any(CustomerProfileRequest.class))).thenThrow(new RuntimeException("Customer not found"));

        mockMvc.perform(put("/api/v1/profile")
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

     @Test
    void updateCustomerProfile_shouldReturnBadRequest_whenRequestBodyIsInvalid() throws Exception {

         String email = "test@example.com";
         UserDetails userDetails = new User(email, "password", Collections.emptyList());
         Principal principal = userDetails::getUsername;

        CustomerProfileRequest request = new CustomerProfileRequest();
        request.setName("");
        request.setEmail("invalid-email");
        request.setPhoneNumber("123");

         mockMvc.perform(put("/api/v1/profile")
                         .principal(principal)
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request)))
                 .andExpect(status().isBadRequest());
    }
}
