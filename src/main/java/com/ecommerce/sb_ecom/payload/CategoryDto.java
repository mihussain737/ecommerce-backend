package com.ecommerce.sb_ecom.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CategoryDto {
    private Long categoryId;
    @NotBlank(message = "Category Name cannot be blank")
    @Size(min = 5, max = 20, message = "Category Name must be between 5 and 20 characters")
    private String categoryName;
}
