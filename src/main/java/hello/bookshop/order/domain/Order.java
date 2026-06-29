package hello.bookshop.order.domain;

import hello.bookshop.order.type.OrderStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    private Long orderId;

    private Long memberId;

    private OrderStatus orderStatus;

    private String receiverName;

    private String receiverPhone;

    private String zipcode;

    private String address;

    private String addressDetail;

    private Integer totalPrice;

    private LocalDateTime orderedAt;

    private LocalDateTime canceledAt;

    @Builder(access = AccessLevel.PRIVATE)
    private Order(Long memberId, String receiverName, String receiverPhone, String zipcode, String address, String addressDetail, Integer totalPrice) {

        this.memberId = memberId;
        this.orderStatus = OrderStatus.READY;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.zipcode = zipcode;
        this.address = address;
        this.addressDetail = addressDetail;
        this.totalPrice = totalPrice;
    }

    public static Order create(Long memberId, String receiverName, String receiverPhone, String zipcode, String address, String addressDetail, Integer totalPrice) {

        return Order.builder()
                .memberId(memberId)
                .receiverName(receiverName)
                .receiverPhone(receiverPhone)
                .zipcode(zipcode)
                .address(address)
                .addressDetail(addressDetail)
                .totalPrice(totalPrice)
                .build();
    }

}
