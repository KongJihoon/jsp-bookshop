package hello.bookshop.product.mapper;


import hello.bookshop.common.dto.PageRequest;
import hello.bookshop.product.domain.Product;
import hello.bookshop.product.domain.ProductImage;
import hello.bookshop.product.dto.response.AdminProductListResponse;
import hello.bookshop.product.type.ProductStatus;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductMapper {

    void saveProduct(Product product);

    void saveProductImage(ProductImage productImage);

    List<AdminProductListResponse> findAllByAdminProductList(PageRequest pageRequest);

    int countAdminProductList(PageRequest pageRequest);


}
