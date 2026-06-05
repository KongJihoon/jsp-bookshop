package hello.bookshop.product.domain;


import hello.bookshop.product.type.ProductImageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage {

    private Long productImageId;

    private Long productId;

    private String originalFileName;

    private String storedFileName;

    private String imagePath;

    private ProductImageType imageType;

    private Integer sortOrder;

    private LocalDateTime createdAt;

    @Builder
    private ProductImage (Long productId, String originalFileName, String storedFileName
    , String imagePath, ProductImageType imageType, Integer sortOrder) {

        this.productId = productId;
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.imagePath = imagePath;
        this.imageType = imageType;
        this.sortOrder = sortOrder;

    }

    public static ProductImage create(
            Long productId,
            String originalFileName,
            String storedFileName,
            String imagePath,
            ProductImageType imageType,
            Integer sortOrder
    ) {

        return ProductImage.builder()
                .productId(productId)
                .originalFileName(originalFileName)
                .storedFileName(storedFileName)
                .imagePath(imagePath)
                .imageType(imageType)
                .sortOrder(sortOrder)
                .build();
    }



}
