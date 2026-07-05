package hello.bookshop.payment.mapper;

import hello.bookshop.payment.domain.Payment;
import hello.bookshop.payment.type.PaymentMethod;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface PaymentMapper {

    void save(Payment payment);

    Optional<Payment> findByTossOrderId(String tossOrderId);

    void updatePaid(
            @Param("paymentId") Long paymentId,
            @Param("paymentKey") String paymentKey,
            @Param("paymentMethod") PaymentMethod paymentMethod
    );

    void updateFailed(
            @Param("tossOrderId") String tossOrderId,
            @Param("failedReason") String failedReason
    );

}
