package hello.bookshop.category.mapper;

import hello.bookshop.category.domain.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {


    boolean existsByCategoryId(Long categoryId);

    List<Category> findAllByCategories();
}
