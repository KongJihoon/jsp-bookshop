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
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {


    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private TossPaymentClient tossPaymentClient;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    @DisplayName("카드 결제 승인 성공 시 결제/주문 상태 변경, 재고 차감, 장바구니 삭제")
    void confirmPayment_success() {
        // given
        Long memberId = 1L;
        Long orderId = 100L;
        Long paymentId = 1000L;

        String paymentKey = "payment-key-test";
        String tossOrderId = "BOOKSHOP-100";
        Integer amount = 80000;

        Payment payment = createPayment(paymentId, orderId, tossOrderId, amount);

        when(paymentMapper.findByTossOrderId(tossOrderId))
                .thenReturn(Optional.of(payment));

        TossConfirmResponse tossResponse = createConfirmResponse(paymentKey, tossOrderId, "카드", amount, null);

        when(tossPaymentClient.confirm(any(TossConfirmRequest.class)))
                .thenReturn(tossResponse);

        List<OrderFormItemResponse> items = List.of(
                createOrderItem(10L, 100L, "자바의 정석", 30000, 2),
                createOrderItem(20L, 200L, "스프링 입문", 20000, 1)
        );

        when(orderMapper.findOrderItemsForPayment(orderId))
                .thenReturn(items);

        when(orderMapper.decreaseProductStock(100L, 2))
                .thenReturn(1);
        when(orderMapper.decreaseProductStock(200L, 1))
                .thenReturn(1);

        when(orderMapper.deletePaidOrderCartItems(memberId, orderId))
                .thenReturn(2);

        // when

        Long result = paymentService.confirmPayment(
                memberId,
                paymentKey,
                tossOrderId,
                amount
        );

        // then

        assertThat(result).isEqualTo(orderId);

        ArgumentCaptor<TossConfirmRequest> tossRequestCaptor = ArgumentCaptor.forClass(TossConfirmRequest.class);

        verify(tossPaymentClient).confirm(tossRequestCaptor.capture());

        TossConfirmRequest tossRequest = tossRequestCaptor.getValue();

        assertThat(tossRequest.getPaymentKey()).isEqualTo(paymentKey);
        assertThat(tossRequest.getOrderId()).isEqualTo(tossOrderId);
        assertThat(tossRequest.getAmount()).isEqualTo(amount);

        verify(orderMapper).decreaseProductStock(100L,2);
        verify(orderMapper).decreaseProductStock(200L,1);

        verify(paymentMapper).updatePaid(
                paymentId,
                paymentKey,
                PaymentMethod.CARD
        );

        verify(orderMapper).updateOrderStatus(orderId, OrderStatus.PAID);
        verify(orderMapper).deletePaidOrderCartItems(memberId, orderId);

    }

    @Test
    @DisplayName("토스페이 결제 승인 성공 시 결제수단을 TOSS_PAY로 저장")
    void confirmPayment_success_tossPay() {
        // given

        Long memberId = 1L;
        Long orderId = 10L;
        Long paymentId = 100L;

        String paymentKey = "payment-key-test";
        String tossOrderId = "BOOKSHOP-100";

        Integer amount = 30000;

        Payment payment = createPayment(paymentId, orderId, tossOrderId, amount);

        when(paymentMapper.findByTossOrderId(tossOrderId))
                .thenReturn(Optional.of(payment));

        TossConfirmResponse confirmResponse = createConfirmResponse(paymentKey, tossOrderId, "간편결제", amount, "토스페이");

        when(tossPaymentClient.confirm(any(TossConfirmRequest.class)))
                .thenReturn(confirmResponse);

        List<OrderFormItemResponse> items = List.of(
                createOrderItem(10L, 100L, "자바의 정석", 30000, 1)
        );

        when(orderMapper.findOrderItemsForPayment(orderId))
                .thenReturn(items);

        when(orderMapper.decreaseProductStock(100L, 1))
                .thenReturn(1);

        when(orderMapper.deletePaidOrderCartItems(memberId, orderId))
                .thenReturn(1);


        // when
        Long result = paymentService.confirmPayment(memberId, paymentKey, tossOrderId, amount);

        // then

        assertThat(result).isEqualTo(orderId);

        verify(paymentMapper).updatePaid(
                paymentId,
                paymentKey,
                PaymentMethod.TOSS_PAY
        );

        verify(orderMapper).updateOrderStatus(orderId, OrderStatus.PAID);
        verify(orderMapper).deletePaidOrderCartItems(memberId, orderId);
    }

    @Test
    @DisplayName("결제 정보가 없으면 예외 발생")
    void confirmPayment_fail_paymentNotFound() {
        // given
        Long memberId = 1L;
        String paymentKey = "payment-key-test";
        String tossOrderId = "BOOKSHOP-100";
        Integer amount = 80000;

        when(paymentMapper.findByTossOrderId(tossOrderId))
                .thenReturn(Optional.empty());

        // when

        // then

        assertThatThrownBy(() -> paymentService.confirmPayment(
                memberId, paymentKey, tossOrderId, amount
        )).isInstanceOf(OrderInfoException.class)
                .hasMessage("결제 정보를 찾을 수 없습니다.");

        verify(tossPaymentClient, never()).confirm(any(TossConfirmRequest.class));
        verify(orderMapper, never()).findOrderItemsForPayment(anyLong());
        verify(paymentMapper, never()).updatePaid(anyLong(), anyString(), any(PaymentMethod.class));
        verify(orderMapper, never()).updateOrderStatus(anyLong(), any(OrderStatus.class));

    }

    @Test
    @DisplayName("결제 승인 후 재고 차감 실패 시 예외 발생")
    void confirmPayment_fail_stockDecreaseFail() {
        // given
        Long memberId = 1L;
        Long orderId = 100L;
        Long paymentId = 1000L;

        String paymentKey = "payment-key-test";
        String tossOrderId = "BOOKSHOP-100";
        Integer amount = 80000;

        Payment payment = createPayment(paymentId, orderId, tossOrderId, amount);

        when(paymentMapper.findByTossOrderId(tossOrderId))
                .thenReturn(Optional.of(payment));

        TossConfirmResponse tossResponse = createConfirmResponse(
                paymentKey,
                tossOrderId,
                "카드",
                amount,
                null
        );

        when(tossPaymentClient.confirm(any(TossConfirmRequest.class)))
                .thenReturn(tossResponse);

        List<OrderFormItemResponse> items = List.of(
                createOrderItem(10L, 100L, "자바의 정석", 30000, 2)
        );

        when(orderMapper.findOrderItemsForPayment(orderId))
                .thenReturn(items);

        when(orderMapper.decreaseProductStock(100L, 2))
                .thenReturn(0);

        // when & then
        assertThatThrownBy(() -> paymentService.confirmPayment(
                memberId,
                paymentKey,
                tossOrderId,
                amount
        ))
                .isInstanceOf(StockQuantityExceedException.class)
                .hasMessage("재고 수량을 초과한 상품이 있습니다.");

        verify(tossPaymentClient).confirm(any(TossConfirmRequest.class));
        verify(orderMapper).decreaseProductStock(100L, 2);

        verify(paymentMapper, never()).updatePaid(anyLong(), anyString(), any(PaymentMethod.class));
        verify(orderMapper, never()).updateOrderStatus(anyLong(), any(OrderStatus.class));
        verify(orderMapper, never()).deletePaidOrderCartItems(anyLong(), anyLong());
    }

    private OrderFormItemResponse createOrderItem(
            Long cartItemId,
            Long productId,
            String productName,
            Integer price,
            Integer quantity
    ) {
        OrderFormItemResponse item = new OrderFormItemResponse();

        item.setCartItemId(cartItemId);
        item.setProductId(productId);
        item.setProductName(productName);
        item.setPrice(price);
        item.setQuantity(quantity);
        item.setStockQuantity(10);

        return item;
    }

    public Payment createPayment(Long paymentId, Long orderId, String tossOrderId, Integer amount) {

        Payment payment = Payment.ready(orderId, tossOrderId, amount);

        ReflectionTestUtils.setField(payment, "paymentId", paymentId);

        return payment;
    }

    public TossConfirmResponse createConfirmResponse(
            String paymentKey, String orderId, String method, Integer amount, String easyPayProvider
    ) {
        TossConfirmResponse response = new TossConfirmResponse();

        response.setPaymentKey(paymentKey);
        response.setOrderId(orderId);
        response.setMethod(method);
        response.setTotalAmount(amount);

        if (easyPayProvider != null) {
            TossConfirmResponse.EasyPay easyPay = new TossConfirmResponse.EasyPay();

            easyPay.setProvider(easyPayProvider);
            easyPay.setAmount(amount);
            easyPay.setDiscountAmount(0);

            response.setEasyPay(easyPay);
        }

        return response;


    }

}