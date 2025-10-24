package com.ecommerce.sb_ecom.service.impl;
import com.ecommerce.sb_ecom.config.AppConstants;
import com.ecommerce.sb_ecom.exception.APIException;
import com.ecommerce.sb_ecom.exception.ResourceNotFoundException;
import com.ecommerce.sb_ecom.model.Category;
import com.ecommerce.sb_ecom.payload.CategoryDto;
import com.ecommerce.sb_ecom.payload.CategoryResponse;
import com.ecommerce.sb_ecom.repository.CategoryRepository;
import com.ecommerce.sb_ecom.service.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
        Sort sortByAndOrder=null;
        try {
             sortByAndOrder = sortDir.equalsIgnoreCase("asc")
                    ? Sort.by(sortBy).ascending()
                    : Sort.by(sortBy).descending();
        } catch (Exception e) {
            sortByAndOrder = Sort.by(AppConstants.SORT_CATEGORIES_BY).ascending();
        }


        Pageable pageDetails= PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Category> categoryPage=categoryRepository.findAll(pageDetails);

        List<Category> allCategories = categoryPage.getContent();
        if(allCategories.isEmpty()){
            throw new ResourceNotFoundException("Categories");
        }
        List<CategoryDto> categoryDtoList = allCategories.stream().map(i -> modelMapper.map(i, CategoryDto.class)).toList();
        CategoryResponse categoryResponse=new CategoryResponse();
        categoryResponse.setContent(categoryDtoList);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalpages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());
        return  categoryResponse;
    }

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        categoryRepository.findByCategoryName(categoryDto.getCategoryName()).ifPresent(
                categoryName->{
                    throw new APIException("Category already exists with the same name: "+categoryDto.getCategoryName());
                }
        );
        Category category = new Category();
        category.setCategoryName(categoryDto.getCategoryName());
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory,CategoryDto.class);
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        Category savedCategory=categoryRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Category","CategoryId",id));
        return modelMapper.map(savedCategory,CategoryDto.class);
    }

    @Override
    public CategoryDto deleteCategory(Long id) {
        Category savedCategory=categoryRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Category","CategoryId",id));

        categoryRepository.deleteById(savedCategory.getCategoryId());
            return modelMapper.map(savedCategory,CategoryDto.class);
    }
    @Override
    public CategoryDto updateCategory(Long id,CategoryDto categoryDto) {
        Category savedCategory=categoryRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Category","CategoryId",id));

        categoryRepository.findByCategoryName(categoryDto.getCategoryName()).ifPresent(
                categoryName->{
                    throw new APIException("Category already exists with the same name: "+categoryDto.getCategoryName());
                }
        );
        Category category = new Category();
        category.setCategoryId(id);
        category.setCategoryName(categoryDto.getCategoryName());
        savedCategory=categoryRepository.save(category);
        return new CategoryDto(category.getCategoryId(),category.getCategoryName());
    }
}
