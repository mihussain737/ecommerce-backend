package com.ecommerce.sb_ecom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name="addresses")
@ToString
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Size(min = 5,message = "Street name must be atleast 5 characters")
    private String street;

    @NotBlank
    @Size(min = 5,message = "Building name must be atleast 5 characters")
    private String buildingName;

    @NotBlank
    @Size(min = 4,message = "city name must be atleast 4 characters")
    private String cityName;

    @NotBlank
    @Size(min = 4,message = "State name must be atleast 4 characters")
    private String state;

    @NotBlank
    @Size(min = 3,message = "Country name must be atleast 3 characters")
    private String country;

    @NotBlank
    @Size(min = 6,message = "Pincode name must be atleast 6 characters")
    private String pincode;

    @ToString.Exclude
    @ManyToMany(mappedBy = "addresses")
    private List<User> users=new ArrayList<>();

    public Address(Long addressId, String street, String buildingName, String cityName, String state, String country, String pincode) {
        this.addressId = addressId;
        this.street = street;
        this.buildingName = buildingName;
        this.cityName = cityName;
        this.state = state;
        this.country = country;
        this.pincode = pincode;
    }
}
