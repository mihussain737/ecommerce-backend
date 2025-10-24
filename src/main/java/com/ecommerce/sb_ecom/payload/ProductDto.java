package com.ecommerce.sb_ecom.payload;

import com.ecommerce.sb_ecom.model.Category;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private Long productId;
    @Size(min = 3 ,message = "Product Name should be at least 3 characters")
    private String productName;
    private String image;
    @Size(min = 6 ,message = "Product Name should be at least 6 characters")
    private String description;
    private Integer quantity;
    private double price;
    private double discount;
    private double specialPrice;
}
