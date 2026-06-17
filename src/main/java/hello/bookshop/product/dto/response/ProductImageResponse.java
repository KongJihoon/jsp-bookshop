package hello.bookshop.product.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductImageResponse {

    private Long productImageId;

    private String imagePath;

    private String imageType;

    private Integer sortOrder;

}
