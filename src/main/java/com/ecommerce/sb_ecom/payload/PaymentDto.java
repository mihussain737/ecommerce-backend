package com.ecommerce.sb_ecom.payload;

import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor
public class PaymentDto {

    private Long paymentId;
    private String paymentMethod;
    private String pgPaymentId;
    private String pgStatus;
    private String pgName;
    private String pgResponseMessage;
}
