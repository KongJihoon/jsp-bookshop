package hello.bookshop.product.service;

import hello.bookshop.common.dto.PageRequest;
import hello.bookshop.common.dto.PageResponse;
import hello.bookshop.common.exception.product.ProductNotFoundException;
import hello.bookshop.product.domain.Product;
import hello.bookshop.product.dto.response.ProductImageResponse;
import hello.bookshop.product.dto.response.UserProductDetailResponse;
import hello.bookshop.product.dto.response.UserProductListResponse;
import hello.bookshop.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public PageResponse<UserProductListResponse> findUserProductList(Integer page, Long categoryId) {


        PageRequest pageRequest = new PageRequest(page, 8, categoryId, null, null);

        List<UserProductListResponse> products = productMapper.findUserProductList(pageRequest);

        int totalCount = productMapper.countUserProductList(pageRequest);

        return new PageResponse<>(
                products,
                pageRequest.getPage(),
                pageRequest.getSize(),
                totalCount
        );

    }

    @Transactional(readOnly = true)
    public UserProductDetailResponse findUserProductDetail(Long productId) {

        UserProductDetailResponse product = productMapper.findUserProductDetail(productId)
                .orElseThrow(() -> new ProductNotFoundException("상품을 찾을 수 없습니다."));

        List<ProductImageResponse> images = productMapper.findProductImagesByProductId(product.getProductId());


        product.setImages(images);

        return product;
    }

}
