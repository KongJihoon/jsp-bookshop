package hello.bookshop.order.controller;

import hello.bookshop.common.dto.PageRequest;
import hello.bookshop.common.dto.PageResponse;
import hello.bookshop.common.exception.CustomException;
import hello.bookshop.order.dto.request.AdminOrderStatusUpdateRequest;
import hello.bookshop.order.dto.response.AdminOrderDetailResponse;
import hello.bookshop.order.dto.response.AdminOrderListResponse;
import hello.bookshop.order.service.AdminOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @GetMapping
    public String orderList(
            @RequestParam(defaultValue = "1") Integer page,
            Model model) {

        PageRequest pageRequest = new PageRequest(page, 10, null, null, null);

        PageResponse<AdminOrderListResponse> pageResponse = adminOrderService.findAdminOrders(pageRequest);

        model.addAttribute("pageResponse", pageResponse);
        model.addAttribute("orders", pageResponse.getContent());

        return "admin/order/list";

    }

    @GetMapping("/{orderId}")
    public String orderDetail(
            @PathVariable Long orderId,
            Model model,
            RedirectAttributes redirectAttributes
    ) {

        try {
            AdminOrderDetailResponse order = adminOrderService.findAdminOrderDetail(orderId);

            model.addAttribute("order", order);
            model.addAttribute("orderItems", order.getItems());
            model.addAttribute("statusUpdateRequest", new AdminOrderStatusUpdateRequest());

            return "admin/order/detail";
        } catch (CustomException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

            return "redirect:/admin/orders";
        }

    }
    @PostMapping("/{orderId}/status")
    public String updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @ModelAttribute AdminOrderStatusUpdateRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    bindingResult.getAllErrors().get(0).getDefaultMessage());

            return "redirect:/admin/orders/" + orderId;
        }

        try {

            adminOrderService.updateOrderStatus(orderId, request.getOrderStatus());

            redirectAttributes.addFlashAttribute("successMessage", "주문 상태가 변경되었습니다.");

        } catch (CustomException e) {

            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/orders/" + orderId;
    }

}
