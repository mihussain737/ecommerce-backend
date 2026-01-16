package com.ecommerce.sb_ecom.service.impl;

import com.ecommerce.sb_ecom.exception.APIException;
import com.ecommerce.sb_ecom.exception.ResourceNotFoundException;
import com.ecommerce.sb_ecom.model.Cart;
import com.ecommerce.sb_ecom.model.Category;
import com.ecommerce.sb_ecom.model.Product;
import com.ecommerce.sb_ecom.payload.CartDto;
import com.ecommerce.sb_ecom.payload.ProductDto;
import com.ecommerce.sb_ecom.payload.ProductResponse;
import com.ecommerce.sb_ecom.repository.*;
import com.ecommerce.sb_ecom.service.CartService;
import com.ecommerce.sb_ecom.service.FileService;
import com.ecommerce.sb_ecom.service.ProductService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;


    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartService cartService;

    @Value("${project.image}")
    private String path;

    @Value("${image.base.url}")
    private String imageBaseUrl;

    @Autowired
    private EntityManager entityManager;

    @Override
    public ProductDto saveProduct(Long categoryId,ProductDto productDto) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new ResourceNotFoundException("category", "categoryId", categoryId));

        boolean iSProductNotPresent=true;
        List<Product> products=category.getProducts();
        for (Product value:products){
            if(value.getProductName().equals(productDto.getProductName())){
                iSProductNotPresent=false;
                break;
            }
        }
        if(iSProductNotPresent){
            Product product=new Product();
            product.setProductName(productDto.getProductName());
            product.setImage("default.png");
            product.setDescription(productDto.getDescription());
            product.setQuantity(productDto.getQuantity());
            product.setPrice(productDto.getPrice());
            product.setDiscount(productDto.getDiscount());
            product.setCategory(category);

            double discountAmount = (productDto.getDiscount() / 100.0) * productDto.getPrice();
            double specialPrice = productDto.getPrice() - discountAmount;
            product.setSpecialPrice(specialPrice);

            Product savedProduct=productRepository.save(product);
            return modelMapper.map(savedProduct,ProductDto.class);
        }else {
            throw new APIException("Product Already Exist");
        }
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String keyword, String category) {

        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")?
                Sort.by(sortBy).ascending():
                Sort.by(sortBy).descending();

        Pageable pageDetails= PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Specification<Product> spec=Specification.anyOf();

        if(keyword!=null && !keyword.isEmpty()){
            spec=spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("productName")), "%" + keyword.toLowerCase() + "%"));
        }
        if(category!=null && !category.isEmpty()){
            spec=spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("category").get("categoryName"), category));
        }

        Page<Product> pageProducts=productRepository.findAll(spec,pageDetails);
        List<Product> products=pageProducts.getContent();

        List<ProductDto> productDtos=products.stream()
                .map(product->{
                    ProductDto productDto =modelMapper.map(product,ProductDto.class);
                    productDto.setImage(constructImageUrl(product.getImage()));
                    return productDto;
                        })
                .toList();

        if(products.isEmpty()){
            throw new APIException("No Product Exists");
        }
        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDtos);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }


    private String constructImageUrl(String imageName){
        return imageBaseUrl.endsWith("/") ? imageBaseUrl + imageName : imageBaseUrl + "/" + imageName;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new ResourceNotFoundException("category", "categoryId", categoryId));
        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")?
                Sort.by(sortBy).ascending():
                Sort.by(sortBy).descending();

        Pageable pageDetails= PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> pageProducts=productRepository.findByCategoryOrderBySpecialPriceAsc(category,pageDetails);
        List<Product> products=pageProducts.getContent();
        if(products.size()==0){
            throw new APIException(category.getCategoryName()+" category does not have any product");
        }

        List<ProductDto> productDtos = products.stream().map(i -> modelMapper.map(i, ProductDto.class)).toList();
        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDtos);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")?
                Sort.by(sortBy).ascending():
                Sort.by(sortBy).descending();
        Pageable pageDetails= PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> pageProducts= productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%',pageDetails);
        List<Product> products=pageProducts.getContent();
        List<ProductDto> productDtos = products.stream().map(i -> modelMapper.map(i, ProductDto.class)).toList();

        if(products.size()==0){
            throw new APIException("Products not found with keyowrd:: "+keyword);
        }
        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDtos);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }

    // new method
    @Override
    public ProductDto updateProductById(Long productId, ProductDto productDto) {
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new ResourceNotFoundException("Product", "productId", productId));

        productDto.setSpecialPrice(productDto.getPrice() - (productDto.getPrice() * productDto.getDiscount() / 100));
        product.setProductName(productDto.getProductName());
        product.setDescription(productDto.getDescription());
        product.setQuantity(productDto.getQuantity());
        product.setPrice(productDto.getPrice());
        product.setDiscount(productDto.getDiscount());
        product.setSpecialPrice(productDto.getSpecialPrice());
        Product updatedProduct = productRepository.save(product);

        List<Cart> carts=cartRepository.findCartsByProductId(productId);

        List<CartDto> cartDtos =carts.stream().map(cart -> {
            CartDto cartDto=modelMapper.map(cart,CartDto.class);
            List<ProductDto> products=cart.getCartItems().stream()
                    .map(p->modelMapper.map(p,ProductDto.class)).toList();
            cartDto.setProducts(products);
            return cartDto;
        }).toList();

        cartDtos.forEach(cart-> cartService.updateProductCarts(cart.getCartId(),productId));

        return modelMapper.map(updatedProduct,ProductDto.class);
    }

