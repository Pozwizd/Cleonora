package com.example.cleanorarest.mapper;

import com.example.cleanorarest.entity.Customer;
import com.example.cleanorarest.model.customer.CustomerRequest;
import com.example.cleanorarest.model.customer.CustomerResponse;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerMapper {

    Customer toEntity(CustomerRequest customerRequest);

    CustomerResponse toResponse(Customer customer);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Customer partialUpdate(CustomerRequest customerRequest, @MappingTarget Customer customer);

    default Page<CustomerResponse> toResponsePage(Page<Customer> customers){
        return customers.map(this::toResponse);
    }
}
