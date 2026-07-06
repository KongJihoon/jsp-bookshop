package hello.bookshop.product.service;

import hello.bookshop.category.mapper.CategoryMapper;
import hello.bookshop.common.dto.PageRequest;
import hello.bookshop.common.exception.category.NotFoundCategoryException;
import hello.bookshop.common.exception.member.MemberNotFoundException;
import hello.bookshop.common.exception.product.FileUploadException;
import hello.bookshop.common.exception.product.AdminProductNotFoundException;
import hello.bookshop.file.FileStore;
import hello.bookshop.file.UploadFile;
import hello.bookshop.member.domain.Member;
import hello.bookshop.member.mapper.MemberMapper;
import hello.bookshop.product.domain.Product;
import hello.bookshop.product.domain.ProductImage;
import hello.bookshop.product.dto.request.ProductCreateRequest;
import hello.bookshop.product.dto.request.ProductUpdateRequest;
import hello.bookshop.product.dto.response.AdminProductListResponse;
import hello.bookshop.product.mapper.ProductMapper;
import hello.bookshop.product.type.ProductImageType;
import hello.bookshop.product.type.ProductStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminProductServiceTest {


    @Mock
    private MemberMapper memberMapper;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private FileStore fileStore;

    @InjectMocks
    private AdminProductService adminProductService;

    @Test
    @DisplayName("도서 등록 성공 - 상품, 대표 이미지, 상세 이미지 저장")
    void createProduct_success() throws Exception{
        // given
        Long adminId = 1L;

        Long categoryId = 10L;

        Member admin = createMember(adminId);

        MockMultipartFile thumbnail = new MockMultipartFile(
                "thumbnailImage",
                "thumbnail.jpg",
                "image/jpeg",
                "thumbnail".getBytes()
        );

        MockMultipartFile detail1 = new MockMultipartFile(
                "detailImages",
                "detail1.jpg",
                "image/jpeg",
                "detail1".getBytes()
        );

        MockMultipartFile detail2 = new MockMultipartFile(
                "detailImages",
                "detail2.jpg",
                "image/jpeg",
                "detail2".getBytes()
        );

        ProductCreateRequest request = new ProductCreateRequest();
        request.setCategoryId(categoryId);
        request.setName("자바의 정석");
        request.setAuthor("남궁성");
        request.setPublisher("도우출판");
        request.setPrice(30000);
        request.setStockQuantity(10);
        request.setDescription("자바 기본서");
        request.setThumbnailImage(thumbnail);
        request.setDetailImages(List.of(detail1, detail2));

        when(memberMapper.findMemberByIdAndWithdrawnAtIsNull(adminId))
                .thenReturn(Optional.of(admin));

        when(categoryMapper.existsByCategoryId(categoryId))
                .thenReturn(true);

        when(fileStore.storeFile(thumbnail))
                .thenReturn(new UploadFile(
                        "thumbnail.jpg",
                        "stored-thumbnail.jpg",
                        "/upload/stored-thumbnail.jpg"
                ));

        when(fileStore.storeFiles(request.getDetailImages()))
                .thenReturn(List.of(
                        new UploadFile(
                                "detail1.jpg",
                                "stored-detail1.jpg",
                                "/upload/stored-detail1.jpg"
                        ),
                        new UploadFile(
                                "detail2.jpg",
                                "stored-detail2.jpg",
                                "/upload/stored-detail2.jpg"
                        )
                ));

        doAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            setField(product, "productId", 100L);
            return null;
        }).when(productMapper).saveProduct(any(Product.class));

        // when

        adminProductService.createProduct(request, adminId);

        // then

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);

        verify(productMapper).saveProduct(captor.capture());

        Product savedProduct = captor.getValue();

        assertThat(savedProduct.getCategoryId()).isEqualTo(categoryId);
        assertThat(savedProduct.getName()).isEqualTo("자바의 정석");
        assertThat(savedProduct.getAuthor()).isEqualTo("남궁성");
        assertThat(savedProduct.getPublisher()).isEqualTo("도우출판");
        assertThat(savedProduct.getPrice()).isEqualTo(30000);
        assertThat(savedProduct.getStockQuantity()).isEqualTo(10);
        assertThat(savedProduct.getDescription()).isEqualTo("자바 기본서");
        assertThat(savedProduct.getCreatedBy()).isEqualTo(adminId);
        assertThat(savedProduct.getUpdatedBy()).isEqualTo(adminId);

        ArgumentCaptor<ProductImage> imageCaptor = ArgumentCaptor.forClass(ProductImage.class);

        verify(productMapper, times(3)).saveProductImage(imageCaptor.capture());

        List<ProductImage> images = imageCaptor.getAllValues();

        ProductImage thumbnailImage = images.get(0);

        assertThat(thumbnailImage.getProductId()).isEqualTo(100L);
        assertThat(thumbnailImage.getOriginalFileName()).isEqualTo("thumbnail.jpg");
        assertThat(thumbnailImage.getStoredFileName()).isEqualTo("stored-thumbnail.jpg");
        assertThat(thumbnailImage.getImagePath()).isEqualTo("/upload/stored-thumbnail.jpg");
        assertThat(thumbnailImage.getImageType().name()).isEqualTo("THUMBNAIL");
        assertThat(thumbnailImage.getSortOrder()).isEqualTo(0);

        ProductImage detailImage1 = images.get(1);
        assertThat(detailImage1.getImageType().name()).isEqualTo("DETAIL");
        assertThat(detailImage1.getSortOrder()).isEqualTo(1);

        ProductImage detailImage2 = images.get(2);
        assertThat(detailImage2.getImageType().name()).isEqualTo("DETAIL");
        assertThat(detailImage2.getSortOrder()).isEqualTo(2);

    }

    @Test
    @DisplayName("도서 등록 실패 - 관리자가 존재하지 않을 경우 예외 처리")
    void createProduct_fail_memberNotFound() {
        // given

        Long adminId = 1L;

        ProductCreateRequest request = new ProductCreateRequest();

        when(memberMapper.findMemberByIdAndWithdrawnAtIsNull(adminId))
                .thenReturn(Optional.empty());
        // when

        assertThatThrownBy(() -> adminProductService.createProduct(request, adminId))
                .isInstanceOf(MemberNotFoundException.class);

        // then

        verify(productMapper, never()).saveProduct(any(Product.class));
        verify(productMapper, never()).saveProductImage(any(ProductImage.class));

    }

    @Test
    @DisplayName("도서 등록 실패 - 존재하지 않는 카테고리 예외 처리")
    void createProduct_fail_notFoundCategory() throws Exception {
        // given

        Long adminId = 1L;

        Long categoryId = 10L;


        Member admin = createMember(adminId);

        ProductCreateRequest request = new ProductCreateRequest();

        request.setCategoryId(categoryId);

        when(memberMapper.findMemberByIdAndWithdrawnAtIsNull(adminId))
                .thenReturn(Optional.of(admin));

        when(categoryMapper.existsByCategoryId(categoryId))
                .thenReturn(false);
        // when

        assertThatThrownBy(() -> adminProductService.createProduct(request, adminId))
                .isInstanceOf(NotFoundCategoryException.class);


        // then

        verify(productMapper, never()).saveProduct(any(Product.class));
        verify(productMapper, never()).saveProductImage(any(ProductImage.class));

    }

    @Test
    @DisplayName("도서 등록 실패 - 대표 이미지 존재하지 않을 경우 예외 처리")
    void createProduct_fail_thumbnailNotFound() throws Exception {
        // given

        Long adminId = 1L;

        Long categoryId = 10L;


        Member member = createMember(adminId);

        ProductCreateRequest request = new ProductCreateRequest();

        request.setCategoryId(categoryId);
        request.setThumbnailImage(null);

        when(memberMapper.findMemberByIdAndWithdrawnAtIsNull(adminId))
                .thenReturn(Optional.of(member));

        when(categoryMapper.existsByCategoryId(categoryId))
                .thenReturn(true);

        // when

        assertThatThrownBy(() -> adminProductService.createProduct(request, adminId))
                .isInstanceOf(FileUploadException.class)
                .hasMessage("대표 이미지는 필수 입니다.");

        // then

        verify(productMapper, never()).saveProduct(any(Product.class));
        verify(productMapper, never()).saveProductImage(any(ProductImage.class));

    }

    @Test
    @DisplayName("도서 등록 실패 - 상세 이미지 5장 초과 시 예외 처리")
    void createProduct_fail_detailImagesLimit() throws Exception {
        // given

        Long adminId = 1L;

        Long categoryId = 10L;

        Member admin = createMember(adminId);

        MockMultipartFile thumbnail = new MockMultipartFile(
                "thumbnailImage",
                "thumbnail.jpg",
                "image/jpeg",
                "thumbnail".getBytes()
        );

        List<MockMultipartFile> detailImages = List.of(
                createImage("detail1.jpg"),
                createImage("detail2.jpg"),
                createImage("detail3.jpg"),
                createImage("detail4.jpg"),
                createImage("detail5.jpg"),
                createImage("detail6.jpg")
        );

        ProductCreateRequest request = new ProductCreateRequest();

        request.setCategoryId(categoryId);
        request.setThumbnailImage(thumbnail);
        request.setDetailImages((List)detailImages);

        when(memberMapper.findMemberByIdAndWithdrawnAtIsNull(adminId))
                .thenReturn(Optional.of(admin));

        when(categoryMapper.existsByCategoryId(categoryId))
                .thenReturn(true);
        // when

        assertThatThrownBy(() -> adminProductService.createProduct(request, adminId))
                .isInstanceOf(FileUploadException.class)
                .hasMessage("상세 이미지는 최대 5장까지 등록할 수 있습니다.");


        // then

        verify(productMapper, never()).saveProduct(any(Product.class));
        verify(productMapper, never()).saveProductImage(any(ProductImage.class));

    }

