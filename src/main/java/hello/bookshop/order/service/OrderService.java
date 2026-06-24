package hello.bookshop.order.service;

import hello.bookshop.common.exception.member.NotLoginMemberException;
import hello.bookshop.common.exception.order.OrderInfoException;
import hello.bookshop.common.exception.product.ProductNotFoundException;
import hello.bookshop.common.exception.product.StockQuantityExceedException;
import hello.bookshop.member.mapper.MemberMapper;
import hello.bookshop.order.domain.Order;
import hello.bookshop.order.domain.OrderItem;
import hello.bookshop.order.dto.request.OrderCreateRequest;
import hello.bookshop.order.dto.response.OrderCompleteResponse;
import hello.bookshop.order.dto.response.OrderFormItemResponse;
import hello.bookshop.order.dto.response.OrderFormResponse;
import hello.bookshop.order.mapper.OrderMapper;
import hello.bookshop.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final MemberMapper memberMapper;

    private final OrderMapper orderMapper;


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
    public OrderCompleteResponse createCartOrder(Long memberId, OrderCreateRequest request) {
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

            int stockUpdateCount = orderMapper.decreaseProductStock(item.getProductId(), item.getQuantity());

            if (stockUpdateCount == 0) {

                throw new StockQuantityExceedException("재고 수량을 초과한 상품이 있습니다.");
            }

            OrderItem orderItem = OrderItem.create(
                    order.getOrderId(),
                    item.getProductId(),
                    item.getProductName(),
                    item.getPrice(),
                    item.getQuantity()
            );

            orderMapper.saveOrderItem(orderItem);

        }

        int deletedCount = orderMapper.deleteOrderCartItems(memberId, request.getCartItemIds());

        if (deletedCount != request.getCartItemIds().size()) {
            throw new OrderInfoException("주문한 장바구니 상품 삭제에 실패했습니다.");
        }

        return new OrderCompleteResponse(order.getOrderId(), order.getTotalPrice());
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

}
