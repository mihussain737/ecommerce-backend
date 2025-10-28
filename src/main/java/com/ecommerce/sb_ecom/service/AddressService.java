package com.ecommerce.sb_ecom.service;

import com.ecommerce.sb_ecom.model.User;
import com.ecommerce.sb_ecom.payload.AddressDto;

public interface AddressService {

    public AddressDto createAddress(AddressDto addressDto, User user);
}
