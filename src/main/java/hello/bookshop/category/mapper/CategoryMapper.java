package hello.bookshop.category.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper {


    boolean existsByCategoryId(Long categoryId);

}
