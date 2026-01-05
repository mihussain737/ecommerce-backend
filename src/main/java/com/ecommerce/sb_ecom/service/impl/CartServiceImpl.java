package com.ecommerce.sb_ecom.service.impl;
import com.ecommerce.sb_ecom.exception.APIException;
import com.ecommerce.sb_ecom.exception.ResourceNotFoundException;
import com.ecommerce.sb_ecom.model.Cart;
import com.ecommerce.sb_ecom.model.CartItem;
import com.ecommerce.sb_ecom.model.Product;
import com.ecommerce.sb_ecom.model.User;
import com.ecommerce.sb_ecom.payload.CartDto;
import com.ecommerce.sb_ecom.payload.CartItemDto;
import com.ecommerce.sb_ecom.payload.ProductDto;
import com.ecommerce.sb_ecom.repository.CartItemRepository;
import com.ecommerce.sb_ecom.repository.CartRepository;
import com.ecommerce.sb_ecom.repository.ProductRepository;
import com.ecommerce.sb_ecom.service.CartService;
import com.ecommerce.sb_ecom.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public CartDto addProductToCart(Long productId, Integer quantity) {

        Cart cart=createCart();

        // Retrieve products details
        Product product=productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product","ProductId",productId));

        //Perform Validations
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);

        if(cartItem!=null){
            throw new APIException("Product "+product.getProductName() +" already exist in the cart");
        }

        if(product.getQuantity()==0){
            throw new APIException(product.getProductName()+ " is not available");
        }

        if(product.getQuantity()<quantity){
            throw new APIException("Please, make an order of the "+product.getProductName()+
                    " less than or equal to the quantity "+product.getQuantity()+ ".");
        }

        // Create Cart Item
        CartItem newCartItem=new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());
        // save Cart Item
        CartItem savedCartItem = cartItemRepository.save(newCartItem);

        product.setQuantity(product.getQuantity());
        // Return updated cart
        cart.setTotalPrice(cart.getTotalPrice()+product.getSpecialPrice()*quantity);
        Cart savedCart = cartRepository.save(cart);
        CartDto cartDto = modelMapper.map(savedCart, CartDto.class);
        List<CartItem> cartItems=cart.getCartItems();
        Stream<ProductDto> productDtoStream=cartItems.stream().map(item->{
            ProductDto map=modelMapper.map(item.getProduct(),ProductDto.class);
            map.setQuantity(item.getQuantity());
            return map;
        });
        cartDto.setProducts(productDtoStream.toList());
        return cartDto;
    }

    @Override
    public List<CartDto> getAllCarts() {
        List<Cart> carts=cartRepository.findAll();
        if(carts.size()==0){
            throw new APIException("No cart exist");
        }
        List<CartDto> cartDtoList=carts.stream()
                .map(cart -> {
                    CartDto cartDto=modelMapper.map(cart,CartDto.class);
                    List<ProductDto> products=cart.getCartItems().stream()
                            .map(p->{
                               ProductDto productDto= modelMapper.map(p.getProduct(),ProductDto.class);
                                productDto.setQuantity(p.getQuantity());
                                return productDto;
                            })
                            .toList();
                    cartDto.setProducts(products);

                    return cartDto;
                }).toList();

        return cartDtoList;
    }

    @Override
    public CartDto getCart(String emailId, Long cartId) {
        Cart cart=cartRepository.findCartByEmailAndCartId(emailId,cartId);
        if(cart==null){
            throw new ResourceNotFoundException("Cart","cartId",cartId);
        }
        CartDto cartDto=modelMapper.map(cart,CartDto.class);
        cart.getCartItems().forEach(c->c.getProduct().setQuantity(c.getQuantity()));
        List<ProductDto> productDtos=cart.getCartItems().stream()
                .map(p->modelMapper.map(p.getProduct(),ProductDto.class))
                .toList();
        cartDto.setProducts(productDtos);
        return cartDto;
    }

    @Transactional
    @Override
    public CartDto updateProductQuantity(Long productId, Integer quantity) {
        String emailId=authUtil.loggedInEmail();
        Cart userCart=cartRepository.findCartByEmail(emailId);
        Long cartId=userCart.getCartId();

        Cart cart=cartRepository.findById(cartId).
                orElseThrow(()->new ResourceNotFoundException("Cart","cartId",cartId));

        Product product=productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product","ProductId",productId));

        if(product.getQuantity()==0){
            throw new APIException(product.getProductName()+ " is not available");
        }

        if(product.getQuantity()<quantity){
            throw new APIException("Please, make an order of the "+product.getProductName()+
                    " less than or equal to the quantity "+product.getQuantity()+ ".");
        }

        CartItem cartItem=cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);
        if(cartItem==null){
            throw new APIException("Product "+ product.getProductName()+" not available in the cart");
        }

        int newQuantity=cartItem.getQuantity()+quantity;
        if(newQuantity<0){
            throw new APIException("The resulting quantity cannot be negative");
        }
        if(newQuantity==0){
            deleteProductFromCart(cartId,productId);
        }else {
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity()+quantity);
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice()+(cartItem.getProductPrice())*quantity);
            cartRepository.save(cart);
        }

        CartItem updatedItem=cartItemRepository.save(cartItem);

        if(updatedItem.getQuantity()==0){
            cartItemRepository.deleteById(updatedItem.getCartItemId());
        }

        CartDto cartDto=modelMapper.map(cart,CartDto.class);

        List<CartItem> cartItems=cart.getCartItems();
        Stream<ProductDto> productDtoStream=cartItems.stream().map(item->{
            ProductDto prd=modelMapper.map(item.getProduct(),ProductDto.class);
            prd.setQuantity(item.getQuantity());
            return prd;
        });
        cartDto.setProducts(productDtoStream.toList());
        return cartDto;
    }

    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart=cartRepository.findById(cartId).orElseThrow(()->
                new ResourceNotFoundException("cart","cartId",cartId));
        CartItem cartItem=cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);

        if(cartItem==null){
            throw new ResourceNotFoundException("Product","productId",productId);
        }
        cart.setTotalPrice(cart.getTotalPrice()-cartItem.getProductPrice()*cartItem.getQuantity());
        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId,productId);
        return "Product "+cartItem.getProduct().getProductName()+" removed from the cart!!";
    }

    @Override
    public void updateProductCarts(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId).
                orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        if (cartItem == null) {
            throw new APIException("product " + product.getProductName() + " not available in the cart!!!");
        }

        double cartPrice=cart.getTotalPrice() -
                (cartItem.getProductPrice() * cartItem.getQuantity());

        cartItem.setProductPrice(product.getSpecialPrice());
        cart.setTotalPrice(cartPrice +
                (cartItem.getProductPrice() * cartItem.getQuantity()));
        cartItemRepository.save(cartItem);
    }

    @Override
    public String createOrUpdateCartWithItems(List<CartItemDto> cartItemDtos) {
        String emailId= authUtil.loggedInEmail();

        Cart existingCart=cartRepository.findCartByEmail(emailId);
        if(existingCart==null){
            // saving the cart if cart not existing
            existingCart=new Cart();
            existingCart.setTotalPrice(0.00);
            existingCart.setUser(authUtil.loggedInUser());
            existingCart=cartRepository.save(existingCart);
        }else{
            // clear all current items in the existing cart
            cartItemRepository.deleteAllByCartId(existingCart.getCartId());
        }

        double totalPrice=0.00;
        for (CartItemDto cartItemDto:cartItemDtos){
//            Long productId= cartItemDto.getProduct().getProductId();
            Long productId= cartItemDto.getProductId();
            Integer quantity=cartItemDto.getQuantity();

            Product product =productRepository.findById(productId).orElseThrow(
                    ()-> new ResourceNotFoundException("Product","productId",productId));

          //  product.setQuantity(product.getQuantity()-quantity);
            totalPrice+=product.getSpecialPrice()*quantity;
            CartItem cartItem=new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setDiscount(product.getDiscount());
            cartItemRepository.save(cartItem);
        }

        existingCart.setTotalPrice(totalPrice);
        cartRepository.save(existingCart);
        return "Cart created/update with new items successfully";
    }

    public Cart createCart(){
        Cart userCart=cartRepository.findCartByEmail(authUtil.loggedInEmail());

        if(userCart!=null){
            return userCart;
        }

        Cart cart=new Cart();
        cart.setTotalPrice(0.0);
        cart.setUser(authUtil.loggedInUser());
        Cart newCart=cartRepository.save(cart);
        return newCart;
    }
}
