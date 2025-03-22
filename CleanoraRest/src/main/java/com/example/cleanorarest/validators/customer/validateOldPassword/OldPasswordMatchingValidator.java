package com.example.cleanorarest.validators.customer.validateOldPassword;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.cleanorarest.entity.Customer;
import com.example.cleanorarest.repository.CustomerRepository;

@RequiredArgsConstructor
public class OldPasswordMatchingValidator implements ConstraintValidator<OldPasswordMatching, Object> {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    private String idField;
    private String oldPasswordField;

    @Override
    public void initialize(OldPasswordMatching constraintAnnotation) {
        this.idField = constraintAnnotation.id();
        this.oldPasswordField = constraintAnnotation.oldPassword();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(value);
        String oldPasswordValue = (String) beanWrapper.getPropertyValue(oldPasswordField);
        Object idValue = beanWrapper.getPropertyValue(idField);

        if (oldPasswordValue == null || oldPasswordValue.isEmpty()) {
            return true; // Old password not provided, consider it valid
        }

        if (idValue == null) {
            return false;
        }

        if (!(idValue instanceof Long)) {
            return false;
        }

        Customer customer;
        try {
            customer = customerRepository.findById((Long) idValue)
                    .orElseThrow(() -> new EntityNotFoundException("Customer not found with ID: " + idValue));
        } catch (EntityNotFoundException e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage())
                    .addPropertyNode(idField)
                    .addConstraintViolation();
            return false;
        }

        boolean isValid = passwordEncoder.matches(oldPasswordValue, customer.getPassword());

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode(oldPasswordField)
                    .addConstraintViolation();
        }

        return isValid;
    }
}