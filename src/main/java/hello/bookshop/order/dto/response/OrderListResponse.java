package hello.bookshop.order.dto.response;

import hello.bookshop.order.type.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class OrderListResponse {

    private Long orderId;

    private OrderStatus orderStatus;

    private Integer totalPrice;

    private LocalDateTime orderedAt;

    private String representativeProductName;

    private String representativeImagePath;

    private Integer totalItemCount;

    public String getDisplayProductName() {
        if (totalItemCount == null || totalItemCount <= 1) {
            return representativeProductName;
        }

        return representativeProductName + " 외 " + (totalItemCount - 1) + "권";
    }

    public String getFormattedOrderedAt() {
        if (orderedAt == null) {
            return "";
        }

        return orderedAt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }

}
