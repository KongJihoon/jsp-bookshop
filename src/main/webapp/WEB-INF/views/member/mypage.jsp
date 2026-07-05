<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>마이페이지 | BookShop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
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

            <article class="content-section">
                <div class="section-header">
                    <h3>최근 주문정보</h3>
                    <a href="${contextPath}/orders">더보기</a>
                </div>

                <c:choose>
                    <c:when test="${empty myPageHome.recentOrders}">
                        <div class="empty-box">최근 주문 내역이 없습니다.</div>
                    </c:when>

                    <c:otherwise>
                        <div class="d-flex flex-column gap-3">
                            <c:forEach var="order" items="${myPageHome.recentOrders}">
                                <a href="${contextPath}/orders/${order.orderId}"
                                   class="text-decoration-none text-dark">
                                    <div class="card border-0 shadow-sm rounded-4">
                                        <div class="card-body p-3">

                                            <div class="d-flex justify-content-between align-items-center mb-3">
                                                <div>
                                                    <div class="fw-bold">
                                                            ${order.formattedOrderedAt} 주문
                                                    </div>
                                                    <div class="text-muted small">
                                                        주문번호 #${order.orderId}
                                                    </div>
                                                </div>

                                                <span class="badge text-bg-primary">
                                                        ${order.orderStatus.description}
                                                </span>
                                            </div>

                                            <div class="d-flex align-items-center gap-3">
                                                <div class="border rounded-3 bg-white d-flex align-items-center justify-content-center"
                                                     style="width: 70px; height: 90px; overflow: hidden; flex-shrink: 0;">

                                                    <c:choose>
                                                        <c:when test="${not empty order.representativeImagePath}">
                                                            <img src="${contextPath}${order.representativeImagePath}"
                                                                 alt="${order.representativeProductName}"
                                                                 style="width: 100%; height: 100%; object-fit: cover;">
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="text-muted small">이미지</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>

                                                <div class="flex-grow-1">
                                                    <div class="fw-semibold mb-1">
                                                            ${order.displayProductName}
                                                    </div>

                                                    <div class="fw-bold text-primary">
                                                        <fmt:formatNumber value="${order.totalPrice}" pattern="#,###"/>원
                                                    </div>
                                                </div>
                                            </div>

                                        </div>
                                    </div>
                                </a>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </article>

            <article class="content-section">
                <div class="section-header">
                    <h3>장바구니 상품</h3>
                    <a href="#">더보기</a>
                </div>
                <div class="empty-box">장바구니에 담긴 상품이 없습니다.</div>
            </article>

            <article class="content-section">
                <div class="section-header">
                    <h3>회원정보</h3>
                    <a href="#">조회하기</a>
                </div>

                <div class="info-box">
                    회원정보, 조회, 수정, 탈퇴 메뉴를 이용할 수 있습니다.
                </div>
            </article>

        </section>

    </div>


</main>



<jsp:include page="../common/footer.jsp"/>


</body>