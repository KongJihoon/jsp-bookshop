<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>도서 목록 | BookShop</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link rel="stylesheet"
          href="${contextPath}/css/bookshop.css">
</head>

<body>

<jsp:include page="../common/header.jsp"/>

<main class="bg-white py-5">

    <div class="container">

    <%-- 경로 --%>
        <div class="mb-4 small text-muted">
            홈 &gt; ${product.categoryName} &gt; ${product.name}
        </div>

        <!-- 상품 상단 영역 -->
        <section class="card shadow-sm border-0 rounded-4 mb-5">
            <div class="card-body p-4 p-md-5">

                <div class="row g-5">

                    <!-- 왼쪽 : 이미지 영역 -->
                    <div class="col-md-5">

                        <div class="bg-white border rounded-4 d-flex justify-content-center align-items-center"
                        style="height: 520px; overflow: hidden;">
                            <c:set var="hasThumbnail" value="false"/>

                            <c:forEach var="image" items="${product.images}">
                                <c:if test="${image.imageType == 'THUMBNAIL'}">
                                    <c:set var="hasThumbnail" value="true"/>

                                    <img src="${contextPath}${image.imagePath}" alt="${product.name}"
                                    class="img-fluid"
                                    style="width: 100%; height: 100%; object-fit: cover;">

                                </c:if>

                            </c:forEach>

                            <c:if test="${not hasThumbnail}">
                                <span>도서 이미지</span>
                            </c:if>
                        </div>

                    </div>

                    <!-- 오른쪽: 상품 정보 -->
                    <div class="col-md-7">
                        <div class="border-bottom pb-4 mb-4">
                            <p class="text-primary fw-semibold mb-2">${product.categoryName}</p>

                            <h2 class="fw-bold mb-3">${product.name}</h2>

                            <p class="text-muted mb-0">${product.author} 지음 · ${product.publisher}</p>
                        </div>

                        <div class="mb-4">
                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <span class="text-muted">판매가</span>

                                <strong class="fs-4">
                                    <fmt:formatNumber value="${product.price}" pattern="#,###"/>원
                                </strong>
                            </div>

                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <span class="text-muted">배송 안내</span>

                                <span>무료 배송</span>
                            </div>

                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <span class="text-muted">재고</span>

                                <span>
                                <fmt:formatNumber value="${product.stockQuantity}" pattern="#,###"/>권
                            </span>
                            </div>
                        </div>

                        <div class="bg-light rounded-4 p-4 mb-4">
                            <p class="fw-semibold mb-2">구매 전 안내</p>

                            <p class="text-muted small mb-0">장바구니 및 주문 기능은 준비 중입니다.</p>
                        </div>

                        <div class="d-grid gap-2 d-md-flex">
                            <button class="btn btn-primary btn-lg flex-fill">장바구니 담기</button>

                            <button class="btn btn-outline-primary btn-lg flex-fill">바로 구매</button>
                        </div>


                    </div>

                </div>



            </div>

        </section>

        <!-- 상세 정보 탭 -->

        <section class="card shadow-sm border-0 rounded-4 mb-4">
            <div class="card-header bg-white border-bottom">
                <ul class="nav nav-tabs card-header-tabs">
                    <li class="nav-item">
                        <a class="nav-link active" href="#description">상품 정보</a>
                    </li>

                    <li class="nav-item">
                        <a class="nav-link" href="#detailImages">상세 이미지</a>
                    </li>
                </ul>
            </div>

            <div id="description" class="card-body p-4 p-md-5">
                <h2 class="h4 fw-bold mb-4">도서 소개</h2>

                <div class="text-secondary lh-lg" style="white-space: pre-line;">
                    ${product.description}
                </div>
            </div>

        </section>

        <section id="detailImages" class="card shadow-sm border-0 rounded-4">
            <div class="card-header bg-white fw-bold py-3">
                상세 이미지
            </div>

            <div class="card-body p-4 p-md-5">
                <c:set var="detailImageCount" value="0"/>

                <c:forEach var="image" items="${product.images}">
                    <c:if test="${image.imageType == 'DETAIL'}">

                        <c:set var="detailImageCount" value="${detailImageCount + 1}"/>

                        <div class="text-center mb-4">
                            <img class="img-fluid rounded-4 border" src="${contextPath}${image.imagePath}" alt="상세 이미지 ${detailImageCount}">
                        </div>

                    </c:if>
                </c:forEach>

                <c:if test="${detailImageCount == 0}">
                    <div class="text-center text-muted py-5">등록된 상세 이미지가 없습니다.</div>
                </c:if>

            </div>
        </section>

    </div>


</main>
<jsp:include page="../common/footer.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js">
</script>

</body>

</html>