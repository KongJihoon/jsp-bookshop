package hello.bookshop.order.service;

import hello.bookshop.common.dto.PageRequest;
import hello.bookshop.common.dto.PageResponse;
import hello.bookshop.common.exception.order.OrderInfoException;
import hello.bookshop.order.dto.response.AdminOrderDetailResponse;
import hello.bookshop.order.dto.response.AdminOrderListResponse;
import hello.bookshop.order.dto.response.OrderDetailItemResponse;
import hello.bookshop.order.mapper.OrderMapper;
import hello.bookshop.order.type.OrderStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminOrderServiceTest {

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private AdminOrderService adminOrderService;

    private PageRequest pageRequest;

    @BeforeEach
    void setup() {
        pageRequest = new PageRequest(1, 10, null, null, null);
    }
    @Test
    @DisplayName("관리자 주문 목록 페이징 조회 성공")
    void findAdminOrders_success() {
        // given

        AdminOrderListResponse firstOrder = createAdminOrderListResponse(
                1L,
                "testName",
                "testProductName",
                1,
                30000,
                OrderStatus.PAID
        );

        AdminOrderListResponse secondOrder = createAdminOrderListResponse(
                2L,
                "testName2",
                "testProductName2",
                2,
                50000,
                OrderStatus.SHIPPING
        );

        when(orderMapper.findAdminOrders(pageRequest))
                .thenReturn(List.of(firstOrder, secondOrder));

        when(orderMapper.countAdminOrders())
                .thenReturn(2);
        // when

        PageResponse<AdminOrderListResponse> result = adminOrderService.findAdminOrders(pageRequest);

        // then

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalCount()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.isHasPrevious()).isFalse();
        assertThat(result.isHasNext()).isFalse();

        assertThat(result.getContent().get(0).getOrderId()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getOrderStatus()).isEqualTo(OrderStatus.PAID);

        verify(orderMapper).findAdminOrders(pageRequest);
        verify(orderMapper).countAdminOrders();
    }

    @Test
    @DisplayName("관리자 주문 상세 조회 성공")
    void findAdminOrderDetail_success() {
        // given

        Long orderId = 1L;

        AdminOrderDetailResponse order = new AdminOrderDetailResponse();
        order.setOrderId(orderId);
        order.setMemberName("testMemberName");
        order.setMemberEmail("test@test.com");
        order.setOrderStatus(OrderStatus.PAID);
        order.setTotalPrice(80000);
        order.setOrderedAt(LocalDateTime.now());
        order.setReceiverName("testReceiverName");
        order.setReceiverPhone("010-1111-2222");
        order.setZipcode("12345");
        order.setAddress("서울시 강남구");
        order.setAddressDetail("101호");

        OrderDetailItemResponse firstItem =
                createOrderDetailItemResponse(1L, "자바의 정석", 30000, 2);

        OrderDetailItemResponse secondItem =
                createOrderDetailItemResponse(2L, "스프링 입문", 20000, 1);

        when(orderMapper.findAdminOrderDetailsByOrderId(orderId))
                .thenReturn(order);

        when(orderMapper.findOrderDetailItemsByOrderId(orderId))
                .thenReturn(List.of(firstItem, secondItem));

        // when
        AdminOrderDetailResponse result = adminOrderService.findAdminOrderDetail(orderId);

        // then

        assertThat(result.getOrderId()).isEqualTo(orderId);
        assertThat(result.getMemberName()).isEqualTo("testMemberName");
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(result.getItems()).hasSize(2);


        verify(orderMapper).findAdminOrderDetailsByOrderId(orderId);
        verify(orderMapper).findOrderDetailItemsByOrderId(orderId);

    }

    @Test
    @DisplayName("관리자 주문 상세 조회 시 존재하지 않는 주문 내역 조회 예외 발생")
    void findAdminOrderDetail_fail_notFoundOrder() {
        // given

        Long orderId = 1L;

        when(orderMapper.findAdminOrderDetailsByOrderId(orderId))
                .thenReturn(null);

        // when

        // then

        assertThatThrownBy(() -> adminOrderService.findAdminOrderDetail(orderId))
                .isInstanceOf(OrderInfoException.class)
                .hasMessage("주문 내역을 찾을 수 없습니다.");

        verify(orderMapper).findAdminOrderDetailsByOrderId(orderId);
        verify(orderMapper, never()).findOrderDetailItemsByOrderId(orderId);

    }

    @Test
    @DisplayName("관리자 주문 상태 변경 성공")
    void updateOrderStatus_success() {
        // given
        Long orderId = 1L;

        when(orderMapper.findOrderStatusByOrderId(orderId))
                .thenReturn(OrderStatus.PAID);

        when(orderMapper.updateAdminOrderStatus(orderId, OrderStatus.PREPARING))
                .thenReturn(1);

        // when
        adminOrderService.updateOrderStatus(orderId, OrderStatus.PREPARING);

        // then

        verify(orderMapper).findOrderStatusByOrderId(orderId);
        verify(orderMapper).updateAdminOrderStatus(orderId, OrderStatus.PREPARING);

    }

    @Test
    @DisplayName("존재하지 않는 주문 상태 변경 시 예외 발생")
    void updateOrderStatus_fail_notFoundOrder() {
        // given
        Long orderId = 999L;

        when(orderMapper.findOrderStatusByOrderId(orderId))
                .thenReturn(null);

        // when

        // then

        assertThatThrownBy(() -> adminOrderService.updateOrderStatus(orderId,OrderStatus.PREPARING))
                .isInstanceOf(OrderInfoException.class)
                .hasMessage("주문 내역을 찾을 수 없습니다.");

        verify(orderMapper).findOrderStatusByOrderId(orderId);
        verify(orderMapper, never()).updateAdminOrderStatus(orderId, OrderStatus.PREPARING);
    }

    @Test
    @DisplayName("허용되지 않는 주문 상태 변경 시 예외 발생")
    void updateOrderStatus_fail_invalidStatusChange() {
        // given

        Long orderId = 1L;

        when(orderMapper.findOrderStatusByOrderId(orderId))
                .thenReturn(OrderStatus.PAID);

        // when

        // then

        assertThatThrownBy(() -> adminOrderService.updateOrderStatus(orderId, OrderStatus.DELIVERED))
                .isInstanceOf(OrderInfoException.class)
                .hasMessage("변경할 수 없는 주문 상태입니다.");

        verify(orderMapper).findOrderStatusByOrderId(orderId);
        verify(orderMapper, never()).updateAdminOrderStatus(anyLong(), any(OrderStatus.class));

    }

    @Test
    @DisplayName("주문 상태 변경 update count가 0이면 예외발생")
    void updateOrderStatus_fail_updateCountZero() {
        // given

        Long orderId = 1L;

        when(orderMapper.findOrderStatusByOrderId(orderId))
                .thenReturn(OrderStatus.PAID);

        when(orderMapper.updateAdminOrderStatus(orderId, OrderStatus.PREPARING))
                .thenReturn(0);
        // when

        // then

        assertThatThrownBy(() -> adminOrderService.updateOrderStatus(orderId, OrderStatus.PREPARING))
                .isInstanceOf(OrderInfoException.class)
                .hasMessage("주문 상태 변경에 실패하였습니다.");

        verify(orderMapper).findOrderStatusByOrderId(orderId);
        verify(orderMapper).updateAdminOrderStatus(orderId, OrderStatus.PREPARING);

    }

    private AdminOrderListResponse createAdminOrderListResponse(
            Long orderId,
            String memberName,
            String representativeProductName,
            Integer totalItemCount,
            Integer totalPrice,
            OrderStatus orderStatus
    ) {
        AdminOrderListResponse response = new AdminOrderListResponse();

        response.setOrderId(orderId);
        response.setMemberName(memberName);
        response.setRepresentativeProductName(representativeProductName);
        response.setTotalItemCount(totalItemCount);
        response.setTotalPrice(totalPrice);
        response.setOrderStatus(orderStatus);
        response.setOrderedAt(LocalDateTime.now());

        return response;
    }

    private OrderDetailItemResponse createOrderDetailItemResponse(
            Long productId,
            String productName,
            Integer price,
            Integer quantity
    ) {
        OrderDetailItemResponse response = new OrderDetailItemResponse();

        response.setProductId(productId);
        response.setProductName(productName);
        response.setPrice(price);
        response.setQuantity(quantity);
        response.setItemTotalPrice(price * quantity);

        return response;
    }

}