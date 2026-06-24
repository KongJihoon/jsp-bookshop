package hello.bookshop.cart.dto.response;

import lombok.Getter;

@Getter
public class CartItemDeleteResponse {

    private final Long cartItemId;

    private final String message;


    public CartItemDeleteResponse(Long cartItemId, String message) {
        this.cartItemId = cartItemId;
        this.message = message;
    }
}
