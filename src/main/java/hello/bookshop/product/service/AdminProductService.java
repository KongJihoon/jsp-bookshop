package hello.bookshop.product.service;

import hello.bookshop.category.mapper.CategoryMapper;
import hello.bookshop.common.dto.PageRequest;
import hello.bookshop.common.dto.PageResponse;
import hello.bookshop.common.exception.category.NotFoundCategoryException;
import hello.bookshop.common.exception.member.MemberNotFoundException;
import hello.bookshop.common.exception.product.FileUploadException;
import hello.bookshop.common.exception.product.ProductNotFoundException;
import hello.bookshop.file.FileStore;
import hello.bookshop.file.UploadFile;
import hello.bookshop.member.domain.Member;
import hello.bookshop.member.mapper.MemberMapper;
import hello.bookshop.product.domain.Product;
import hello.bookshop.product.domain.ProductImage;
import hello.bookshop.product.dto.request.ProductCreateRequest;
import hello.bookshop.product.dto.response.AdminProductDetailResponse;
import hello.bookshop.product.dto.response.AdminProductListResponse;
import hello.bookshop.product.dto.response.ProductImageResponse;
import hello.bookshop.product.mapper.ProductMapper;
import hello.bookshop.product.type.ProductImageType;
import hello.bookshop.product.type.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminProductService {

    private final MemberMapper memberMapper;

    private final CategoryMapper categoryMapper;

    private final ProductMapper productMapper;

    private final FileStore fileStore;


    @Transactional
    public void createProduct(ProductCreateRequest request, Long createdBy) {

        // 1. 회원 및 카테고리 검증
        Member member = memberMapper.findMemberByIdAndWithdrawnAtIsNull(createdBy)
                .orElseThrow(MemberNotFoundException::new);


        boolean exists = categoryMapper.existsByCategoryId(request.getCategoryId());

        if (!exists) {
            throw new NotFoundCategoryException("카테고리를 찾을 수 없습니다.");
        }

        // 2. 이미지 등록 검증
        validateProductImages(request);

        // 3. Product 도메인 생성 및 DB 저장
        Product product = Product.create(request.getCategoryId(), request.getName(), request.getAuthor(), request.getPublisher(), request.getPrice(),
                request.getStockQuantity(), request.getDescription(), member.getMemberId());


        productMapper.saveProduct(product);

        saveThumbnailImage(product.getProductId(), request);

        saveDetailImages(product.getProductId(), request);

    }

    /**
     * 도서 목록 조회 기능
     */
    public PageResponse<AdminProductListResponse> findAdminProductList(Integer page,
                                                                       Long categoryId,
                                                                       ProductStatus status,
                                                                       String keyword) {


        PageRequest pageRequest = new PageRequest(page, 10, categoryId, status,  keyword);

        List<AdminProductListResponse> products = productMapper.findAllByAdminProductList(pageRequest);

        int totalCount = productMapper.countAdminProductList(pageRequest);

        return new PageResponse<>(
                products,
                pageRequest.getPage(),
                pageRequest.getSize(),
                totalCount
        );

    }
    /**
     * 도서 상세 조회 기능
     */
    @Transactional(readOnly = true)
    public AdminProductDetailResponse getAdminProductDetail(Long productId) {

        AdminProductDetailResponse product = productMapper.findAdminProductDetail(productId)
                .orElseThrow(() -> new ProductNotFoundException("도서를 찾을 수 없습니다."));

        List<ProductImageResponse> images = productMapper.findProductImagesByProductId(product.getProductId());

        product.setImages(images);

        return product;

    }

    /**
     * 대표 이미지 필수 여부 및 상세 이미지 개수 검증
     */

    private void validateProductImages(ProductCreateRequest request) {

        if (request.getThumbnailImage() == null || request.getThumbnailImage().isEmpty()) {
            throw new FileUploadException("대표 이미지는 필수 입니다.");
        }

        if (request.getDetailImages() != null && request.getDetailImages().size() > 5) {
            throw new FileUploadException("상세 이미지는 최대 5장까지 등록할 수 있습니다.");
        }

    }

    /**
     * 대표 이미지 저장
     */
    private void saveThumbnailImage(Long productId, ProductCreateRequest request) {

        UploadFile uploadFile = fileStore.storeFile(request.getThumbnailImage());

        ProductImage productImage = ProductImage.create(
                productId,
                uploadFile.getOriginalFileName(),
                uploadFile.getStoredFileName(),
                uploadFile.getFilePath(),
                ProductImageType.THUMBNAIL,
                0
        );

        productMapper.saveProductImage(productImage);

    }

    /**
     * 상세 이미지 저장
     */
    private void saveDetailImages(Long productId, ProductCreateRequest request) {

        List<UploadFile> uploadFiles = fileStore.storeFiles(request.getDetailImages());

        int sortOrder = 1;

        for (UploadFile uploadFile : uploadFiles) {

            ProductImage productImage = ProductImage.create(
                    productId,
                    uploadFile.getOriginalFileName(),
                    uploadFile.getStoredFileName(),
                    uploadFile.getFilePath(),
                    ProductImageType.DETAIL,
                    sortOrder++
            );

            productMapper.saveProductImage(productImage);

        }

    }



}