//    @Override
//    public ProductDto deleteProductById(Long productId) {
//        Product product = productRepository.findById(productId).orElseThrow(() ->
//                new ResourceNotFoundException("Product", "ProductId", productId));
//
//        List<Cart> carts=cartRepository.findCartsByProductId(productId);
//        carts.forEach(cart-> cartService.deleteProductFromCart(cart.getCartId(),productId));
//        cartRepository.flush();
//        productRepository.deleteById(productId);
//        return modelMapper.map(product,ProductDto.class);
//    }

    @Transactional
    @Override
    public ProductDto deleteProductById(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product", "ProductId", productId));

        // 1. Delete all cart items of this product (DB level)
        cartItemRepository.deleteByProductId(productId);

        // 2. Flush to execute SQL delete
        entityManager.flush();

        // 3. Now delete product
        productRepository.delete(product);

        return modelMapper.map(product, ProductDto.class);
    }


    // handle images
//    @Override
//    public ProductDto updateProductImage(Long productId, MultipartFile image) throws IOException {
//        Product productFromDb = productRepository.findById(productId).orElseThrow(() ->
//                new ResourceNotFoundException("Product", "ProductId", productId));
//        // upload image to server for prod but in local upload in the uploads folder
//        String fileName= fileService.uploadImage(path,image);
//        productFromDb.setImage(fileName);
//        Product product=productRepository.save(productFromDb);
//        return modelMapper.map(product,ProductDto.class);
//    }

    @Override
    public ProductDto updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        String fileName = fileService.uploadImage(path, image);
        productFromDb.setImage(fileName);

        Product updatedProduct = productRepository.save(productFromDb);
        return modelMapper.map(updatedProduct, ProductDto.class);
    }

    @Override
    public ProductResponse getAllProductsForAdmin(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")?
                Sort.by(sortBy).ascending():
                Sort.by(sortBy).descending();

        Pageable pageDetails= PageRequest.of(pageNumber,pageSize,sortByAndOrder);


        Page<Product> pageProducts=productRepository.findAll(pageDetails);
        List<Product> products=pageProducts.getContent();

        List<ProductDto> productDtos=products.stream()
                .map(product->{
                    ProductDto productDto =modelMapper.map(product,ProductDto.class);
                    productDto.setImage(constructImageUrl(product.getImage()));
                    return productDto;
                })
                .toList();

        if(products.isEmpty()){
            throw new APIException("No Product Exists");
        }
        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDtos);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }
}
