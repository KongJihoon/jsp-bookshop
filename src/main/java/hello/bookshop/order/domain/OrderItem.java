package hello.bookshop.order.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    private Long orderItemId;

    private Long orderId;

    private Long productId;

    private String productName;

    private Integer price;

    private Integer quantity;

    private Integer itemTotalPrice;

    private LocalDateTime createdAt;

    @Builder(access = AccessLevel.PRIVATE)
    private OrderItem(
            Long orderId,
            Long productId,
            String productName,
            Integer price,
            Integer quantity
    ) {
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.itemTotalPrice = price * quantity;
    }

    public static OrderItem create(
            Long orderId,
            Long productId,
            String productName,
            Integer price,
            Integer quantity
    ) {
        return OrderItem.builder()
                .orderId(orderId)
                .productId(productId)
                .productName(productName)
                .price(price)
                .quantity(quantity)
                .build();
    }

}
