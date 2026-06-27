package hello.bookshop.member.dto.response;

import hello.bookshop.order.dto.response.OrderListResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class MyPageHomeResponse {

    private final int recentOrderCount;

    private final int cartItemCount;

    private final List<OrderListResponse> recentOrders;

    public MyPageHomeResponse(int recentOrderCount, int cartItemCount, List<OrderListResponse> recentOrders) {

        this.recentOrderCount = recentOrderCount;
        this.cartItemCount = cartItemCount;
        this.recentOrders = recentOrders;
    }

}
