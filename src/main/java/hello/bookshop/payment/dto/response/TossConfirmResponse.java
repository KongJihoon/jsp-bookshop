package hello.bookshop.payment.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TossConfirmResponse {

    private String paymentKey;
    private String orderId;
    private String method;
    private Integer totalAmount;
    private EasyPay easyPay;

    @Getter
    @Setter
    public static class EasyPay {
        private String provider;
        private Integer amount;
        private Integer discountAmount;
    }

    public String getEasyPayProvider() {
        if (easyPay == null) {
            return null;
        }

        return easyPay.getProvider();
    }
}
