package com.ecommerce.sb_ecom.payload;

import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor
public class OrderItemDto {

    private Long orderItemId;
    private ProductDto product;
    private Integer quantity;
    private double discount;
    private double orderedProductPrice;
}
