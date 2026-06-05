package hello.bookshop.product.domain;


import hello.bookshop.product.type.ProductStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class Product {

    private Long productId;

    private Long categoryId;

    private String name;

    private String author;

    private String publisher;

    private Integer price;

    private Integer stockQuantity;

    private String description;

    private ProductStatus status;

    private Long createdBy;

    private Long updatedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    @Builder
    private Product(Long categoryId, String name, String author, String publisher, Integer price, Integer stockQuantity
                    , String description, ProductStatus status, Long createdBy
    ) {

        this.categoryId = categoryId;
        this.name = name;
        this.author = author;
        this.publisher = publisher;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.description = description;
        this.status = status;
        this.createdBy = createdBy;
        this.updatedBy = createdBy;
    }

    public static Product create(Long categoryId, String name, String author, String publisher, Integer price, Integer stockQuantity
            , String description, Long createdBy) {
        return Product.builder()
                .categoryId(categoryId)
                .name(name)
                .author(author)
                .publisher(publisher)
                .price(price)
                .stockQuantity(stockQuantity)
                .description(description)
                .status(ProductStatus.ACTIVE)
                .createdBy(createdBy)
                .build();
    }

}
