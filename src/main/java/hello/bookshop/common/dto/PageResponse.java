package hello.bookshop.common.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PageResponse<T> {

    private final List<T> content;

    private final int page;

    private final int size;

    private final int totalCount;

    private final int totalPages;

    private final boolean hasPrevious;

    private final boolean hasNext;

    public PageResponse(List<T> content, int page, int size, int totalCount) {

        this.content = content;

        this.page = page;

        this.size = size;

        this.totalCount = totalCount;

        this.totalPages = (int) Math.ceil((double) totalCount / size);

        this.hasPrevious = page > 1;

        this.hasNext = page < totalPages;

    }
}
