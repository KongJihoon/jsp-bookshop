package hello.bookshop.payment.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentMethod {

    CARD("카드"),
    TOSS_PAY("토스페이"),
    KAKAO_PAY("카카오페이"),
    NAVER_PAY("네이버페이"),
    PAYCO("페이코"),
    EASY_PAY("간편결제"),
    UNKNOWN("알 수 없음");



    private final String description;


    public static PaymentMethod from(String method, String easyPayProvider) {
        if ("카드".equals(method)) {
            return CARD;
        }

        if ("간편결제".equals(method)) {
            return fromEasyPayProvider(easyPayProvider);
        }

        return UNKNOWN;
    }

    private static PaymentMethod fromEasyPayProvider(String provider) {

        if (provider == null || provider.isBlank()) {
            return EASY_PAY;
        }

        return switch (provider) {
            case "토스페이" -> TOSS_PAY;
            case "카카오페이" -> KAKAO_PAY;
            case "네이버페이" -> NAVER_PAY;
            case "페이코" -> PAYCO;
            default -> EASY_PAY;
        };

    }
}
