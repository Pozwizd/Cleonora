package com.example.cleanorarest.service.Imp;

import com.example.cleanorarest.entity.Customer;
import com.example.cleanorarest.mapper.CustomerMapper;
import com.example.cleanorarest.model.customer.CustomerProfileRequest;
import com.example.cleanorarest.model.customer.CustomerResponse;
import com.example.cleanorarest.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link CustomerServiceImp}.
 */
@ExtendWith(MockitoExtension.class)
class CustomerServiceImpTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImp customerService;

    /**
     * Test for {@link CustomerServiceImp#ifCustomerCountMoreThan(int)}.
     * Should return true if the customer count is more than the given value.
     */
    @Test
    void ifCustomerCountMoreThan_CustomerCountGreaterThanValue_ReturnsTrue() {
        int value = 5;
        when(customerRepository.count()).thenReturn(6L);

        boolean result = customerService.ifCustomerCountMoreThan(value);

        assertTrue(result);
        verify(customerRepository).count();
    }

    /**
     * Test for {@link CustomerServiceImp#ifCustomerCountMoreThan(int)}.
     * Should return false if the customer count is not more than the given value.
     */
    @Test
    void ifCustomerCountMoreThan_CustomerCountLessThanValue_ReturnsFalse() {
        int value = 5;
        when(customerRepository.count()).thenReturn(4L);

        boolean result = customerService.ifCustomerCountMoreThan(value);

        assertFalse(result);
        verify(customerRepository).count();
    }

     /**
     * Test for {@link CustomerServiceImp#ifCustomerCountMoreThan(int)}.
     * Should return false if the customer count is equal to the given value.
     */
    @Test
    void ifCustomerCountMoreThan_CustomerCountEqualsValue_ReturnsFalse() {
        int value = 5;
        when(customerRepository.count()).thenReturn(5L);

        boolean result = customerService.ifCustomerCountMoreThan(value);

        assertFalse(result);
        verify(customerRepository).count();
    }

    /**
     * Test for {@link CustomerServiceImp#getCustomerResponseByEmail(String)}.
     * Should return CustomerResponse when customer is found.
     */
    @Test
    void getCustomerResponseByEmail_CustomerFound_ReturnsCustomerResponse() {
        String email = "test@example.com";
        Customer customer = new Customer();
        customer.setEmail(email);
        CustomerResponse expectedResponse = new CustomerResponse();
        expectedResponse.setEmail(email);

        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));
        when(customerMapper.toResponse(customer)).thenReturn(expectedResponse);

        CustomerResponse actualResponse = customerService.getCustomerResponseByEmail(email);

        assertEquals(expectedResponse, actualResponse);
        verify(customerRepository).findByEmail(email);
        verify(customerMapper).toResponse(customer);
    }

    /**
     * Test for {@link CustomerServiceImp#getCustomerResponseByEmail(String)}.
     * Should return null when customer is not found.
     */
    @Test
    void getCustomerResponseByEmail_CustomerNotFound_ReturnsNull() {
        String email = "test@example.com";

        when(customerRepository.findByEmail(email)).thenReturn(Optional.empty());

        CustomerResponse actualResponse = customerService.getCustomerResponseByEmail(email);

        assertNull(actualResponse);
        verify(customerRepository).findByEmail(email);
        verify(customerMapper, never()).toResponse(any());
    }

    /**
     * Test for {@link CustomerServiceImp#updateCustomerFromCustomerRequest(CustomerProfileRequest)}.
     * Should return null.  Currently not implemented.
     */
    @Test
    void updateCustomerFromCustomerRequest_ReturnsNull() {
        CustomerProfileRequest request = new CustomerProfileRequest();

        CustomerResponse response = customerService.updateCustomerFromCustomerRequest(request);

        assertNull(response);
    }

    @Test
    void getCustomerByEmail_CustomerNotFound_ReturnsNull() {
        String email = "test@example.com";

        when(customerRepository.findByEmail(email)).thenReturn(Optional.empty());

        Customer customer = customerService.getCustomerByEmail(email);

        assertNull(customer);
        verify(customerRepository).findByEmail(email);
    }

    /**
     * Test for {@link CustomerServiceImp#save(Customer)}.
     */
    @Test
    void save_SavesCustomer() {
        Customer customer = new Customer();

        customerService.save(customer);

        verify(customerRepository).save(customer);
    }

    /**
     * Test for {@link CustomerServiceImp#deleteByEmail(String)}.
     * Should mark customer as deleted when customer is found.
     */
    @Test
    void deleteByEmail_CustomerFound_MarksCustomerAsDeleted() {
        String email = "test@example.com";
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setDeleted(false);

        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));

        customerService.deleteByEmail(email);

        assertTrue(customer.getDeleted());
        verify(customerRepository).findByEmail(email);
        verify(customerRepository).save(customer);
    }

    /**
     * Test for {@link CustomerServiceImp#deleteByEmail(String)}.
     * Should do nothing when customer is not found.
     */
    @Test
    void deleteByEmail_CustomerNotFound_DoesNothing() {
        String email = "test@example.com";

        when(customerRepository.findByEmail(email)).thenReturn(Optional.empty());

        customerService.deleteByEmail(email);

        verify(customerRepository).findByEmail(email);
        verify(customerRepository, never()).save(any());
    }
}