//    @Test
//    @DisplayName("관리자 도서 목록 조회 성공 테스트")
//    void findAdminProductList_success() {
//        // given
//        List<AdminProductListResponse> responses = List.of(
//                new AdminProductListResponse(
//                        1L,
//                        "자바의 정석",
//                        "남궁성",
//                        "도우출판",
//                        30000,
//                        10,
//                        "ACTIVE",
//                        "베스트셀러",
//                        LocalDateTime.now()
//                )
//        );
//
//        when(productMapper.findAllByAdminProductList(new PageRequest()))
//                .thenReturn(responses);
//        // when
//
//        List<AdminProductListResponse> result = adminProductService.findAdminProductList();
//
//        // then
//
//        assertThat(result).hasSize(1);
//        assertThat(result.get(0).getName()).isEqualTo("자바의 정석");
//
//        verify(productMapper).findAllByAdminProductList();
//
//    }


    @Test
    @DisplayName("도서 수정 성공 - 이미지 변경 없이 기본 정보 수정")
    void updateProduct_success_withoutImage() throws Exception {
        // given

        Long productId = 1L;

        Long adminId = 1L;

        Long categoryId = 10L;

        Member admin = createMember(adminId);
        Product product = createProduct(productId, categoryId, adminId);

        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setCategoryId(20L);
        request.setName("수정된 도서명");
        request.setAuthor("수정된 저자");
        request.setPublisher("수정된 출판사");
        request.setPrice(25000);
        request.setStockQuantity(5);
        request.setDescription("수정된 설명");
        request.setStatus(ProductStatus.SOLD_OUT);

        when(memberMapper.findMemberByIdAndWithdrawnAtIsNull(adminId))
                .thenReturn(Optional.of(admin));
        when(productMapper.findByProductIdAndDeletedAtIsNull(productId))
                .thenReturn(Optional.of(product));

        when(categoryMapper.existsByCategoryId(20L))
                .thenReturn(true);

        // when
        adminProductService.updateProduct(productId, adminId, request);


        // then
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);

        verify(productMapper).updateProduct(captor.capture());

        Product updatedProduct = captor.getValue();

        assertThat(updatedProduct.getProductId()).isEqualTo(productId);
        assertThat(updatedProduct.getCategoryId()).isEqualTo(20L);
        assertThat(updatedProduct.getName()).isEqualTo("수정된 도서명");
        assertThat(updatedProduct.getAuthor()).isEqualTo("수정된 저자");
        assertThat(updatedProduct.getPublisher()).isEqualTo("수정된 출판사");
        assertThat(updatedProduct.getPrice()).isEqualTo(25000);
        assertThat(updatedProduct.getStockQuantity()).isEqualTo(5);
        assertThat(updatedProduct.getDescription()).isEqualTo("수정된 설명");
        assertThat(updatedProduct.getStatus()).isEqualTo(ProductStatus.SOLD_OUT);
        assertThat(updatedProduct.getUpdatedBy()).isEqualTo(adminId);

        verify(productMapper, never()).deleteThumbnailImageByProductId(anyLong());
        verify(productMapper, never()).deleteProductImagesByProductIdAndType(anyLong(), any());
        verify(fileStore, never()).deleteFile(anyString());
        verify(productMapper, never()).saveProductImage(any(ProductImage.class));
    }

    @Test
    @DisplayName("도서 수정 성공 - 대표이미지와 상세 이미지 교체")
    void updateProduct_success_replaceImage() throws Exception{
        // given

        Long productId = 1L;
        Long adminId = 1L;
        Long categoryId = 10L;

        Member admin = createMember(adminId);
        Product product = createProduct(productId, categoryId, adminId);

        MockMultipartFile newThumbnailImage = createImage("new-thumbnail.jpg");

        MockMultipartFile newDetail1 =
                createImage("new-detail1.jpg");

        MockMultipartFile newDetail2 =
                createImage("new-detail2.jpg");

        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setCategoryId(categoryId);
        request.setName("수정 도서");
        request.setAuthor("수정 저자");
        request.setPublisher("수정 출판사");
        request.setPrice(30000);
        request.setStockQuantity(10);
        request.setDescription("수정 설명");
        request.setStatus(ProductStatus.ACTIVE);
        request.setThumbnailImage(newThumbnailImage);
        request.setDetailImages(List.of(newDetail1, newDetail2));

        ProductImage oldThumbnail = ProductImage.create(
                productId,
                "old-thumbnail.jpg",
                "stored-old-thumbnail.jpg",
                "/upload/stored-old-thumbnail.jpg",
                ProductImageType.THUMBNAIL,
                0
        );

        ProductImage oldDetail1 = ProductImage.create(
                productId,
                "old-detail1.jpg",
                "stored-old-detail1.jpg",
                "/upload/stored-old-detail1.jpg",
                ProductImageType.DETAIL,
                1
        );

        ProductImage oldDetail2 = ProductImage.create(
                productId,
                "old-detail2.jpg",
                "stored-old-detail2.jpg",
                "/upload/stored-old-detail2.jpg",
                ProductImageType.DETAIL,
                2
        );
        when(memberMapper.findMemberByIdAndWithdrawnAtIsNull(adminId))
                .thenReturn(Optional.of(admin));

        when(productMapper.findByProductIdAndDeletedAtIsNull(productId))
                .thenReturn(Optional.of(product));

        when(categoryMapper.existsByCategoryId(categoryId))
                .thenReturn(true);

        when(productMapper.findThumbnailImageByProductId(productId))
                .thenReturn(Optional.of(oldThumbnail));

        when(productMapper.findProductImagesByProductIdAndType(productId, ProductImageType.DETAIL))
                .thenReturn(List.of(oldDetail1, oldDetail2));

        when(fileStore.storeFile(newThumbnailImage))
                .thenReturn(new UploadFile(
                        "new-thumbnail.jpg",
                        "stored-new-thumbnail.jpg",
                        "/upload/stored-new-thumbnail.jpg"
                ));

        when(fileStore.storeFiles(request.getDetailImages()))
                .thenReturn(List.of(
                        new UploadFile(
                                "new-detail1.jpg",
                                "stored-new-detail1.jpg",
                                "/upload/stored-new-detail1.jpg"
                        ),
                        new UploadFile(
                                "new-detail2.jpg",
                                "stored-new-detail2.jpg",
                                "/upload/stored-new-detail2.jpg"
                        )
                ));

        // when

        adminProductService.updateProduct(productId, adminId, request);

        // then
        verify(productMapper).updateProduct(any(Product.class));
        verify(fileStore).deleteFile("stored-old-thumbnail.jpg");
        verify(fileStore).deleteFile("stored-old-detail1.jpg");
        verify(fileStore).deleteFile("stored-old-detail2.jpg");

        verify(productMapper).deleteThumbnailImageByProductId(productId);
        verify(productMapper).deleteProductImagesByProductIdAndType(
                productId,
                ProductImageType.DETAIL
        );

        ArgumentCaptor<ProductImage> imageCaptor = ArgumentCaptor.forClass(ProductImage.class);

        verify(productMapper, times(3))
                .saveProductImage(imageCaptor.capture());

        List<ProductImage> savedImages = imageCaptor.getAllValues();
        assertThat(savedImages.get(0).getImageType())
                .isEqualTo(ProductImageType.THUMBNAIL);
        assertThat(savedImages.get(0).getSortOrder()).isEqualTo(0);

        assertThat(savedImages.get(1).getImageType())
                .isEqualTo(ProductImageType.DETAIL);
        assertThat(savedImages.get(1).getSortOrder()).isEqualTo(1);

        assertThat(savedImages.get(2).getImageType())
                .isEqualTo(ProductImageType.DETAIL);
        assertThat(savedImages.get(2).getSortOrder()).isEqualTo(2);

    }

    @Test
    @DisplayName("도서 수정 실패 - 도서가 존재하지 않을 경우 예외 처리")
    void updateProduct_fail_productNotFound() throws Exception {
        // given

        Long productId = 1L;

        Long adminId = 1L;

        Member member = createMember(adminId);
        ProductUpdateRequest request = new ProductUpdateRequest();

        when(memberMapper.findMemberByIdAndWithdrawnAtIsNull(adminId))
                .thenReturn(Optional.of(member));

        when(productMapper.findByProductIdAndDeletedAtIsNull(productId))
                .thenReturn(Optional.empty());

        // when

        // then

        assertThatThrownBy(() -> adminProductService.updateProduct(productId, adminId, request))
                .isInstanceOf(AdminProductNotFoundException.class)
                .hasMessage("도서를 찾을 수 없습니다.");

        verify(productMapper, never()).updateProduct(any(Product.class));
        verify(productMapper, never()).saveProductImage(any(ProductImage.class));

    }

    @Test
    @DisplayName("도서 수정 실패 - 존재하지 않는 카테고리 예외처리")
    void updateProduct_fail_categoryNotFound() throws Exception {
        // given

        Long productId = 1L;
        Long categoryId = 99L;
        Long adminId = 1L;

        Member member = createMember(adminId);

        Product product = createProduct(productId, 10L, adminId);

        ProductUpdateRequest request = new ProductUpdateRequest();

        request.setCategoryId(categoryId);

        when(memberMapper.findMemberByIdAndWithdrawnAtIsNull(adminId))
                .thenReturn(Optional.of(member));

        when(productMapper.findByProductIdAndDeletedAtIsNull(productId))
                .thenReturn(Optional.of(product));

        when(categoryMapper.existsByCategoryId(categoryId))
                .thenReturn(false);

        // when

        // then

        assertThatThrownBy(() -> adminProductService.updateProduct(productId, adminId, request))
                .isInstanceOf(NotFoundCategoryException.class)
                .hasMessage("카테고리를 찾을 수 없습니다.");

        verify(productMapper, never()).updateProduct(product);

    }

    @Test
    @DisplayName("도서 삭제 성공 - 상품 상태를 DELETED로 변경")
    void deleteProduct_success() throws Exception {
        // given

        Long productId = 1L;

        Long adminId = 1L;

        Long categoryId = 10L;

        Member member = createMember(adminId);

        Product product = createProduct(productId, categoryId, adminId);

        when(memberMapper.findMemberByIdAndWithdrawnAtIsNull(adminId))
                .thenReturn(Optional.of(member));

        when(productMapper.findByProductIdAndDeletedAtIsNull(productId))
                .thenReturn(Optional.of(product));

        when(productMapper.deleteProduct(any(Product.class)))
                .thenReturn(1);

        // when

        adminProductService.deleteProduct(productId, adminId);

        // then

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);

        verify(productMapper).deleteProduct(captor.capture());

        Product deletedProduct = captor.getValue();

        assertThat(deletedProduct.getProductId()).isEqualTo(productId);

        assertThat(deletedProduct.getStatus()).isEqualTo(ProductStatus.DELETED);

        assertThat(deletedProduct.getUpdatedBy()).isEqualTo(adminId);

        verify(fileStore, never()).deleteFile(anyString());
        verify(productMapper, never()).deleteThumbnailImageByProductId(anyLong());
        verify(productMapper, never()).deleteProductImagesByProductIdAndType(anyLong(), any());
    }

    @Test
    @DisplayName("도서 삭제 실패 - 이미 삭제된 도서일 경우 예외 처리")
    void deleteProduct_fail_alreadyDeleted() throws Exception {
        // given

        Long productId = 1L;
        Long categoryId = 10L;
        Long adminId = 1L;

        Member member = createMember(adminId);
        Product product = createProduct(productId, categoryId, adminId);

        when(memberMapper.findMemberByIdAndWithdrawnAtIsNull(adminId))
                .thenReturn(Optional.of(member));

        when(productMapper.findByProductIdAndDeletedAtIsNull(productId))
                .thenReturn(Optional.of(product));

        when(productMapper.deleteProduct(any(Product.class)))
                .thenReturn(0);
        // when

        // then

        assertThatThrownBy(() -> adminProductService.deleteProduct(productId, adminId))
                .isInstanceOf(AdminProductNotFoundException.class)
                .hasMessage("이미 삭제되었거나 존재하지 않은 도서입니다.");
    }

    private Member createMember(Long memberId) throws Exception {
        Member member = new Member();

        setField(member,"memberId", memberId);

        return member;

    }

    private Product createProduct(Long productId, Long categoryId, Long createdBy) throws Exception{
        Product product = Product.create(
                categoryId,
                "testName",
                "testAuthor",
                "testPublisher",
                30000,
                10,
                "testDescription",
                createdBy
        );

        setField(product, "productId", productId);

        return product;
    }

    private MockMultipartFile createImage(String fileName) {
        return new MockMultipartFile(
                "detailImages",
                fileName,
                "image/jpeg",
                fileName.getBytes()
        );
    }



    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);

        field.setAccessible(true);
        field.set(target, value);
    }

}
