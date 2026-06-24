package hello.bookshop.order.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderFormItemResponse {

    private Long cartItemId;

    private Long productId;

    private String productName;

    private String author;

    private String publisher;

    private String imagePath;

    private Integer price;

    private Integer quantity;

    private Integer stockQuantity;

    public Integer getItemTotalPrice() {
        return price * quantity;
    }
}
