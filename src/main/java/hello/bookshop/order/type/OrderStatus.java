package hello.bookshop.order.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {

    READY("주문대기"),
    PAID("결제완료"),
    PREPARING("배송준비"),
    SHIPPING("배송중"),
    DELIVERED("배송완료"),
    CANCELED("주문취소"),
    FAILED("결제실패");

    private final String description;

    public boolean canChangStatus(OrderStatus nextStatus) {

        if (this == PAID) {
            return nextStatus == PREPARING;
        }

        if (this == PREPARING) {
            return nextStatus == SHIPPING;
        }

        if (this == SHIPPING) {
            return nextStatus == DELIVERED;
        }


        return false;

    }
}
