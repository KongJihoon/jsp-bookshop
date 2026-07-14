package hello.bookshop.order.controller;

import hello.bookshop.common.exception.CustomException;
import hello.bookshop.common.session.SessionConst;
import hello.bookshop.member.dto.response.SessionMemberDto;
import hello.bookshop.order.dto.request.CartOrderFormRequest;
import hello.bookshop.order.dto.request.OrderCreateRequest;
import hello.bookshop.order.dto.response.*;
import hello.bookshop.order.service.OrderService;
import hello.bookshop.payment.config.TossProperties;
import hello.bookshop.payment.dto.response.PaymentCheckoutResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    private final TossProperties tossProperties;

    /**
     * 상품 주문 폼
     */
    @PostMapping("/form/cart")
    public String cartOrderForm(
            @ModelAttribute CartOrderFormRequest request,
            @SessionAttribute(SessionConst.LOGIN_MEMBER) SessionMemberDto loginMember,
            RedirectAttributes redirectAttributes,
            Model model
            ) {
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
            @SessionAttribute(SessionConst.LOGIN_MEMBER) SessionMemberDto loginMember,
            RedirectAttributes redirectAttributes,
            Model model
            ) {
        if (bindingResult.hasErrors()) {
            OrderFormResponse orderForm = orderService.getCartOrderForm(loginMember.getMemberId(), request.getCartItemIds());

            model.addAttribute("errorMessage", bindingResult.getAllErrors().get(0).getDefaultMessage());
            model.addAttribute("orderForm", orderForm);
            model.addAttribute("orderItems", orderForm.getItems());
            model.addAttribute("orderCreateRequest", request);
            return "order/form";
        }

        try {

            PaymentCheckoutResponse checkout = orderService.createReadyCartOrder(loginMember.getMemberId(), request);

            model.addAttribute("checkout", checkout);
            model.addAttribute("clientKey", tossProperties.getClientKey());
            return "payment/checkout";

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


    @GetMapping
    public String orderList(
            @SessionAttribute(SessionConst.LOGIN_MEMBER) SessionMemberDto loginMember,
            Model model
    ) {
        List<OrderListResponse> orders = orderService.findMyOrder(loginMember.getMemberId());

        model.addAttribute("orders", orders);

        return "order/list";

    }

    @GetMapping("/{orderId}")
    public String orderDetail(
            @PathVariable Long orderId,
            @SessionAttribute(SessionConst.LOGIN_MEMBER) SessionMemberDto loginMember,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        try {
            OrderDetailResponse orderDetail = orderService.findMyOrderDetail(loginMember.getMemberId(), orderId);

            model.addAttribute("order", orderDetail);
            model.addAttribute("orderItems", orderDetail.getItems());

            return "order/detail";

        } catch (CustomException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

            return "redirect:/orders";
        }

    }

    @GetMapping("/delivery")
    public String deliveryList(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) SessionMemberDto loginMember,
            Model model
    ) {

        List<DeliveryListResponse> deliveries = orderService.findMyDeliveries(loginMember.getMemberId());

        model.addAttribute("deliveries", deliveries);


        return "order/delivery-list";
    }

}
