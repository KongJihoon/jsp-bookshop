package hello.bookshop.common.advice;

import hello.bookshop.category.domain.Category;
import hello.bookshop.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributeAdvice {

    private final CategoryService categoryService;

    @ModelAttribute("headerCategories")
    public List<Category> headerCategories() {
        return categoryService.findAllByCategories();
    }

}
