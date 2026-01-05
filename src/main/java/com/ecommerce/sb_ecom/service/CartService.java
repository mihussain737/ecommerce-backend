package com.ecommerce.sb_ecom.service;

import com.ecommerce.sb_ecom.payload.CartDto;
import com.ecommerce.sb_ecom.payload.CartItemDto;

import java.util.List;

public interface CartService {
  public  CartDto addProductToCart(Long productId, Integer quantity);
  public List<CartDto> getAllCarts();

  CartDto getCart(String emailId, Long cartId);

  CartDto updateProductQuantity(Long productId, Integer quantity);

  String deleteProductFromCart(Long cartId, Long productId);

    void updateProductCarts(Long cartId, Long productId);

  String createOrUpdateCartWithItems(List<CartItemDto> cartItemDtos);
}
