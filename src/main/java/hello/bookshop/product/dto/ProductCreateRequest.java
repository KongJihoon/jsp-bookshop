package hello.bookshop.product.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateRequest {

    @NotNull(message = "카테고리를 선택해주세요.")
    private Long categoryId;

    @NotBlank(message = "도서명을 입력해주세요.")
    private String name;

    @NotBlank(message = "저자를 입력해주세요.")
    private String author;

    @NotBlank(message = "출판사를 입력해주세요.")
    private String publisher;

    @NotNull(message = "가격을 입력해주세요.")
    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private Integer price;


    @NotNull(message = "재고를 입력해주세요.")
    @Min(value = 0, message = "재고는 0권 이상이어야 합니다.")
    private Integer stockQuantity;

    @NotBlank(message = "설명을 입력해주세요.")
    private String description;

    /** 대표 이미지 */
    private MultipartFile thumbnailImage;

    /** 상세 이미지 */
    private List<MultipartFile> detailImages;


}
