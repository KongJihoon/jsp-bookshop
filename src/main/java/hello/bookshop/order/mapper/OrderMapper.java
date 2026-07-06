package hello.bookshop.order.mapper;

import hello.bookshop.common.dto.PageRequest;
import hello.bookshop.order.domain.Order;
import hello.bookshop.order.domain.OrderItem;
import hello.bookshop.order.dto.response.*;
import hello.bookshop.order.type.OrderStatus;
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

    // tossPayment

    List<OrderFormItemResponse> findOrderItemsForPayment(Long orderId);

    void updateOrderStatus(
            @Param("orderId") Long orderId,
            @Param("orderStatus") OrderStatus orderStatus
    );

    int deletePaidOrderCartItems(
            @Param("memberId") Long memberId,
            @Param("orderId") Long orderId
    );

    List<AdminOrderListResponse> findAdminOrders(PageRequest pageRequest);

    int countAdminOrders();

    AdminOrderDetailResponse findAdminOrderDetailsByOrderId(Long orderId);

    OrderStatus findOrderStatusByOrderId(Long orderId);

    int updateAdminOrderStatus(
            @Param("orderId") Long orderId,
            @Param("orderStatus") OrderStatus orderStatus
    );

    boolean existsByActiveOrderByMemberId(Long memberId);

}
