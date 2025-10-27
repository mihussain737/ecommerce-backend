package com.ecommerce.sb_ecom.payload;

import lombok.*;


@Data @NoArgsConstructor @AllArgsConstructor
public class AddressDto {

    private Long addressId;

    private String street;

    private String buildingName;

    private String cityName;

    private String state;

    private String country;

    private String pincode;

}
