package com.ecommerce.sb_ecom.controller;

import com.ecommerce.sb_ecom.model.User;
import com.ecommerce.sb_ecom.payload.AddressDto;
import com.ecommerce.sb_ecom.service.AddressService;
import com.ecommerce.sb_ecom.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private AuthUtil authUtil;

    @PostMapping("/addresses")
    public ResponseEntity<AddressDto> saveAddress(@Valid @RequestBody AddressDto addressDto){
        User user=authUtil.loggedInUser();
        AddressDto savedAddressDto=addressService.createAddress(addressDto,user);
        return new ResponseEntity<>(savedAddressDto, HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDto>> getAllAddresses(){
        List<AddressDto> addressDtoList=addressService.getAllAddresses();
        return new ResponseEntity<>(addressDtoList,HttpStatus.OK);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDto> getAddressById(@PathVariable Long addressId){
        AddressDto addressDto=addressService.getAddressById(addressId);
        return new ResponseEntity<>(addressDto,HttpStatus.OK);
    }

    @GetMapping("/user/addresses")
    public ResponseEntity<List<AddressDto>> getAddressByUser(){
        User user=authUtil.loggedInUser();
        List<AddressDto> addressDtoList=addressService.getUserAddresses(user);
        return new ResponseEntity<>(addressDtoList,HttpStatus.OK);
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDto> updateAddressById(@PathVariable Long addressId,@Valid @RequestBody AddressDto addressDto){
        AddressDto address=addressService.updateAddress(addressId,addressDto);
        return new ResponseEntity<>(address,HttpStatus.OK);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddressById(@PathVariable Long addressId){
        String status=addressService.deleteAddressById(addressId);
        return new ResponseEntity<>("Address is deleted with id: "+addressId,HttpStatus.OK);
    }
}
