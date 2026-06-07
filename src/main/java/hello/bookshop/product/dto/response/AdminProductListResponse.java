package hello.bookshop.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class AdminProductListResponse {

    private Long productId;

    private String name;

    private String author;

    private String publisher;

    private int price;

    private int stockQuantity;

    private String status;

    private String thumbnailPath;

    private String categoryName;

    private LocalDateTime createdAt;

}
