package hello.bookshop.order.controller;

import hello.bookshop.common.exception.CustomException;
import hello.bookshop.common.session.SessionConst;
import hello.bookshop.member.dto.response.SessionMemberDto;
import hello.bookshop.order.dto.request.CartOrderFormRequest;
import hello.bookshop.order.dto.request.OrderCreateRequest;
import hello.bookshop.order.dto.response.OrderCompleteResponse;
import hello.bookshop.order.dto.response.OrderFormResponse;
import hello.bookshop.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    /**
     * 상품 주문 폼
     */
    @PostMapping("/form/cart")
    public String cartOrderForm(
            @ModelAttribute CartOrderFormRequest request,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) SessionMemberDto loginMember,
            RedirectAttributes redirectAttributes,
            Model model
            ) {

        if (loginMember == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인 후 사용 가능합니다.");
            return "redirect:/member/login";
        }


        try {

            OrderFormResponse orderForm = orderService.getCartOrderForm(loginMember.getMemberId(), request.getCartItemIds());

            model.addAttribute("orderForm", orderForm);
            model.addAttribute("orderItems", orderForm.getItems());

            return "order/form";


        } catch (CustomException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/cart";
        }
    }

    /**
     * 상품 주문 기능
     */
    @PostMapping
    public String createOrder(
            @Validated @ModelAttribute OrderCreateRequest request,
            BindingResult bindingResult,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) SessionMemberDto loginMember,
            RedirectAttributes redirectAttributes,
            Model model
            ) {

        if (loginMember == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인 후 사용 가능합니다.");
            return "redirect:/member/login";
        }

        if (bindingResult.hasErrors()) {
            OrderFormResponse orderForm = orderService.getCartOrderForm(loginMember.getMemberId(), request.getCartItemIds());

            model.addAttribute("errorMessage", bindingResult.getAllErrors().get(0).getDefaultMessage());
            model.addAttribute("orderForm", orderForm);
            model.addAttribute("orderItems", orderForm.getItems());
            model.addAttribute("orderCreateRequest", request);
            return "order/form";
        }

        try {
            OrderCompleteResponse response = orderService.createCartOrder(loginMember.getMemberId(), request);

            return "redirect:/orders/" + response.getOrderId() + "/complete";

        } catch (CustomException e) {
            OrderFormResponse orderForm = orderService.getCartOrderForm(loginMember.getMemberId(), request.getCartItemIds());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("orderForm", orderForm);
            model.addAttribute("orderItems", orderForm.getItems());
            model.addAttribute("orderCreateRequest", request);

            return "order/form";

        }


    }

    @GetMapping("/{orderId}/complete")
    public String orderComplete(@PathVariable Long orderId, Model model) {
        model.addAttribute("orderId", orderId);

        return "order/complete";
    }

}

