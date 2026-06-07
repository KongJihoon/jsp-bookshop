package hello.bookshop.product.mapper;


import hello.bookshop.product.domain.Product;
import hello.bookshop.product.domain.ProductImage;
import hello.bookshop.product.dto.response.AdminProductListResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductMapper {

    void saveProduct(Product product);

    void saveProductImage(ProductImage productImage);

    List<AdminProductListResponse> findAllByAdminProductList();


}
