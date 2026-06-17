package hello.bookshop.common.dto;

import hello.bookshop.product.type.ProductStatus;
import lombok.Getter;

@Getter
public class PageRequest {


    private final int page;

    private final int size;

    private final Long categoryId;

    private final ProductStatus status;

    private final String keyword;

    public PageRequest(Integer page, Integer size, Long categoryId, ProductStatus status, String keyword) {

        this.page = page == null || page < 1 ? 1 : page;
        this.size = size == null || size < 1 ? 10 : size;
        this.status = status;
        this.categoryId = categoryId;
        this.keyword = keyword;

    }

    public int getOffset() {
        return (page - 1) * size;
    }

}
