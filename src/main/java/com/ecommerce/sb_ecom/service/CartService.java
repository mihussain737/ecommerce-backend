package com.ecommerce.sb_ecom.service;

import com.ecommerce.sb_ecom.payload.CartDto;

import java.util.List;

public interface CartService {
  public  CartDto addProductToCart(Long productId, Integer quantity);
  public List<CartDto> getAllCarts();

  CartDto getCart(String emailId, Long cartId);

  CartDto updateProductQuantity(Long productId, Integer quantity);
}
