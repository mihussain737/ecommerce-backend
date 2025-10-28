package com.ecommerce.sb_ecom.service.impl;

import com.ecommerce.sb_ecom.exception.ResourceNotFoundException;
import com.ecommerce.sb_ecom.model.Address;
import com.ecommerce.sb_ecom.model.User;
import com.ecommerce.sb_ecom.payload.AddressDto;
import com.ecommerce.sb_ecom.repository.AddressRepository;
import com.ecommerce.sb_ecom.service.AddressService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    
    @Autowired
    private ModelMapper modelMapper;
    
    @Autowired
    private AddressRepository addressRepository;
    
    @Override
    public AddressDto createAddress(AddressDto addressDto, User user) {

        Address address=modelMapper.map(addressDto,Address.class);
        List<Address> addressesList=user.getAddresses();
        addressesList.add(address);
        user.setAddresses((addressesList));
        address.setUser(user);

        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDto.class);
    }

    @Override
    public List<AddressDto> getAllAddresses() {
        List<Address> addresses=addressRepository.findAll();
        List<AddressDto> addressDtos = addresses.stream()
                .map(address -> modelMapper.map(address, AddressDto.class)).toList();
        return addressDtos;
    }

    @Override
    public AddressDto getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId).orElseThrow(
                () -> new ResourceNotFoundException("Address", "AddressId", addressId)
        );
        return modelMapper.map(address,AddressDto.class);
    }
}
