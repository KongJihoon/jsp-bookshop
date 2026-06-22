package hello.bookshop.cart.dto.response;

import lombok.Getter;

@Getter
public class CartQuantityUpdateResponse {

    private final Long cartItemId;

    private final Integer quantity;

    private final Integer itemTotalPrice;



    public CartQuantityUpdateResponse(
            Long cartItemId,
            Integer quantity,
            Integer price
    ) {
        this.cartItemId = cartItemId;
        this.quantity = quantity;
        this.itemTotalPrice = quantity * price;
    }
}
