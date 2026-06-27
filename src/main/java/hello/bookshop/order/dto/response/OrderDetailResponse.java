package hello.bookshop.order.dto.response;


import hello.bookshop.order.type.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
public class OrderDetailResponse {


    private Long orderId;

    private OrderStatus orderStatus;

    private Integer totalPrice;

    private LocalDateTime orderedAt;

    private String receiverName;

    private String receiverPhone;

    private String zipcode;

    private String address;

    private String addressDetail;

    private List<OrderDetailItemResponse> items;

    public String getFormattedOrderedAt() {
        if (orderedAt == null) {
            return "";
        }

        return orderedAt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
    }

}
