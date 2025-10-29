package com.ecommerce.sb_ecom.controller;

import com.ecommerce.sb_ecom.payload.OrderDto;
import com.ecommerce.sb_ecom.payload.OrderRequestDto;
import com.ecommerce.sb_ecom.service.OrderService;
import com.ecommerce.sb_ecom.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AuthUtil authUtil;

    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDto> orderProducts(@PathVariable String paymentMethod, @Valid @RequestBody OrderRequestDto orderRequestDto){
        String emailId=authUtil.loggedInEmail();
       OrderDto orderDto= orderService.placeOrder(
                emailId,
                orderRequestDto.getAddressId(),
                paymentMethod,
                orderRequestDto.getPgName(),
                orderRequestDto.getPgPaymentId(),
                orderRequestDto.getPgStatus(),
                orderRequestDto.getPgResponseMessage()
        );
        return new ResponseEntity<>(orderDto, HttpStatus.OK);
    }
}
