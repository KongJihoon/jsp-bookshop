package hello.bookshop.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentCheckoutResponse {

    private Long orderId;

    private String tossOrderId;

    private Integer amount;

    private String orderName;

    private String customerName;

    private String customerKey;

}
