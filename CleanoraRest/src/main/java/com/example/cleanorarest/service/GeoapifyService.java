package com.example.cleanorarest.service;

import com.example.cleanorarest.model.order.CustomerAddressRequest;
import com.example.cleanorarest.entity.AddressOrder;

public interface GeoapifyService {
    AddressOrder processAddress(CustomerAddressRequest request);
}
