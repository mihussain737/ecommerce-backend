package com.ecommerce.sb_ecom.payload;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CartItemDto {

    private Long productId;
    private Integer quantity;
//    private CartDto cart;
//    private ProductDto product;
//    private Integer quantity;
//    private Double discount;
//    private Double productPrice;
}
