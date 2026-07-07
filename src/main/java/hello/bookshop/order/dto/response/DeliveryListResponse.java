package hello.bookshop.order.dto.response;

import hello.bookshop.order.type.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class DeliveryListResponse {

    private Long orderId;

    private OrderStatus orderStatus;

    private Integer totalPrice;

    private LocalDateTime orderedAt;

    private String representativeProductName;

    private String representativeImagePath;

    private Integer totalItemCount;

    private String receiverName;

    private String zipcode;

    private String address;

    private String addressDetail;

    public String getOrderStatusDescription() {
        return orderStatus.getDescription();
    }

    public String getFormattedOrderedAt() {

        return orderedAt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }

    public String getDisplayProductName() {
        if (totalItemCount != null && totalItemCount > 1) {
            return representativeProductName + " 외 " + (totalItemCount - 1) + "건";
        }

        return representativeProductName;
    }

    public String getOrderStatusName() {
        return orderStatus.name();
    }

}
