package hello.bookshop.product.mapper;

import hello.bookshop.product.dto.response.AdminProductListResponse;
import hello.bookshop.product.type.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProductMapperTest {
    @Autowired
    private ProductMapper productMapper;

    @Test
    @DisplayName("관리자 도서 목록 조회 성공 - 도서와 썸네일 함께 조회")
    void findAdminProductList_success() {
        // given

        List<AdminProductListResponse> products = productMapper.findAllByAdminProductList();

        // when

        // then

        assertThat(products).isNotEmpty();

        AdminProductListResponse product = products.get(0);

        assertThat(product.getProductId()).isNotNull();
        assertThat(product.getName()).isNotBlank();
        assertThat(product.getAuthor()).isNotBlank();
        assertThat(product.getPublisher()).isNotBlank();
        assertThat(product.getPrice()).isNotNull();
        assertThat(product.getStockQuantity()).isNotNull();
        assertThat(product.getStatus()).isEqualTo("ACTIVE");
        assertThat(product.getThumbnailPath()).isNotBlank();
    }


}
