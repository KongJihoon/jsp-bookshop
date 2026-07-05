package hello.bookshop.payment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TossConfirmRequest {

    private String paymentKey;

    private String orderId;

    private Integer amount;

}
