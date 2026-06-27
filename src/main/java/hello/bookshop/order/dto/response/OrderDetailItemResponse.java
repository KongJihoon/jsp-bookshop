package hello.bookshop.order.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDetailItemResponse {

    private Long productId;

    private String productName;

    private String imagePath;

    private Integer price;

    private Integer quantity;

    private Integer itemTotalPrice;

}
