package hello.bookshop.order.service;

import hello.bookshop.common.dto.PageRequest;
import hello.bookshop.common.dto.PageResponse;
import hello.bookshop.common.exception.order.OrderInfoException;
import hello.bookshop.order.dto.response.AdminOrderDetailResponse;
import hello.bookshop.order.dto.response.AdminOrderListResponse;
import hello.bookshop.order.dto.response.OrderDetailItemResponse;
import hello.bookshop.order.mapper.OrderMapper;
import hello.bookshop.order.type.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminOrderService {

    private final OrderMapper orderMapper;

    @Transactional(readOnly = true)
    public PageResponse<AdminOrderListResponse> findAdminOrders(PageRequest pageRequest) {

        List<AdminOrderListResponse> orders = orderMapper.findAdminOrders(pageRequest);

        int totalCount = orderMapper.countAdminOrders();


        return new PageResponse<>(
                orders,
                pageRequest.getPage(),
                pageRequest.getSize(),
                totalCount
        );
    }

    @Transactional(readOnly = true)
    public AdminOrderDetailResponse findAdminOrderDetail(Long orderId) {

        AdminOrderDetailResponse order = orderMapper.findAdminOrderDetailsByOrderId(orderId);

        if (order == null) {
            throw new OrderInfoException("주문 내역을 찾을 수 없습니다.");
        }

        List<OrderDetailItemResponse> items = orderMapper.findOrderDetailItemsByOrderId(orderId);

        order.setItems(items);

        return order;
    }

    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus nextStatus) {

        OrderStatus currentStatus = orderMapper.findOrderStatusByOrderId(orderId);

        if (currentStatus == null) {
            throw new OrderInfoException("주문 내역을 찾을 수 없습니다.");
        }

        if (!currentStatus.canChangStatus(nextStatus)) {
            throw new OrderInfoException("변경할 수 없는 주문 상태입니다.");
        }

        int updatedCount = orderMapper.updateAdminOrderStatus(orderId, nextStatus);

        if (updatedCount == 0) {
            throw new OrderInfoException("주문 상태 변경에 실패하였습니다.");
        }

    }

}
