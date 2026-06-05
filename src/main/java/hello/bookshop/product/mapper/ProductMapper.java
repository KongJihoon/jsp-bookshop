package hello.bookshop.product.mapper;


import hello.bookshop.product.domain.Product;
import hello.bookshop.product.domain.ProductImage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper {

    void saveProduct(Product product);

    void saveProductImage(ProductImage productImage);


}
