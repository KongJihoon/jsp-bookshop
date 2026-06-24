<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
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
    <!-- 메인 베너 -->
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
                        <a href="${contextPath}/products/${product.productId}">

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
                                <span class="text-muted">
                                    도서 이미지
                                </span>
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
                                            ${product.price}원
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

</main>

<jsp:include page="common/footer.jsp"/>



    
</body>
</html>