package com.ecommerce.sb_ecom.service.impl;

import com.ecommerce.sb_ecom.exception.ResourceNotFoundException;
import com.ecommerce.sb_ecom.model.Address;
import com.ecommerce.sb_ecom.model.User;
import com.ecommerce.sb_ecom.payload.AddressDto;
import com.ecommerce.sb_ecom.repository.AddressRepository;
import com.ecommerce.sb_ecom.repository.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

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

    @Override
    public List<AddressDto> getUserAddresses(User user) {
        List<Address> addresses=user.getAddresses();
        List<AddressDto> addressDtos = addresses.stream()
                .map(address -> modelMapper.map(address, AddressDto.class)).toList();
        return addressDtos;
    }

    @Override
    public AddressDto updateAddress(Long addressId,AddressDto addressDto) {
        Address addressFromDb = addressRepository.findById(addressId).
                orElseThrow(() -> new ResourceNotFoundException("Address", "AddressId", addressId));

        addressFromDb.setCityName(addressDto.getCityName());
        addressFromDb.setCountry(addressDto.getCountry());
        addressFromDb.setPincode(addressDto.getPincode());
        addressFromDb.setCountry(addressDto.getCountry());
        addressFromDb.setStreet(addressDto.getStreet());
        addressFromDb.setState(addressDto.getState());
        addressFromDb.setBuildingName(addressDto.getBuildingName());
        Address updatedAddress = addressRepository.save(addressFromDb);

        User user=addressFromDb.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        user.getAddresses().add(updatedAddress);
        userRepository.save(user);
        return modelMapper.map(updatedAddress,AddressDto.class);
    }

    @Override
    public String deleteAddressById(Long addressId) {
        Address addressFromDb = addressRepository.findById(addressId).
                orElseThrow(() -> new ResourceNotFoundException("Address", "AddressId", addressId));

        User user=addressFromDb.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        userRepository.save(user);
        addressRepository.delete(addressFromDb);
        return "Address deleted successfully";
    }
}
