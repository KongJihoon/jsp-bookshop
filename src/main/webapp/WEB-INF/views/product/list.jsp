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

<main class="bg-light py-5">

    <div class="container">
        <div class="d-flex justify-content-between align-items-end mb-4">
            <div>
                <h2 class="fw-bold mb-1">도서 목록</h2>
                <p class="text-muted mb-0">BookShop에 등록된 도서입니다.</p>
            </div>
        </div>

        <div class="row g-4">

            <c:forEach var="product" items="${products}">

                <div class="col-md-3">
                    <a href="${contextPath}/products/${product.productId}"
                    class="text-decoration-none text-dark">
                        <div class="card border-0 shadow-sm rounded-4 h-100">
                            <div class="bg-white rounded-top-4 d-flex align-items-center justify-content-center"
                            style="height: 220px; overflow: hidden;">
                                <c:choose>
                                    <c:when test="${not empty product.imagePath}">
                                        <img src="${contextPath}${product.imagePath}" alt="${product.name}"
                                        class="img-fluid" style="width: 100%; height: 100%; object-fit: cover;">
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-muted">도서 이미지</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="card-body">
                                <p class="text-muted small mb-1">${product.categoryName}</p>

                                <h5 class="fw-bold mb-2">${product.name}</h5>

                                <p class="text-muted small mb-2">${product.author} · ${product.publisher}</p>

                                <p class="fw-bold mb-0">
                                    <fmt:formatNumber value="${product.price}" pattern="#,###"/>원
                                </p>

                            </div>
                        </div>
                    </a>

                </div>

            </c:forEach>

            <c:if test="${empty products}">
                <div class="col-12">
                    <div class="card border-0 shadow-sm rounded-4">
                        <div class="card-body text-center text-muted py-5">
                            <c:choose>
                                <c:when test="${not empty keyword}">
                                    검색 결과가 없습니다.
                                </c:when>

                                <c:when test="${not empty selectedCategoryId}">
                                    해당 카테고리에 등록된 도서가 없습니다.
                                </c:when>

                                <c:otherwise>
                                    등록된 도서가 없습니다.
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </c:if>
        </div>

        <%-- 페이징 --%>
        <c:if test="${productPage.totalPages > 1}">

            <nav class="mt-5">
                <ul class="pagination justify-content-center">


                    <c:if test="${productPage.hasPrevious}">

                        <c:url var="previousPageUrl" value="/products">

                            <c:param name="page" value="${productPage.page - 1}"/>
                            <c:if test="${not empty selectedCategoryId}">
                                <c:param name="categoryId" value="${selectedCategoryId}"/>
                            </c:if>
                            <c:if test="${not empty keyword}">
                                <c:param name="keyword" value="${keyword}"/>
                            </c:if>

                        </c:url>
                        <li class="page-item">
                            <a class="page-link" href="${previousPageUrl}">이전</a>
                        </li>
                    </c:if>
                    
                    <c:forEach begin="1"
                    end="${productPage.totalPages}"
                    var="pageNumber">
                        <c:url var="pageUrl" value="/products">
                            <c:param name="page" value="${pageNumber}"/>
                            <c:if test="${not empty selectedCategoryId}">
                                <c:param name="categoryId" value="${selectedCategoryId}"/>
                            </c:if>
                            <c:if test="${not empty keyword}">
                                <c:param name="keyword" value="${keyword}"/>
                            </c:if>
                        </c:url>

                        <li class="page-item ${productPage.page == pageNumber ? 'active' : ''}">
                            <a class="page-link" href="${pageUrl}">${pageNumber}</a>
                        </li>
                    </c:forEach>
                    
                    <c:if test="${productPage.hasNext}">
                        <c:url var="nextPageUrl" value="/products">
                            <c:param name="page" value="${productPage.page + 1}"/>
                            <c:if test="${not empty selectedCategoryId}">
                                <c:param name="categoryId" value="${selectedCategoryId}"/>
                            </c:if>
                            <c:if test="${not empty keyword}">
                                <c:param name="keyword" value="${keyword}"/>
                            </c:if>
                        </c:url>

                        <li class="page-item">
                            <a class="page-link" href="${nextPageUrl}">다음</a>
                        </li>
                        
                    </c:if>
                    
                </ul>
            </nav>

        </c:if>
    </div>

</main>

<jsp:include page="../common/footer.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js">
</script>
</body>

</html>