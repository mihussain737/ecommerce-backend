package com.ecommerce.sb_ecom.payload;
import lombok.*;
import java.util.List;

@Getter @NoArgsConstructor @AllArgsConstructor @Setter
public class CategoryResponse {
    private List<CategoryDto> content;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalpages;
    private boolean isLastPage;
}
