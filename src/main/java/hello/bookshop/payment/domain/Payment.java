package hello.bookshop.payment.domain;

import hello.bookshop.payment.type.PaymentMethod;
import hello.bookshop.payment.type.PaymentStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    private Long paymentId;

    private Long orderId;

    private String tossOrderId;

    private String paymentKey;

    private Integer amount;

    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private LocalDateTime approvedAt;

    private String failedReason;

    @Builder(access = AccessLevel.PRIVATE)
    private Payment(Long orderId, String tossOrderId, Integer amount) {
        this.orderId = orderId;
        this.tossOrderId = tossOrderId;
        this.amount = amount;
        this.paymentStatus = PaymentStatus.READY;
    }

    public static Payment ready(Long orderId, String tossOrderId, Integer amount) {
        return Payment.builder()
                .orderId(orderId)
                .tossOrderId(tossOrderId)
                .amount(amount)
                .build();
    }
}
