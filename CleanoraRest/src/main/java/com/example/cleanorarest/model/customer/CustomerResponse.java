package com.example.cleanorarest.model.customer;

import com.example.cleanorarest.entity.Customer;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link Customer}
 */
@Data
public class CustomerResponse implements Serializable {
    Long id;
    String name;
    String surname;
    String email;
    String phoneNumber;
    Boolean isActive;
}
