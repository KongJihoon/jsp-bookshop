<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>주문 내역 | BookShop</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link rel="stylesheet" href="${contextPath}/css/bookshop.css">
    <link rel="stylesheet" href="${contextPath}/css/mypage.css">
</head>

<body>
<jsp:include page="../common/header.jsp"/>

<main class="mypage">
    <jsp:include page="../common/mypage-menu.jsp"/>

    <div class="mypage-layout">
        <jsp:include page="../common/mypage-sidebar.jsp"/>

        <section class="mypage-content">

            <div class="mb-4">
                <h3 class="fw-bold mb-1">주문 내역</h3>
                <p class="text-muted mb-0">주문한 도서 목록을 확인할 수 있습니다.</p>
            </div>

            <c:choose>
                <c:when test="${empty orders}">
                    <div class="card border-0 shadow-sm rounded-4">
                        <div class="card-body text-center py-5">
                            <p class="text-muted mb-4">주문 내역이 없습니다.</p>
                            <a href="${contextPath}/products"
                               class="btn btn-primary">
                                쇼핑하러 가기
                            </a>
                        </div>
                    </div>
                </c:when>

                <c:otherwise>
                    <div class="d-flex flex-column gap-3">

                        <c:forEach var="order" items="${orders}">
                            <div class="card border-0 shadow-sm rounded-4">
                                <div class="card-body p-4">

                                    <div class="d-flex justify-content-between align-items-center border-bottom pb-3 mb-3">
                                        <div>
                                            <div class="fw-bold">
                                                    ${order.formattedOrderedAt} 주문
                                            </div>

                                            <div class="text-muted small">
                                                주문번호 #${order.orderId}
                                            </div>
                                        </div>

                                        <a href="${contextPath}/orders/${order.orderId}"
                                           class="btn btn-outline-primary btn-sm">
                                            상세보기
                                        </a>
                                    </div>

                                    <div class="d-flex gap-3 align-items-center">

                                        <div class="border rounded-4 bg-white d-flex align-items-center justify-content-center"
                                             style="width: 90px; height: 120px; overflow: hidden; flex-shrink: 0;">

                                            <c:choose>
                                                <c:when test="${not empty order.representativeImagePath}">
                                                    <img src="${contextPath}${order.representativeImagePath}"
                                                         alt="${order.representativeProductName}"
                                                         class="img-fluid"
                                                         style="width: 100%; height: 100%; object-fit: cover;">
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-muted small">도서 이미지</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>

                                        <div class="flex-grow-1">
                                            <div class="fw-bold mb-2">
                                                    ${order.displayProductName}
                                            </div>

                                            <div class="text-muted small mb-2">
                                                주문상태:
                                                <span class="fw-semibold text-primary">
                                                        ${order.orderStatus.description}
                                                </span>
                                            </div>

                                            <div class="fw-bold">
                                                <fmt:formatNumber value="${order.totalPrice}" pattern="#,###"/>원
                                            </div>
                                        </div>

                                    </div>

                                </div>
                            </div>
                        </c:forEach>

                    </div>
                </c:otherwise>
            </c:choose>

        </section>
    </div>
</main>

<jsp:include page="../common/footer.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js">
</script>

</body>
</html>