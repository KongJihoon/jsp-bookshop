<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BookShop</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${contextPath}/css/bookshop.css">
</head>
<body>

<jsp:include page="common/header.jsp"/>


<main class="main-content py-5 bg-light">
    <!-- 메인 배너 -->
    <section class="bg-white py-4 border-bottom">
        <div class="container">
            <a href="#latestBooks" class="d-block text-decoration-none">
                <img src="${contextPath}/images/main-banner.jpg"
                     alt="BookShop 신규 도서 배너"
                     class="img-fluid rounded-4 shadow-sm w-100"
                     style="max-height: 180px; object-fit: cover;">
            </a>
        </div>
    </section>

    <!-- 서비스 소개 -->
    <section class="py-5 bg-white border-bottom">
        <div class="container">
            <div class="row g-4 text-center">
                <div class="col-md-4">
                    <div class="p-4 border rounded-4 h-100">
                        <h5 class="fw-bold mb-2">개발자를 위한 도서</h5>
                        <p class="text-muted mb-0">
                            IT, 자기계발, 신간 도서를 한 곳에서 확인할 수 있습니다.
                        </p>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="p-4 border rounded-4 h-100">
                        <h5 class="fw-bold mb-2">장바구니와 주문</h5>
                        <p class="text-muted mb-0">
                            원하는 도서를 담고 주문과 결제까지 이어지는 쇼핑 흐름을 제공합니다.
                        </p>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="p-4 border rounded-4 h-100">
                        <h5 class="fw-bold mb-2">주문 상태 확인</h5>
                        <p class="text-muted mb-0">
                            결제 완료 후 배송 준비, 배송중, 배송완료 상태를 확인할 수 있습니다.
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- 최신 등록 도서 -->
    <section id="latestBooks" class="py-5">
        <div class="container">
            <div class="d-flex justify-content-between align-items-end mb-4">
                <div>
                    <h2 class="h4 fw-bold mb-1">최신 등록 도서</h2>
                    <p class="text-muted mb-0">새롭게 등록된 도서를 확인해보세요.</p>
                </div>

                <a href="${contextPath}/products" class="btn btn-sm btn-outline-secondary">전체 보기</a>
            </div>

            <div class="row g-4">

                <c:forEach var="product" items="${latestProducts}">

                    <div class="col-md-3">
                        <a href="${contextPath}/products/${product.productId}"
                           class="text-decoration-none text-dark">

                            <div class="card border-0 shadow-sm rounded-4 h-100">
                                <div class="bg-white rounded-top-4 d-flex align-items-center justify-content-center"
                                     style="height: 220px; overflow: hidden;">

                                    <c:choose>
                                        <c:when test="${not empty product.imagePath}">
                                            <img src="${contextPath}${product.imagePath}"
                                                 alt="${product.name}"
                                                 class="img-fluid"
                                                 style="width: 100%; height: 100%; object-fit: cover;">
                                        </c:when>

                                        <c:otherwise>
                                            <span class="text-muted">도서 이미지</span>
                                        </c:otherwise>
                                    </c:choose>

                                </div>
                                <div class="card-body">
                                    <p class="text-muted small mb-1">
                                        ${product.publisher}
                                    </p>

                                    <h5 class="fw-bold mb-2">
                                        ${product.name}
                                    </h5>

                                    <p class="text-muted small mb-3">
                                        ${product.author}
                                    </p>

                                    <p class="fw-bold mb-0">
                                        <fmt:formatNumber value="${product.price}" pattern="#,###"/>원
                                    </p>
                                </div>

                            </div>

                        </a>
                    </div>

                </c:forEach>

                <c:if test="${empty latestProducts}">
                    <div class="col-12">
                        <div class="card border-0 shadow-sm rounded-4">
                            <div class="card-body text-center text-muted py-5">
                                등록된 최신 도서가 없습니다.
                            </div>
                        </div>
                    </div>
                </c:if>

            </div>
        </div>

    </section>

    <!-- 카테고리 바로가기 -->
    <section class="py-5 bg-white border-top">
        <div class="container">
            <div class="d-flex justify-content-between align-items-end mb-4">
                <div>
                    <h2 class="h4 fw-bold mb-1">카테고리 바로가기</h2>
                    <p class="text-muted mb-0">관심 있는 분야의 도서를 빠르게 찾아보세요.</p>
                </div>
            </div>

            <div class="row g-3">
                <div class="col-6 col-md-4 col-lg-2">
                    <a href="${contextPath}/products"
                       class="d-block text-center text-decoration-none text-dark border rounded-4 py-4 bg-light h-100">
                        <strong>전체 도서</strong>
                    </a>
                </div>

                <c:forEach var="category" items="${headerCategories}">
                    <div class="col-6 col-md-4 col-lg-2">
                        <a href="${contextPath}/products?categoryId=${category.categoryId}"
                           class="d-block text-center text-decoration-none text-dark border rounded-4 py-4 bg-light h-100">
                            <strong>${category.categoryName}</strong>
                        </a>
                    </div>
                </c:forEach>
            </div>
        </div>
    </section>

    <!-- 이용 흐름 -->
    <section class="py-5 bg-light border-top">
        <div class="container">
            <div class="text-center mb-4">
                <h2 class="h4 fw-bold mb-2">BookShop 이용 흐름</h2>
                <p class="text-muted mb-0">
                    도서 탐색부터 주문, 결제, 배송 상태 확인까지 한 번에 진행할 수 있습니다.
                </p>
            </div>

            <div class="row g-4">
                <div class="col-md-3">
                    <div class="card border-0 shadow-sm rounded-4 h-100">
                        <div class="card-body text-center">
                            <div class="fw-bold text-primary mb-2">STEP 1</div>
                            <h6 class="fw-bold">도서 선택</h6>
                            <p class="text-muted small mb-0">
                                카테고리와 검색을 통해 원하는 도서를 찾습니다.
                            </p>
                        </div>
                    </div>
                </div>

                <div class="col-md-3">
                    <div class="card border-0 shadow-sm rounded-4 h-100">
                        <div class="card-body text-center">
                            <div class="fw-bold text-primary mb-2">STEP 2</div>
                            <h6 class="fw-bold">장바구니 담기</h6>
                            <p class="text-muted small mb-0">
                                수량을 선택하고 장바구니에 상품을 담습니다.
                            </p>
                        </div>
                    </div>
                </div>

                <div class="col-md-3">
                    <div class="card border-0 shadow-sm rounded-4 h-100">
                        <div class="card-body text-center">
                            <div class="fw-bold text-primary mb-2">STEP 3</div>
                            <h6 class="fw-bold">주문 및 결제</h6>
                            <p class="text-muted small mb-0">
                                배송 정보를 입력하고 결제를 완료합니다.
                            </p>
                        </div>
                    </div>
                </div>

                <div class="col-md-3">
                    <div class="card border-0 shadow-sm rounded-4 h-100">
                        <div class="card-body text-center">
                            <div class="fw-bold text-primary mb-2">STEP 4</div>
                            <h6 class="fw-bold">주문 상태 확인</h6>
                            <p class="text-muted small mb-0">
                                마이페이지에서 주문과 배송 상태를 확인합니다.
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

</main>

<jsp:include page="common/footer.jsp"/>



    
</body>
</html>
