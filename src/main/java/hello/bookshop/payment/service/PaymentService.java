package hello.bookshop.payment.service;

import hello.bookshop.common.exception.order.OrderInfoException;
import hello.bookshop.common.exception.product.StockQuantityExceedException;
import hello.bookshop.order.dto.response.OrderFormItemResponse;
import hello.bookshop.order.mapper.OrderMapper;
import hello.bookshop.order.type.OrderStatus;
import hello.bookshop.payment.client.TossPaymentClient;
import hello.bookshop.payment.domain.Payment;
import hello.bookshop.payment.dto.request.TossConfirmRequest;
import hello.bookshop.payment.dto.response.TossConfirmResponse;
import hello.bookshop.payment.mapper.PaymentMapper;
import hello.bookshop.payment.type.PaymentMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentMapper paymentMapper;

    private final OrderMapper orderMapper;

    private final TossPaymentClient tossPaymentClient;

    @Transactional
    public Long confirmPayment(
            Long memberId,
            String paymentKey,
            String tossOrderId,
            Integer amount
    ) {

        Payment payment = paymentMapper.findByTossOrderId(tossOrderId)
                .orElseThrow(() -> new OrderInfoException("결제 정보를 찾을 수 없습니다."));

        if (!payment.getAmount().equals(amount)) {
            throw new OrderInfoException("결제 금액이 일치하지 않습니다.");
        }

        TossConfirmResponse response = tossPaymentClient.confirm(
                new TossConfirmRequest(paymentKey, tossOrderId, amount)
        );

        List<OrderFormItemResponse> items = orderMapper.findOrderItemsForPayment(payment.getOrderId());

        for (OrderFormItemResponse item : items) {

            int updateCount = orderMapper.decreaseProductStock(
                    item.getProductId(),
                    item.getQuantity()
            );

            if (updateCount == 0) {
                throw new StockQuantityExceedException("재고 수량을 초과한 상품이 있습니다.");
            }

        }

        PaymentMethod paymentMethod = PaymentMethod.from(response.getMethod(), response.getEasyPayProvider());

        paymentMapper.updatePaid(
                payment.getPaymentId(),
                response.getPaymentKey(),
                paymentMethod
        );

        orderMapper.updateOrderStatus(payment.getOrderId(), OrderStatus.PAID);

        int deletedCount = orderMapper.deletePaidOrderCartItems(memberId, payment.getOrderId());

        if (deletedCount != items.size()) {
            throw new OrderInfoException("주문한 장바구니 삭제에 실패하였습니다.");
        }


        return payment.getOrderId();

    }

    @Transactional
    public void failPayment(String tossOrderId, String message) {

        paymentMapper.updateFailed(tossOrderId, message);

        Payment payment = paymentMapper.findByTossOrderId(tossOrderId)
                .orElseThrow(() -> new OrderInfoException("해당 결제사항을 찾을 수 없습니다."));

        orderMapper.updateOrderStatus(payment.getOrderId(), OrderStatus.FAILED);

    }

}
