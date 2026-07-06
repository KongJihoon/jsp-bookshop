package hello.bookshop.order.dto.response;

import hello.bookshop.order.type.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class AdminOrderListResponse {

    private Long orderId;

    private String memberName;

    private String representativeProductName;

    private Integer totalItemCount;

    private Integer totalPrice;

    private OrderStatus orderStatus;

    private LocalDateTime orderedAt;

    public String getOrderStatusDescription() {

        return orderStatus.getDescription();
    }

    public String getOrderedAtText() {
        if (orderedAt == null) {
            return "";
        }

        return orderedAt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
    }

}
