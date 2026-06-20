package hello.bookshop.product.dto.response;

import hello.bookshop.product.type.ProductStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserProductDetailResponse {

    private Long productId;

    private Long categoryId;

    private String categoryName;

    private String name;

    private String author;

    private String publisher;

    private Integer price;

    private Integer stockQuantity;

    private String description;

    private ProductStatus status;

    private List<ProductImageResponse> images;

}
