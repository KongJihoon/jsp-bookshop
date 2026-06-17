package hello.bookshop.product.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserProductListResponse {

    private Long productId;

    private Long categoryId;

    private String categoryName;

    private String name;

    private String author;

    private String publisher;

    private Integer price;

    private Integer stockQuantity;

    private String imagePath;

}
