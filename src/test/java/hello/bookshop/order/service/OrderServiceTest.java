package hello.bookshop.order.service;

import hello.bookshop.common.exception.order.OrderInfoException;
import hello.bookshop.common.exception.product.ProductNotFoundException;
import hello.bookshop.common.exception.product.StockQuantityExceedException;
import hello.bookshop.member.domain.Member;
import hello.bookshop.member.mapper.MemberMapper;
import hello.bookshop.order.domain.Order;
import hello.bookshop.order.domain.OrderItem;
import hello.bookshop.order.dto.request.OrderCreateRequest;
import hello.bookshop.order.dto.response.*;
import hello.bookshop.order.mapper.OrderMapper;
import hello.bookshop.order.type.OrderStatus;
import hello.bookshop.payment.domain.Payment;
import hello.bookshop.payment.dto.response.PaymentCheckoutResponse;
import hello.bookshop.payment.mapper.PaymentMapper;
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

import java.time.LocalDateTime;
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

    @Mock
    private PaymentMapper paymentMapper;

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

        // when
        PaymentCheckoutResponse result = orderService.createReadyCartOrder(memberId, request);

        // then

        assertThat(result.getOrderId()).isEqualTo(1000L);
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

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);

        verify(paymentMapper).save(paymentCaptor.capture());

        Payment savedPayment = paymentCaptor.getValue();

        assertThat(savedPayment.getOrderId()).isEqualTo(1000L);
        assertThat(savedPayment.getTossOrderId()).startsWith("BOOKSHOP-1000-");
        assertThat(savedPayment.getAmount()).isEqualTo(80000);
        assertThat(result.getTossOrderId()).isEqualTo(savedPayment.getTossOrderId());
        assertThat(result.getAmount()).isEqualTo(80000);

    }

    @Test
    @DisplayName("재고 부족 시 주문 생성 불가")
    void createCartOrder_fail_stockDecreaseFail() {
        // given
        Long memberId = 1L;
        List<Long> cartItemIds = List.of(10L);

        givenLoginMember(memberId);

        OrderCreateRequest request = createOrderCreateRequest(cartItemIds);

        List<OrderFormItemResponse> items = List.of(
                createOrderFormItem(10L, 100L, "자바의 정석", 30000, 2, 1)
        );

        when(orderMapper.findOrderFormItemsByCartItemIds(memberId, cartItemIds))
                .thenReturn(items);

        // when

        // then

        assertThatThrownBy(() -> orderService.createReadyCartOrder(memberId, request))
                .isInstanceOf(StockQuantityExceedException.class)
                .hasMessage("재고 수량을 초과한 상품이 있습니다.");

        verify(orderMapper, never()).saveOrder(any(Order.class));
        verify(orderMapper, never()).saveOrderItem(any(OrderItem.class));
        verify(paymentMapper, never()).save(any(Payment.class));

    }

    @Test
    @DisplayName("회원 주문 목록 조회")
    void findMyOrder_success() {
        // given

        Long memberId = 1L;


        OrderListResponse firstOrder = new OrderListResponse();
        firstOrder.setOrderId(10L);
        firstOrder.setOrderStatus(OrderStatus.READY);
        firstOrder.setTotalPrice(50000);
        firstOrder.setOrderedAt(LocalDateTime.now());
        firstOrder.setRepresentativeProductName("자바의 정석");
        firstOrder.setTotalItemCount(1);

        OrderListResponse secondOrder = new OrderListResponse();
        secondOrder.setOrderId(20L);
        secondOrder.setOrderStatus(OrderStatus.READY);
        secondOrder.setTotalPrice(80000);
        secondOrder.setOrderedAt(LocalDateTime.now());
        secondOrder.setRepresentativeProductName("스프링 입문");
        secondOrder.setTotalItemCount(2);

        givenLoginMember(memberId);

        when(orderMapper.findOrdersByMemberId(memberId))
                .thenReturn(List.of(firstOrder, secondOrder));

        // when

        List<OrderListResponse> result = orderService.findMyOrder(memberId);

        // then

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDisplayProductName()).isEqualTo("자바의 정석");
        assertThat(result.get(1).getDisplayProductName()).isEqualTo("스프링 입문 외 1권");

        verify(memberMapper).findMemberByIdAndWithdrawnAtIsNull(memberId);
        verify(orderMapper).findOrdersByMemberId(memberId);

    }

    @Test
    @DisplayName("주문 내역 상세 조회")
    void findMyOrderDetail_success() {
        // given

        Long memberId = 1L;

        Long orderId = 10L;

        OrderDetailResponse orderDetail = new OrderDetailResponse();

        orderDetail.setOrderId(orderId);
        orderDetail.setOrderStatus(OrderStatus.READY);
        orderDetail.setTotalPrice(80000);
        orderDetail.setOrderedAt(LocalDateTime.now());
        orderDetail.setReceiverName("테스트 수령자");
        orderDetail.setReceiverPhone("010-1234-5678");
        orderDetail.setZipcode("23453");
        orderDetail.setAddress("테스트 주소");
        orderDetail.setAddressDetail("테스트 상세 주소");

        OrderDetailItemResponse firstOrder = createOrderDetailItem(10L, "자바의 정석", 30000, 2);

        OrderDetailItemResponse secondOrder = createOrderDetailItem(20L, "스프링 입문", 20000, 1);

        when(orderMapper.findOrderDetailByOrderId(memberId, orderId))
                .thenReturn(orderDetail);

        when(orderMapper.findOrderDetailItemsByOrderId(orderId))
                .thenReturn(List.of(firstOrder, secondOrder));


        // when

        OrderDetailResponse result = orderService.findMyOrderDetail(memberId, orderId);

        // then

        assertThat(result.getOrderId()).isEqualTo(orderId);
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.READY);
        assertThat(result.getItems()).hasSize(2);

        assertThat(result.getItems().get(0).getProductName()).isEqualTo("자바의 정석");

        assertThat(result.getItems().get(0).getItemTotalPrice()).isEqualTo(60000);

        verify(orderMapper).findOrderDetailByOrderId(memberId, orderId);
        verify(orderMapper).findOrderDetailItemsByOrderId(orderId);


    }

    @Test
    @DisplayName("존재하지 않는 주문 예외 발생")
    void findMyOrderDetail_fail_orderNotFound() {
        // given
        Long memberId = 1L;

        Long orderId = 100L;

        when(orderMapper.findOrderDetailByOrderId(memberId, orderId))
                .thenReturn(null);

        // when

        // then

        assertThatThrownBy(() -> orderService.findMyOrderDetail(memberId, orderId))
                .isInstanceOf(OrderInfoException.class)
                .hasMessage("주문 내역을 찾을 수 없습니다.");

        verify(orderMapper).findOrderDetailByOrderId(memberId, orderId);
        verify(orderMapper, never()).findOrderDetailItemsByOrderId(orderId);
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

    private OrderDetailItemResponse createOrderDetailItem(Long productId, String productName, Integer price, Integer quantity) {

        OrderDetailItemResponse items = new OrderDetailItemResponse();
        items.setProductId(productId);
        items.setProductName(productName);
        items.setImagePath("/upload/" + productName + ".jpg");
        items.setPrice(price);
        items.setQuantity(quantity);
        items.setItemTotalPrice(price * quantity);


        return items;
    }



}
