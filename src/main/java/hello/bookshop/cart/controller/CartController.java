package hello.bookshop.cart.controller;

import hello.bookshop.cart.dto.request.CartAddRequest;
import hello.bookshop.cart.service.CartService;
import hello.bookshop.common.session.SessionConst;
import hello.bookshop.member.dto.response.SessionMemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @PostMapping("/items")
    public String addCartItem(
            @Validated @ModelAttribute CartAddRequest request,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) SessionMemberDto loginMember,
            RedirectAttributes redirectAttributes
            ) {

        if (loginMember == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인 후 사용 가능합니다.");
            return "redirect:/member/login";
        }

        cartService.addCartItem(loginMember.getMemberId(), request.getProductId(), request.getQuantity());

        redirectAttributes.addFlashAttribute("successMessage", "장바구니에 상품을 담았습니다.");

        return "redirect:/products/" + request.getProductId();
    }

}
