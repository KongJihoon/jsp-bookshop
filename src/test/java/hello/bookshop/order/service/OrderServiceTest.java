package hello.bookshop.order.service;

import hello.bookshop.common.exception.order.OrderInfoException;
import hello.bookshop.common.exception.product.ProductNotFoundException;
import hello.bookshop.common.exception.product.StockQuantityExceedException;
import hello.bookshop.member.domain.Member;
import hello.bookshop.member.mapper.MemberMapper;
import hello.bookshop.order.domain.Order;
import hello.bookshop.order.domain.OrderItem;
import hello.bookshop.order.dto.request.OrderCreateRequest;
import hello.bookshop.order.dto.response.OrderCompleteResponse;
import hello.bookshop.order.dto.response.OrderFormItemResponse;
import hello.bookshop.order.dto.response.OrderFormResponse;
import hello.bookshop.order.mapper.OrderMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private MemberMapper memberMapper;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("장바구니 상품 주문서 생성 기능")
    void getCartOrderForm() {
        // given

        Long memberId = 1L;
        List<Long> cartItemIds = List.of(10L, 20L);

        givenLoginMember(memberId);

        List<OrderFormItemResponse> items = List.of(
                createOrderFormItem(10L, 100L, "자바의 정석", 30000, 2, 10),
                createOrderFormItem(20L, 200L, "스프링 입문", 20000, 1, 5)
        );

        when(orderMapper.findOrderFormItemsByCartItemIds(memberId, cartItemIds))
                .thenReturn(items);

        // when

        OrderFormResponse result = orderService.getCartOrderForm(memberId, cartItemIds);

        // then

        assertThat(result.getItems()).hasSize(2);
        assertThat(result.getTotalPrice()).isEqualTo(80000);

        verify(memberMapper).findMemberByIdAndWithdrawnAtIsNull(memberId);
        verify(orderMapper).findOrderFormItemsByCartItemIds(memberId, cartItemIds);

    }

    @Test
    @DisplayName("요청한 장바구니 상품 수와 조회된 상품 수가 다르면 예외 발생")
    void getCartOrderForm_fail_invalidCartItemIncluded() {
        // given

        Long memberId = 1L;
        List<Long> cartItemIds = List.of(10L, 20L);
        givenLoginMember(memberId);
        List<OrderFormItemResponse> items = List.of(
                createOrderFormItem(10L, 100L, "자바의 정석", 30000, 1, 10)
        );

        when(orderMapper.findOrderFormItemsByCartItemIds(memberId, cartItemIds))
                .thenReturn(items);
        // when

        // then

        assertThatThrownBy(() -> orderService.getCartOrderForm(memberId, cartItemIds))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("주문할 수 없는 상품이 포함되어 있습니다.");

    }

    @Test
    @DisplayName("장바구니 상품 주문 생성")
    void createCartOrder() {
        // given
        Long memberId = 1L;
        List<Long> cartItemIds = List.of(10L, 20L);

        givenLoginMember(memberId);

        OrderCreateRequest request = createOrderCreateRequest(cartItemIds);

        List<OrderFormItemResponse> items = List.of(
                createOrderFormItem(10L, 100L, "자바의 정석", 30000, 2, 10),
                createOrderFormItem(20L, 200L, "스프링 입문", 20000, 1, 5)
        );

        when(orderMapper.findOrderFormItemsByCartItemIds(memberId, cartItemIds))
                .thenReturn(items);

        doAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            ReflectionTestUtils.setField(order, "orderId", 1000L);
            return null;
        }).when(orderMapper).saveOrder(any(Order.class));

        when(orderMapper.decreaseProductStock(anyLong(), anyInt()))
                .thenReturn(1);
        when(orderMapper.deleteOrderCartItems(memberId, cartItemIds))
                .thenReturn(2);

        // when
        OrderCompleteResponse result = orderService.createCartOrder(memberId, request);

        // then

        assertThat(result.getOrderId()).isEqualTo(1000L);
        assertThat(result.getTotalPrice()).isEqualTo(80000);
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

        verify(orderMapper).saveOrder(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();

        assertThat(savedOrder.getMemberId()).isEqualTo(memberId);
        assertThat(savedOrder.getReceiverName()).isEqualTo("공지훈");
        assertThat(savedOrder.getReceiverPhone()).isEqualTo("010-1234-5678");
        assertThat(savedOrder.getZipcode()).isEqualTo("12345");
        assertThat(savedOrder.getAddress()).isEqualTo("서울시 강남구");
        assertThat(savedOrder.getAddressDetail()).isEqualTo("101호");
        assertThat(savedOrder.getTotalPrice()).isEqualTo(80000);

        ArgumentCaptor<OrderItem> orderItemCaptor = ArgumentCaptor.forClass(OrderItem.class);

        verify(orderMapper, times(2)).saveOrderItem(orderItemCaptor.capture());

        List<OrderItem> savedOrderItems = orderItemCaptor.getAllValues();

        assertThat(savedOrderItems).hasSize(2);
        assertThat(savedOrderItems.get(0).getOrderId()).isEqualTo(1000L);
        assertThat(savedOrderItems.get(0).getProductId()).isEqualTo(100L);
        assertThat(savedOrderItems.get(0).getProductName()).isEqualTo("자바의 정석");
        assertThat(savedOrderItems.get(0).getPrice()).isEqualTo(30000);
        assertThat(savedOrderItems.get(0).getQuantity()).isEqualTo(2);
        assertThat(savedOrderItems.get(0).getItemTotalPrice()).isEqualTo(60000);

        verify(orderMapper).decreaseProductStock(100L,2);
        verify(orderMapper).decreaseProductStock(200L, 1);
        verify(orderMapper).deleteOrderCartItems(memberId, cartItemIds);

    }

    @Test
    @DisplayName("재고 차감 실패 시 주문 상품 저장 불가")
    void createCartOrder_fail_stockDecreaseFail() {
        // given
        Long memberId = 1L;
        List<Long> cartItemIds = List.of(10L);

        givenLoginMember(memberId);

        OrderCreateRequest request = createOrderCreateRequest(cartItemIds);

        List<OrderFormItemResponse> items = List.of(
                createOrderFormItem(10L, 100L, "자바의 정석", 30000, 1, 10)
        );

        when(orderMapper.findOrderFormItemsByCartItemIds(memberId, cartItemIds))
                .thenReturn(items);

        doAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            ReflectionTestUtils.setField(order, "orderId", 100L);
            return null;
        }).when(orderMapper).saveOrder(any(Order.class));

        when(orderMapper.decreaseProductStock(100L, 1))
                .thenReturn(0);

        // when

        // then

        assertThatThrownBy(() -> orderService.createCartOrder(memberId, request))
                .isInstanceOf(StockQuantityExceedException.class)
                .hasMessage("재고 수량을 초과한 상품이 있습니다.");

        verify(orderMapper).saveOrder(any(Order.class));
        verify(orderMapper).decreaseProductStock(100L, 1);
        verify(orderMapper, never()).saveOrderItem(any(OrderItem.class));
        verify(orderMapper, never()).deleteOrderCartItems(anyLong(), anyList());

    }


    private void givenLoginMember(Long memberId) {

        Member member = Member.signUp(
                "test",
                "password",
                "공지훈",
                "test@test.com",
                "010-1234-5678",
                "12345",
                "서울시 강남구",
                "101호"
        );

        ReflectionTestUtils.setField(member, "memberId", memberId);

        when(memberMapper.findMemberByIdAndWithdrawnAtIsNull(memberId))
                .thenReturn(Optional.of(member));
    }

    private OrderFormItemResponse createOrderFormItem(
            Long cartItemId,
            Long productId,
            String productName,
            Integer price,
            Integer quantity,
            Integer stockQuantity
    ) {
        OrderFormItemResponse response = new OrderFormItemResponse();
        response.setCartItemId(cartItemId);
        response.setProductId(productId);
        response.setProductName(productName);
        response.setAuthor("남궁성");
        response.setPublisher("도우출판");
        response.setImagePath("/upload/book.jpg");
        response.setPrice(price);
        response.setQuantity(quantity);
        response.setStockQuantity(stockQuantity);
        return response;
    }

    private OrderCreateRequest createOrderCreateRequest(List<Long> cartItemIds) {
        OrderCreateRequest request = new OrderCreateRequest();
        request.setCartItemIds(cartItemIds);
        request.setReceiverName("공지훈");
        request.setReceiverPhone("010-1234-5678");
        request.setZipcode("12345");
        request.setAddress("서울시 강남구");
        request.setAddressDetail("101호");
        return request;
    }

}