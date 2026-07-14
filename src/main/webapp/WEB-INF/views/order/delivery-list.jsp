<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>주문배송 조회 | BookShop</title>

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
                <h3 class="fw-bold mb-1">주문배송 조회</h3>
                <p class="text-muted mb-0">
                    주문한 상품의 배송 진행 상태를 확인할 수 있습니다.
                </p>
            </div>

            <c:choose>
                <c:when test="${empty deliveries}">
                    <div class="card border-0 shadow-sm rounded-4">
                        <div class="card-body text-center py-5">
                            <p class="text-muted mb-4">배송 조회 가능한 주문이 없습니다.</p>
                            <a href="${contextPath}/products" class="btn btn-primary">
                                쇼핑하러 가기
                            </a>
                        </div>
                    </div>
                </c:when>

                <c:otherwise>
                    <div class="d-flex flex-column gap-3">

                        <c:forEach var="delivery" items="${deliveries}">
                            <div class="card border-0 shadow-sm rounded-4">
                                <div class="card-body p-4">

                                    <div class="d-flex justify-content-between align-items-start border-bottom pb-3 mb-3">
                                        <div>
                                            <div class="fw-bold mb-1">
                                                    ${delivery.formattedOrderedAt} 주문
                                            </div>
                                            <div class="text-muted small">
                                                주문번호 #${delivery.orderId}
                                            </div>
                                        </div>

                                        <span class="badge text-bg-primary px-3 py-2">
                                                ${delivery.orderStatusDescription}
                                        </span>
                                    </div>

                                    <div class="d-flex gap-3 align-items-center mb-4">
                                        <div class="border rounded-4 bg-white d-flex align-items-center justify-content-center"
                                             style="width: 90px; height: 120px; overflow: hidden; flex-shrink: 0;">

                                            <c:choose>
                                                <c:when test="${not empty delivery.representativeImagePath}">
                                                    <img src="${contextPath}${delivery.representativeImagePath}"
                                                         alt="${delivery.representativeProductName}"
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
                                                    ${delivery.displayProductName}
                                            </div>

                                            <div class="text-muted small mb-2">
                                                받는 분 ${delivery.receiverName}
                                            </div>

                                            <div class="text-muted small">
                                                [${delivery.zipcode}] ${delivery.address}
                                            </div>
                                        </div>

                                        <div class="text-end">
                                            <div class="fw-bold mb-3">
                                                <fmt:formatNumber value="${delivery.totalPrice}" pattern="#,###"/>원
                                            </div>

                                            <a href="${contextPath}/orders/${delivery.orderId}"
                                               class="btn btn-outline-primary btn-sm">
                                                주문 상세보기
                                            </a>
                                        </div>
                                    </div>

                                    <div class="bg-light rounded-4 p-3">
                                        <div class="d-flex justify-content-between text-center small fw-semibold">

                                            <div class="flex-fill ${delivery.orderStatusName == 'PAID' ? 'text-primary' : 'text-muted'}">
                                                결제완료
                                            </div>

                                            <div class="flex-fill ${delivery.orderStatusName == 'PREPARING' ? 'text-primary' : 'text-muted'}">
                                                배송준비
                                            </div>

                                            <div class="flex-fill ${delivery.orderStatusName == 'SHIPPING' ? 'text-primary' : 'text-muted'}">
                                                배송중
                                            </div>

                                            <div class="flex-fill ${delivery.orderStatusName == 'DELIVERED' ? 'text-primary' : 'text-muted'}">
                                                배송완료
                                            </div>

                                        </div>

                                        <div class="progress mt-3" style="height: 8px;">
                                            <c:choose>
                                                <c:when test="${delivery.orderStatusName == 'PAID'}">
                                                    <div class="progress-bar" style="width: 25%;"></div>
                                                </c:when>
                                                <c:when test="${delivery.orderStatusName == 'PREPARING'}">
                                                    <div class="progress-bar" style="width: 50%;"></div>
                                                </c:when>
                                                <c:when test="${delivery.orderStatusName == 'SHIPPING'}">
                                                    <div class="progress-bar" style="width: 75%;"></div>
                                                </c:when>
                                                <c:when test="${delivery.orderStatusName == 'DELIVERED'}">
                                                    <div class="progress-bar" style="width: 100%;"></div>
                                                </c:when>
                                            </c:choose>
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

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>