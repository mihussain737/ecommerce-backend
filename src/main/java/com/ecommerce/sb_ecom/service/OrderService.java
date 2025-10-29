package com.ecommerce.sb_ecom.service;

import com.ecommerce.sb_ecom.payload.OrderDto;

public interface OrderService {
    OrderDto placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage);
}
