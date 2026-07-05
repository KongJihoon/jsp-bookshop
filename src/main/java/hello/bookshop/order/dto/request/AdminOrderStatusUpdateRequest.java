package hello.bookshop.order.dto.request;


import hello.bookshop.order.type.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminOrderStatusUpdateRequest {

    @NotNull(message = "변경할 주문 상태를 선택해주세요.")
    private OrderStatus orderStatus;

}
