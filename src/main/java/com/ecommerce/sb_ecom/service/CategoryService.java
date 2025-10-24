package com.ecommerce.sb_ecom.service;
import com.ecommerce.sb_ecom.payload.CategoryDto;
import com.ecommerce.sb_ecom.payload.CategoryResponse;


public interface CategoryService {
    CategoryResponse  getAllCategories(Integer pageNumber , Integer pageSize ,String sortBy,String sortDir);
    CategoryDto createCategory(CategoryDto categoryDto);
    CategoryDto getCategoryById(Long id);
    CategoryDto deleteCategory(Long id);
    CategoryDto updateCategory(Long id ,CategoryDto categoryDto);
}
