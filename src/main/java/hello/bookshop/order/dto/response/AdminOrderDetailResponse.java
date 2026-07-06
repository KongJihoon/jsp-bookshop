package hello.bookshop.order.dto.response;

import hello.bookshop.order.type.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AdminOrderDetailResponse {

    private Long orderId;

    private String memberName;

    private String memberEmail;

    private OrderStatus orderStatus;

    private Integer totalPrice;

    private LocalDateTime orderedAt;

    private String receiverName;

    private String receiverPhone;

    private String zipcode;

    private String address;

    private String addressDetail;

    private List<OrderDetailItemResponse> items = new ArrayList<>();

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
