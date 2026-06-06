package hello.bookshop.category.service;

import hello.bookshop.category.domain.Category;
import hello.bookshop.category.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public List<Category> findAllByCategories() {

        List<Category> categories = categoryMapper.findAllByCategories();

        log.info("카테고리 검색 시작 = {}", categories);

        return categoryMapper.findAllByCategories();
    }

}
