package hello.bookshop.home.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HomeProductResponse {


    private Long productId;

    private String name;

    private String author;

    private String publisher;

    private Integer price;

    private Integer stockQuantity;

    private String imagePath;


}
