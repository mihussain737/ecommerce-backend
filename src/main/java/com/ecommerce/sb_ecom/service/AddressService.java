package com.ecommerce.sb_ecom.service;

import com.ecommerce.sb_ecom.model.User;
import com.ecommerce.sb_ecom.payload.AddressDto;

import java.util.List;

public interface AddressService {

    public AddressDto createAddress(AddressDto addressDto, User user);

    List<AddressDto> getAllAddresses();

    AddressDto getAddressById(Long addressId);

    List<AddressDto> getUserAddresses(User user);

    AddressDto updateAddress(Long addressId,AddressDto addressDto);

    String deleteAddressById(Long addressId);
}
