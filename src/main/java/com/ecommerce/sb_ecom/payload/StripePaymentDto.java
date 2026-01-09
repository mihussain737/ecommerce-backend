package com.ecommerce.sb_ecom.payload;
import lombok.Data;

@Data
public class StripePaymentDto {

    private Long amount;
    private String currency;
}
