package hello.bookshop.category.domain;


import hello.bookshop.category.type.CategoryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Category {

    private Long categoryId;

    private Long parentId;

    private String categoryName;

    private CategoryStatus categoryStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


}
