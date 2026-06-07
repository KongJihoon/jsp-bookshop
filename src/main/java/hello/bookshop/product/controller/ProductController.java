package hello.bookshop.product.controller;

import hello.bookshop.category.domain.Category;
import hello.bookshop.category.service.CategoryService;
import hello.bookshop.common.exception.category.NotFoundCategoryException;
import hello.bookshop.common.exception.product.FileUploadException;
import hello.bookshop.common.session.SessionConst;
import hello.bookshop.member.dto.SessionMemberDto;
import hello.bookshop.product.dto.ProductCreateRequest;
import hello.bookshop.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/product")
@RequiredArgsConstructor
public class ProductController {


    private final ProductService productService;

    private final CategoryService categoryService;

    @GetMapping("/add")
    public String createProduct(Model model) {

        List<Category> categories = categoryService.findAllByCategories();

        model.addAttribute("product", new ProductCreateRequest());
        model.addAttribute("categories", categories);
        return "product/add";
    }

    @PostMapping("/add")
    public String createProduct(
            @Validated @ModelAttribute("product") ProductCreateRequest request,
            BindingResult bindingResult,
            @SessionAttribute(SessionConst.LOGIN_MEMBER)SessionMemberDto loginMember,
            Model model,
            RedirectAttributes redirectAttributes
            ) {


        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage",
                    bindingResult.getAllErrors().get(0).getDefaultMessage());

            model.addAttribute("categories", categoryService.findAllByCategories());

            return "product/add";
        }

        try {

            productService.createProduct(
                    request, loginMember.getMemberId()
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "도서 등록이 완료되었습니다."
            );


        } catch (NotFoundCategoryException | FileUploadException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("categories", categoryService.findAllByCategories());

            return "product/add";
        }


        return "redirect:/admin/dashboard";

    }

}
