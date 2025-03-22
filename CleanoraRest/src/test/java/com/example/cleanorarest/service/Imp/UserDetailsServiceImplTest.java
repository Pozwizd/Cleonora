package com.example.cleanorarest.service.Imp;

import com.example.cleanorarest.entity.Customer;
import com.example.cleanorarest.repository.AdminUserRepository;
import com.example.cleanorarest.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    /**
     * Test for {@link UserDetailsServiceImpl#loadUserByUsername(String)}.
     * Should return UserDetails when a user with the given username exists.
     */
    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        String email = "test@example.com";
        Customer customer = new Customer();
        customer.setEmail(email);

        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertEquals(customer, userDetails);
        verify(customerRepository).findByEmail(email);
    }

    /**
     * Test for {@link UserDetailsServiceImpl#loadUserByUsername(String)}.
     * Should throw UsernameNotFoundException when a user with the given username does not exist.
     */
    @Test
    void loadUserByUsername_UserNotFound_ThrowsUsernameNotFoundException() {
        String email = "nonexistent@example.com";

        when(customerRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(email));
        verify(customerRepository).findByEmail(email);
    }
}
