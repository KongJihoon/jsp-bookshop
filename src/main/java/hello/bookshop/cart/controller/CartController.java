package hello.bookshop.cart.controller;

import hello.bookshop.cart.dto.request.CartAddRequest;
import hello.bookshop.cart.dto.request.CartQuantityUpdateRequest;
import hello.bookshop.cart.dto.response.CartItemDeleteResponse;
import hello.bookshop.cart.dto.response.CartQuantityUpdateResponse;
import hello.bookshop.cart.dto.response.CartResponse;
import hello.bookshop.cart.service.CartService;
import hello.bookshop.common.exception.CustomException;
import hello.bookshop.common.exception.ErrorResponse;
import hello.bookshop.common.session.SessionConst;
import hello.bookshop.member.dto.response.SessionMemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    /**
     * 장바구니 상품 담기
     */
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

    /**
     * 장바구니 상품 조회
     */
    @GetMapping
    public String cartList(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) SessionMemberDto loginMember,
            RedirectAttributes redirectAttributes,
            Model model
    ) {

        if (loginMember == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인 후 사용 가능합니다.");
            return "redirect:/member/login";
        }


        CartResponse cart = cartService.findCart(loginMember.getMemberId());

        model.addAttribute("cart", cart);
        model.addAttribute("cartItems", cart.getItems());

        return "cart/list";
    }

    /**
     * 장바구니 상품 수량 변경
     */
    @PostMapping("/items/{cartItemId}/quantity")
    public ResponseEntity<?> updateQuantity(
            @PathVariable Long cartItemId,
            @RequestBody CartQuantityUpdateRequest request,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) SessionMemberDto loginMember
            ) {

        if (loginMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("NOT_LOGIN", "로그인 후 사용 가능합니다."));
        }

        try {
            CartQuantityUpdateResponse response = cartService.updateQuantity(loginMember.getMemberId(), cartItemId, request.getQuantity());

            return ResponseEntity.ok(response);


        } catch (CustomException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("INVALID_REQUEST", e.getMessage()));
        }

    }

    /**
     * 장바구니 상품 삭제
     */
    @PostMapping("/items/{cartItemId}/delete")
    @ResponseBody
    public ResponseEntity<?> deleteCartItem(
            @PathVariable Long cartItemId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) SessionMemberDto loginMember) {

        if (loginMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("NOT_LOGIN", "로그인 후 사용 가능합니다."));
        }

        try {
            CartItemDeleteResponse response = cartService.deleteCartItem(loginMember.getMemberId(), cartItemId);

            return ResponseEntity.ok(response);

        } catch (CustomException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
        }

    }

}
