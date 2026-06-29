package hello.bookshop.payment.controller;

import hello.bookshop.common.exception.CustomException;
import hello.bookshop.common.session.SessionConst;
import hello.bookshop.member.dto.response.SessionMemberDto;
import hello.bookshop.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payments/toss")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/success")
    public String success(
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Integer amount,
            @SessionAttribute(SessionConst.LOGIN_MEMBER)SessionMemberDto loginMember,
            RedirectAttributes redirectAttributes
    ) {

        try {
            Long savedOrderId = paymentService.confirmPayment(
                    loginMember.getMemberId(),
                    paymentKey,
                    orderId,
                    amount
            );

            return "redirect:/orders/" + savedOrderId + "/complete";


        } catch (CustomException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

            return "redirect:/cart";
        }

    }

    @GetMapping("/fail")
    public String fail(
            @RequestParam String orderId,
            @RequestParam(required = false) String message,
            RedirectAttributes redirectAttributes
    ) {

        String failedMessage = message != null ? message : "결제가 취소되었습니다.";

        paymentService.failPayment(orderId, failedMessage);

        redirectAttributes.addFlashAttribute("errorMessage", failedMessage);

        return "redirect:/cart";


    }

}
