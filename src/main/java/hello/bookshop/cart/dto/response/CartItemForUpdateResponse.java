package hello.bookshop.cart.dto.response;

import hello.bookshop.product.type.ProductStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemForUpdateResponse {
    private Long cartItemId;

    private Long productId;

    private Integer price;

    private Integer stockQuantity;

    private ProductStatus status;

}
