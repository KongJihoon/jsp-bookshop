<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>주문 상세 | BookShop</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link rel="stylesheet" href="${contextPath}/css/bookshop.css">
    <link rel="stylesheet" href="${contextPath}/css/mypage.css">
    <link rel="stylesheet" href="${contextPath}/css/order.css">
</head>

<body>
<jsp:include page="../common/header.jsp"/>

<main class="mypage">
    <jsp:include page="../common/mypage-menu.jsp"/>

    <div class="mypage-layout">
        <jsp:include page="../common/mypage-sidebar.jsp"/>

        <section class="mypage-content">

            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h3 class="fw-bold mb-1">주문 상세</h3>
                    <p class="text-muted mb-0">주문한 도서와 배송 정보를 확인할 수 있습니다.</p>
                </div>

                <a href="${contextPath}/orders"
                   class="btn btn-outline-secondary btn-sm">
                    목록으로
                </a>
            </div>

            <section class="order-section">
                <div class="order-section-title">
                    <h4>주문 상품</h4>

                    <div class="order-meta">
            <span class="badge text-bg-primary px-3 py-2">
                ${order.orderStatus.description}
            </span>
                        <span>주문번호 #${order.orderId}</span>
                        <span>${order.formattedOrderedAt}</span>
                    </div>
                </div>

                <div class="order-product-list">
                    <c:forEach var="item" items="${order.items}">
                        <div class="order-item">
                            <a href="${contextPath}/products/${item.productId}"
                               class="order-item-image">

                                <c:choose>
                                    <c:when test="${not empty item.imagePath}">
                                        <img src="${contextPath}${item.imagePath}"
                                             alt="${item.productName}">
                                    </c:when>
                                    <c:otherwise>
                            <span class="text-muted small d-flex h-100 align-items-center justify-content-center">
                                도서 이미지
                            </span>
                                    </c:otherwise>
                                </c:choose>
                            </a>

                            <div class="order-item-info">
                                <div class="order-item-name">${item.productName}</div>

                                <div class="order-item-option">
                                    수량 ${item.quantity}개
                                    <span class="mx-1">·</span>
                                    단가 <fmt:formatNumber value="${item.price}" pattern="#,###"/>원
                                </div>
                            </div>

                            <div class="order-item-price">
                                <fmt:formatNumber value="${item.itemTotalPrice}" pattern="#,###"/>원
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </section>

            <section class="order-section">
                <div class="order-section-title">
                    <h4>배송 정보</h4>
                </div>

                <div class="order-info-box">
                    <div class="order-info-row">
                        <div class="order-info-label">받는 분</div>

                        <div>
                            <div class="fw-semibold mb-2">
                                ${order.receiverName} / ${order.receiverPhone}
                            </div>

                            <div>
                                [${order.zipcode}] ${order.address} ${order.addressDetail}
                            </div>

                            <div class="text-muted small mt-2">
                                배송 기능은 추후 구현 예정입니다.
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            <section class="order-section">
                <div class="order-section-title">
                    <h4>결제 정보</h4>
                </div>

                <div class="payment-summary">
                    <div class="payment-summary-item">
                        <div class="payment-summary-label">주문 금액</div>
                        <div class="payment-summary-price">
                            <fmt:formatNumber value="${order.totalPrice}" pattern="#,###"/>원
                        </div>
                    </div>

                    <div class="payment-summary-item">
                        <div class="payment-summary-label">결제 수단</div>
                        <div class="payment-summary-price">
                            ${order.paymentMethodDescription}
                        </div>
                    </div>

                    <div class="payment-summary-item">
                        <div class="payment-summary-label">최종 결제 금액</div>
                        <div class="payment-summary-price primary">
                            <fmt:formatNumber value="${order.totalPrice}" pattern="#,###"/>원
                        </div>
                    </div>
                </div>
            </section>

        </section>
    </div>
</main>

<jsp:include page="../common/footer.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>