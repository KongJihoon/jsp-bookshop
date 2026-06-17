package hello.bookshop.product.controller;

import hello.bookshop.common.dto.PageResponse;
import hello.bookshop.product.dto.response.UserProductDetailResponse;
import hello.bookshop.product.dto.response.UserProductListResponse;
import hello.bookshop.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public String productList(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Long categoryId,
            Model model
    ) {

        PageResponse<UserProductListResponse> productPage = productService.findUserProductList(page, categoryId);

        model.addAttribute("productPage", productPage);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("selectedCategoryId", categoryId);

        return "product/list";

    }

    @GetMapping("/{productId}")
    public String productDetail(@PathVariable Long productId, Model model) {
        UserProductDetailResponse product = productService.findUserProductDetail(productId);

        model.addAttribute("product", product);

        return "product/detail";
    }

}
