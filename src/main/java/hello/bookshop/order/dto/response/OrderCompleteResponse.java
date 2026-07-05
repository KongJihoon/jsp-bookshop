package hello.bookshop.order.dto.response;

import lombok.Getter;

@Getter
public class OrderCompleteResponse {

    private final Long orderId;

    private final Integer totalPrice;

    public OrderCompleteResponse(Long orderId, Integer totalPrice) {
        this.orderId = orderId;
        this.totalPrice = totalPrice;
    }

}
