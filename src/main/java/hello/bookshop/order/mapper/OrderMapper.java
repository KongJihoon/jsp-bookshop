package hello.bookshop.order.mapper;

import hello.bookshop.order.domain.Order;
import hello.bookshop.order.domain.OrderItem;
import hello.bookshop.order.dto.response.OrderDetailItemResponse;
import hello.bookshop.order.dto.response.OrderDetailResponse;
import hello.bookshop.order.dto.response.OrderFormItemResponse;
import hello.bookshop.order.dto.response.OrderListResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {

    List<OrderFormItemResponse> findOrderFormItemsByCartItemIds(
            @Param("memberId") Long memberId,
            @Param("cartItemIds") List<Long> cartItemIds
    );

    void saveOrder(Order order);
    void saveOrderItem(OrderItem orderItem);

    int decreaseProductStock(
            @Param("productId") Long productId,
            @Param("quantity") Integer quantity
    );

    int deleteOrderCartItems(
            @Param("memberId") Long memberId,
            @Param("cartItemIds") List<Long> cartItemIds
    );
    List<OrderListResponse> findOrdersByMemberId(Long memberId);


    OrderDetailResponse findOrderDetailByOrderId(
            @Param("memberId") Long memberId,
            @Param("orderId") Long orderId
    );

    List<OrderDetailItemResponse> findOrderDetailItemsByOrderId(@Param("orderId") Long orderId);

    int countOrdersByMemberId(Long memberId);

    List<OrderListResponse> findRecentOrdersByMemberId(Long memberId);
}
