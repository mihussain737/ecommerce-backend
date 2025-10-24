package com.ecommerce.sb_ecom.payload;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CartItemDto {

    private Long cartItemId;
    private CartDto cart;
    private ProductDto product;
    private Integer quantity;
    private Double discount;
    private Double productPrice;
}
