package hello.bookshop.order.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class OrderFormResponse {

    private final List<OrderFormItemResponse> items;

    private final Integer totalPrice;

    public OrderFormResponse(List<OrderFormItemResponse> items) {
        this.items = items;

        this.totalPrice = items.stream()
                .mapToInt(OrderFormItemResponse::getItemTotalPrice)
                .sum();
    }

}
