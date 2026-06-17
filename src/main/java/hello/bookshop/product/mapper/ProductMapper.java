package hello.bookshop.product.mapper;


import hello.bookshop.common.dto.PageRequest;
import hello.bookshop.home.dto.response.HomeProductResponse;
import hello.bookshop.product.domain.Product;
import hello.bookshop.product.domain.ProductImage;
import hello.bookshop.product.dto.response.*;
import hello.bookshop.product.type.ProductImageType;
import hello.bookshop.product.type.ProductStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ProductMapper {

    void saveProduct(Product product);

    void saveProductImage(ProductImage productImage);

    List<AdminProductListResponse> findAllByAdminProductList(PageRequest pageRequest);

    int countAdminProductList(PageRequest pageRequest);


    Optional<AdminProductDetailResponse> findAdminProductDetail(Long productId);

    List<ProductImageResponse> findProductImagesByProductId(Long productId);



    Optional<Product> findByProductIdAndDeletedAtIsNull(Long productId);

    void updateProduct(Product product);

    Optional<ProductImage> findThumbnailImageByProductId(Long productId);

    List<ProductImage> findProductImagesByProductIdAndType(@Param("productId") Long productId, @Param("imageType") ProductImageType imageType);

    void deleteThumbnailImageByProductId(Long productId);

    void deleteProductImagesByProductIdAndType(@Param("productId") Long productId, @Param("imageType") ProductImageType imageType);


    long countAllProducts();

    long countSoldOutProducts();

    int deleteProduct(Product product);

    List<HomeProductResponse> findLatestProduct();

    List<UserProductListResponse> findUserProductList(PageRequest pageRequest);

    int countUserProductList(PageRequest pageRequest);

    Optional<UserProductDetailResponse> findUserProductDetail(Long productId);
}
