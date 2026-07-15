package hello.bookshop.order.service;

import hello.bookshop.common.exception.member.NotLoginMemberException;
import hello.bookshop.common.exception.order.OrderInfoException;
import hello.bookshop.common.exception.product.ProductNotFoundException;
import hello.bookshop.common.exception.product.StockQuantityExceedException;
import hello.bookshop.member.mapper.MemberMapper;
import hello.bookshop.order.domain.Order;
import hello.bookshop.order.domain.OrderItem;
import hello.bookshop.order.dto.request.OrderCreateRequest;
import hello.bookshop.order.dto.response.*;
import hello.bookshop.order.mapper.OrderMapper;
import hello.bookshop.payment.domain.Payment;
import hello.bookshop.payment.dto.response.PaymentCheckoutResponse;
import hello.bookshop.payment.mapper.PaymentMapper;
import hello.bookshop.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final MemberMapper memberMapper;

    private final OrderMapper orderMapper;

    private final PaymentMapper paymentMapper;


    /**
     * 주문 생성 폼
     */
    @Transactional(readOnly = true)
    public OrderFormResponse getCartOrderForm(Long memberId, List<Long> cartItemIds) {

        validateLoginMember(memberId);

        validateCartItemIds(cartItemIds);

        List<OrderFormItemResponse> items = orderMapper.findOrderFormItemsByCartItemIds(memberId, cartItemIds);

        validateOrderFormItems(cartItemIds, items);

        validateStock(items);

        return new OrderFormResponse(items);
    }

    /**
     * 주문 생성 기능
     */
    @Transactional
    public PaymentCheckoutResponse createReadyCartOrder(Long memberId, OrderCreateRequest request) {

        validateLoginMember(memberId);

        OrderFormResponse orderForm = getCartOrderForm(memberId, request.getCartItemIds());

        Order order = Order.create(
                memberId,
                request.getReceiverName(),
                request.getReceiverPhone(),
                request.getZipcode(),
                request.getAddress(),
                request.getAddressDetail(),
                orderForm.getTotalPrice()
        );

        orderMapper.saveOrder(order);

        for (OrderFormItemResponse item : orderForm.getItems()) {

            OrderItem orderItem = OrderItem.create(
                    order.getOrderId(),
                    item.getProductId(),
                    item.getCartItemId(),
                    item.getProductName(),
                    item.getPrice(),
                    item.getQuantity()
            );

            orderMapper.saveOrderItem(orderItem);
        }

        String tossOrderId = createTossOrderId(order.getOrderId());

        Payment payment = Payment.ready(
                order.getOrderId(),
                tossOrderId,
                order.getTotalPrice()
        );

        paymentMapper.save(payment);


        return new PaymentCheckoutResponse(
                order.getOrderId(),
                tossOrderId,
                order.getTotalPrice(),
                createOrderName(orderForm.getItems()),
                "BookShop 회원",
                "member-" + memberId
        );

    }

    private String createTossOrderId(Long orderId) {
        String uniqueSuffix = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        return "BOOKSHOP-" + orderId + "-" + uniqueSuffix;
    }



    /**
     * 주문 내역 조회
     */
    @Transactional(readOnly = true)
    public List<OrderListResponse> findMyOrder(Long memberId) {
        validateLoginMember(memberId);

        return orderMapper.findOrdersByMemberId(memberId);
    }

    /**
     * 주문 내역 상세 조회
     */
    @Transactional(readOnly = true)
    public OrderDetailResponse findMyOrderDetail(Long memberId, Long orderId) {
        OrderDetailResponse orderDetail = orderMapper.findOrderDetailByOrderId(memberId, orderId);

        if (orderDetail == null) {
            throw new OrderInfoException("주문 내역을 찾을 수 없습니다.");
        }

        List<OrderDetailItemResponse> items = orderMapper.findOrderDetailItemsByOrderId(orderId);

        orderDetail.setItems(items);

        return orderDetail;
    }

    /**
     * 배송 목록 조회
     */
    @Transactional(readOnly = true)
    public List<DeliveryListResponse> findMyDeliveries(Long memberId) {

        validateLoginMember(memberId);

        return orderMapper.findDeliveryOrdersByMemberId(memberId);

    }

    private static void validateStock(List<OrderFormItemResponse> items) {
        for (OrderFormItemResponse item : items) {
            if (item.getStockQuantity() < item.getQuantity()) {
                throw new StockQuantityExceedException("재고 수량을 초과한 상품이 있습니다.");
            }
        }
    }

    private static void validateOrderFormItems(List<Long> cartItemIds, List<OrderFormItemResponse> items) {
        if (items.isEmpty()) {
            throw new ProductNotFoundException("주문할 상품을 찾을 수 없습니다.");
        }

        if (items.size() != cartItemIds.size()) {
            throw new ProductNotFoundException("주문할 수 없는 상품이 포함되어 있습니다.");
        }
    }

    private static void validateCartItemIds(List<Long> cartItemIds) {
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            throw new OrderInfoException("주문할 상품을 선택해주세요.");
        }
    }

    private void validateLoginMember(Long memberId) {
        memberMapper.findMemberByIdAndWithdrawnAtIsNull(memberId)
                .orElseThrow(() -> new NotLoginMemberException("로그인 후 사용 가능합니다."));
    }

    private String createOrderName(List<OrderFormItemResponse> items) {
        if (items.size() == 1) {
            return items.get(0).getProductName();
        }

        return items.get(0).getProductName() + "외" + (items.size() - 1) + "건";
    }

}
