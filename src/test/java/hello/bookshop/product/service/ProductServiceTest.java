package hello.bookshop.product.service;

import hello.bookshop.common.dto.PageRequest;
import hello.bookshop.common.dto.PageResponse;
import hello.bookshop.common.exception.product.ProductNotFoundException;
import hello.bookshop.product.domain.Product;
import hello.bookshop.product.dto.response.ProductImageResponse;
import hello.bookshop.product.dto.response.UserProductDetailResponse;
import hello.bookshop.product.dto.response.UserProductListResponse;
import hello.bookshop.product.mapper.ProductMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("사용자 상품 상세 조회 성공 - 상품 정보와 이미지 목록 반환")
    void findUserProductDetail_success() {
        // given
        Long productId = 1L;

        UserProductDetailResponse product = createUserProductDetailResponse(productId);

        ProductImageResponse thumbnail = createProductImageResponse(
                1L,
                "/upload/thumbnail.jpg",
                "THUMBNAIL",
                0
        );
        ProductImageResponse detail =
                createProductImageResponse(
                        2L,
                        "/upload/detail.jpg",
                        "DETAIL",
                        1
                );

        when(productMapper.findUserProductDetail(productId))
                .thenReturn(Optional.of(product));

        when(productMapper.findProductImagesByProductId(productId))
                .thenReturn(List.of(thumbnail, detail));


        // when

        UserProductDetailResponse result = productService.findUserProductDetail(productId);

        // then

        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getName()).isEqualTo("자바의 정석");
        assertThat(result.getCategoryName()).isEqualTo("IT·컴퓨터");
        assertThat(result.getPrice()).isEqualTo(30000);

        assertThat(result.getImages()).hasSize(2);

        assertThat(result.getImages().get(0).getImageType()).isEqualTo("THUMBNAIL");

        assertThat(result.getImages().get(1).getImageType()).isEqualTo("DETAIL");

        verify(productMapper).findUserProductDetail(productId);
        verify(productMapper).findProductImagesByProductId(productId);
    }

    @Test
    @DisplayName("사용자 상품 상세 조회 실패 - 상품이 없을 시 예외 발생")
    void findUserProductDetail_fail_productNotFound() {
        // given

        Long productId = 999L;

        when(productMapper.findUserProductDetail(productId))
                .thenReturn(Optional.empty());

        // when

        // then

        assertThatThrownBy(() -> productService.findUserProductDetail(productId))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("상품을 찾을 수 없습니다.");

        verify(productMapper).findUserProductDetail(productId);
        verify(productMapper, never()).findProductImagesByProductId(productId);

    }

    @Test
    @DisplayName("사용자 상품 목록 조회 성공 - 페이징 정보와 상품 목록 반환")
    void findUserProductList_success() {
        // given

        Integer page = 1;

        Long categoryId = 10L;

        UserProductListResponse product = createUserProductList();

        when(productMapper.findUserProductList(any(PageRequest.class)))
                .thenReturn(List.of(product));

        when(productMapper.countUserProductList(any(PageRequest.class)))
                .thenReturn(1);

        // when

        PageResponse<UserProductListResponse> result = productService.findUserProductList(page, categoryId);

        // then

        assertThat(result.getPage()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(8);
        assertThat(result.getTotalCount()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.isHasPrevious()).isFalse();
        assertThat(result.isHasNext()).isFalse();

        ArgumentCaptor<PageRequest> captor = ArgumentCaptor.forClass(PageRequest.class);

        verify(productMapper).findUserProductList(captor.capture());
        verify(productMapper).countUserProductList(any(PageRequest.class));

        PageRequest pageRequest = captor.getValue();

        assertThat(pageRequest.getPage()).isEqualTo(1);
        assertThat(pageRequest.getSize()).isEqualTo(8);
        assertThat(pageRequest.getCategoryId()).isEqualTo(categoryId);
    }



    private UserProductDetailResponse createUserProductDetailResponse(Long productId) {

        UserProductDetailResponse response = new UserProductDetailResponse();

        response.setProductId(productId);
        response.setCategoryId(10L);
        response.setCategoryName("IT·컴퓨터");
        response.setName("자바의 정석");
        response.setAuthor("남궁성");
        response.setPublisher("도우출판");
        response.setPrice(30000);
        response.setStockQuantity(10);
        response.setDescription("자바 기본서");

        return response;
    }

    private UserProductListResponse createUserProductList() {

        UserProductListResponse response =
                new UserProductListResponse();

        response.setProductId(1L);
        response.setCategoryId(10L);
        response.setCategoryName("IT·컴퓨터");
        response.setName("자바의 정석");
        response.setAuthor("남궁성");
        response.setPublisher("도우출판");
        response.setPrice(30000);
        response.setStockQuantity(10);
        response.setImagePath("/upload/thumbnail.jpg");

        return response;

    }

    private ProductImageResponse createProductImageResponse(
            Long productImageId,
            String imagePath,
            String imageType,
            Integer sortOrder
    ) {
        ProductImageResponse response =
                new ProductImageResponse();

        response.setProductImageId(productImageId);
        response.setImagePath(imagePath);
        response.setImageType(imageType);
        response.setSortOrder(sortOrder);

        return response;
    }


}