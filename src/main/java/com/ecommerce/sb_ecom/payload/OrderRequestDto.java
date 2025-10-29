package com.ecommerce.sb_ecom.payload;

import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor
public class OrderRequestDto {

    private Long addressId;
    private Long paymentMethod;
    private String pgName;
    private String pgStatus;
    private String pgPaymentId;
    private String pgResponseMessage;
}
