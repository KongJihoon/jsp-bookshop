package hello.bookshop.cart.dto.response;

import hello.bookshop.cart.domain.CartItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
public class CartResponse {

    private final List<CartItemResponse> items;

    private final Integer totalPrice;

    public CartResponse(List<CartItemResponse> items) {
        this.items = items;

        this.totalPrice = items.stream()
                .mapToInt(CartItemResponse::getItemTotalPrice)
                .sum();
    }


}
