package hello.bookshop.product.dto.request;

import hello.bookshop.product.type.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class ProductUpdateRequest {

    @NotNull(message = "카테고리는 필수입니다.")
    private Long categoryId;

    @NotBlank(message = "도서명은 필수입니다.")
    private String name;

    @NotBlank(message = "저자는 필수입니다.")
    private String author;

    @NotBlank(message = "출판사는 필수입니다.")
    private String publisher;

    @NotNull(message = "가격은 필수입니다.")
    private Integer price;

    @NotNull(message = "재고는 필수입니다.")
    private Integer stockQuantity;

    @NotBlank(message = "도서 설명은 필수입니다.")
    private String description;

    @NotNull(message = "판매 상태는 필수입니다.")
    private ProductStatus status;

    private MultipartFile thumbnailImage;

    private List<MultipartFile> detailImages;

}
