package com.ecommerce.sb_ecom.service;

import com.ecommerce.sb_ecom.payload.OrderDto;
import com.ecommerce.sb_ecom.payload.OrderResponse;

public interface OrderService {
    OrderDto placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage);

    OrderResponse getAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    OrderDto updateOrder( Long orderId, String status);
}
