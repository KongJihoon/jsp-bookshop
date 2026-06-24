package hello.bookshop.cart.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CartItem {

    private Long cartItemId;

    private Long cartId;

    private Long productId;

    private Integer quantity;

    private LocalDateTime created_at;

    private LocalDateTime updated_at;

    @Builder
    private CartItem(Long cartId, Long productId, Integer quantity) {
        this.cartId = cartId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public static CartItem create(Long cartId, Long productId, Integer quantity) {
        return CartItem.builder()
                .cartId(cartId)
                .productId(productId)
                .quantity(quantity)
                .build();
    }

}
