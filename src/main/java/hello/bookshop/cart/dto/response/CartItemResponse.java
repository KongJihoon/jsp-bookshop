package hello.bookshop.cart.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemResponse {


    private Long cartItemId;

    private Long productId;

    private String productName;

    private String author;

    private String publisher;

    private Integer price;

    private Integer quantity;

    private String imagePath;

    public Integer getItemTotalPrice() {
        return price * quantity;
    }

}
