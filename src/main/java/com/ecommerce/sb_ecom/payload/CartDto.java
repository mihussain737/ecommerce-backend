package com.ecommerce.sb_ecom.payload;

import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Getter@Setter@NoArgsConstructor@AllArgsConstructor
public class CartDto {

    private Long cartId;
    private Double totalPrice=0.0;
    private List<ProductDto> products=new ArrayList<>();
}
