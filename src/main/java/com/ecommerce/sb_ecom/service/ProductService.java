package com.ecommerce.sb_ecom.service;

import com.ecommerce.sb_ecom.payload.ProductDto;
import com.ecommerce.sb_ecom.payload.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    public ProductDto saveProduct(Long categoryid,ProductDto productDto);

    ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    //new method
    ProductDto updateProductById(Long productId, ProductDto productDto);

    ProductDto deleteProductById(Long productId);

    ProductDto updateProductImage(Long productId, MultipartFile image) throws IOException;
}
