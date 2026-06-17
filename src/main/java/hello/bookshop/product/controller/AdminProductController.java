package hello.bookshop.product.controller;

import hello.bookshop.category.domain.Category;
import hello.bookshop.category.service.CategoryService;
import hello.bookshop.common.exception.category.NotFoundCategoryException;
import hello.bookshop.common.exception.product.FileUploadException;
import hello.bookshop.common.session.SessionConst;
import hello.bookshop.member.dto.response.SessionMemberDto;
import hello.bookshop.product.dto.request.ProductCreateRequest;
import hello.bookshop.product.dto.response.AdminProductListResponse;
import hello.bookshop.product.service.AdminProductService;
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
public class AdminProductController {


    private final AdminProductService adminProductService;

    private final CategoryService categoryService;

    @GetMapping("/add")
    public String createProduct(Model model) {

        List<Category> categories = categoryService.findAllByCategories();

        model.addAttribute("product", new ProductCreateRequest());
        model.addAttribute("categories", categories);
        return "admin/product/add";
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

            return "admin/product/add";
        }

        try {

            adminProductService.createProduct(
                    request, loginMember.getMemberId()
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "도서 등록이 완료되었습니다."
            );


        } catch (NotFoundCategoryException | FileUploadException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("categories", categoryService.findAllByCategories());

            return "admin/product/add";
        }


        return "redirect:/admin/dashboard";

    }

    /**
     * 관리자 도서 목록 조회
     */
    @GetMapping("/list")
    public String productList(Model model) {

        List<AdminProductListResponse> products = adminProductService.findAdminProductList();

        List<Category> categories = categoryService.findAllByCategories();

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);

        return "admin/product/list";
    }


}
