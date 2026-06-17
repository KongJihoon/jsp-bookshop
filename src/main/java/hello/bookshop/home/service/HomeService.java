package hello.bookshop.home.service;

import hello.bookshop.home.dto.response.HomeProductResponse;
import hello.bookshop.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeService {
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public List<HomeProductResponse> findLatestProducts() {
        return productMapper.findLatestProduct();
    }
}
