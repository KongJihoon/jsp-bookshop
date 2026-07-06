<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>주문 상세 | BookShop Admin</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${contextPath}/css/bookshop.css" rel="stylesheet">
</head>

<body class="bg-light">

<jsp:include page="../../common/toast.jsp"/>

<div class="d-flex">

<jsp:include page="../../common/dashboard-sidebar.jsp"/>

    <div class="flex-grow-1">

        <header class="bg-light border-bottom px-4 py-3 d-flex justify-content-between align-items-center">
            <div>
                <h1 class="h5 mb-0 fw-bold">주문 상세</h1>
            </div>

            <div class="text-muted small">
                ${sessionScope.loginMember.name}님
            </div>
        </header>

        <main class="p-4">
            <div class="d-flex justify-content-between align-items-start mb-4">
                <div>
                    <h2 class="h4 fw-bold mb-1">주문 상세</h2>
                    <p class="text-muted mb-0">주문 상품, 배송지, 주문 상태를 확인합니다.</p>
                </div>

                <a href="${contextPath}/admin/orders" class="btn btn-outline-secondary">목록으로</a>
            </div>

            <div class="row g-4">
                <div class="col-lg-8">
                    <div class="card border-0 shadow-sm rounded-4 mb-4">
                        <div class="card-header bg-white py-3">
                            <h5 class="fw-bold mb-0">주문 상품</h5>
                        </div>

                        <div class="card-body">
                            <c:forEach var="item" items="${orderItems}">
                                <div class="d-flex gap-3 py-3 border-bottom">
                                    <c:choose>
                                        <c:when test="${not empty item.imagePath}">
                                            <img src="${contextPath}${item.imagePath}" alt="${item.productName}"
                                                class="rounded-3 border bg-white"
                                                style="width: 90px; height: 120px; object-fit: cover;">
                                        </c:when>
                                        <c:otherwise>
                                            <div class="rounded-3 border bg-white d-flex align-items-center justify-content-center text-muted">
                                                이미지 없음
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                    <div class="flex-grow-1">
                                        <h6 class="fw-bold mb-2">${item.productName}</h6>
                                        <p class="text-muted mb-1">
                                            수량 ${item.quantity}개
                                        </p>
                                        <p class="mb-0">
                                            <fmt:formatNumber value="${item.price}" pattern="#,###"/>원
                                        </p>
                                    </div>

                                    <div class="text-end fw-bold text-primary">
                                        <fmt:formatNumber value="${item.itemTotalPrice}" pattern="#,###"/>원
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                    <div class="card border-0 shadow-sm rounded-4">
                        <div class="card-header bg-white py-3">
                            <h5 class="fw-bold mb-0">배송 정보</h5>
                        </div>

                        <div class="card-body">
                            <div class="row mb-3">
                                <div class="col-3 text-muted">수령인</div>
                                <div class="col fw-semibold">${order.receiverName}</div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-3 text-muted">연락처</div>
                                <div class="col">${order.receiverPhone}</div>
                            </div>

                            <div class="row">
                                <div class="col-3 text-muted">주소</div>
                                <div class="col">
                                    [${order.zipcode}] ${order.address} ${order.addressDetail}
                                </div>
                            </div>
                        </div>
                    </div>

                </div>

                <div class="col-lg-4">

                    <div class="card border-0 shadow-sm rounded-4 mb-4">
                        <div class="card-header bg-white py-3">
                            <h5 class="fw-bold mb-0">주문 정보</h5>
                        </div>

                        <div class="card-body">
                            <div class="d-flex justify-content-between mb-3">
                                <span class="text-muted">주문번호</span>
                                <strong>#${order.orderId}</strong>
                            </div>

                            <div class="d-flex justify-content-between mb-3">
                                <span class="text-muted">주문자</span>
                                <strong>${order.memberName}</strong>
                            </div>

                            <div class="d-flex justify-content-between mb-3">
                                <span class="text-muted">이메일</span>
                                <span>${order.memberEmail}</span>
                            </div>

                            <div class="d-flex justify-content-between mb-3">
                                <span class="text-muted">주문상태</span>
                                <span class="badge bg-primary-subtle text-primary">
                                    ${order.orderStatusDescription}
                                </span>
                            </div>

                            <div class="d-flex justify-content-between mb-3">
                                <span class="text-muted">주문일</span>
                                <span>${order.orderedAtText}</span>
                            </div>

                            <hr>

                            <div class="d-flex justify-content-between align-items-center">
                                <span class="fw-semibold">총 결제금액</span>
                                <strong class="fs-4 text-primary">
                                    <fmt:formatNumber value="${order.totalPrice}" pattern="#,###"/>원
                                </strong>
                            </div>
                        </div>
                    </div>

                    <div class="card border-0 shadow-sm rounded-4">
                        <div class="card-header bg-white py-3">
                            <h5 class="fw-bold mb-0">배송 상태 변경</h5>
                        </div>

                        <div class="card-body">
                            <form action="${contextPath}/admin/orders/${order.orderId}/status" method="post">
                                <div class="mb-3">
                                    <label for="orderStatus" class="form-label fw-semibold">
                                        변경할 상태
                                    </label>

                                    <select name="orderStatus" id="orderStatus" class="form-select">
                                        <option value="">상태 선택</option>
                                        <option value="PREPARING">배송준비</option>
                                        <option value="SHIPPING">배송중</option>
                                        <option value="DELIVERED">배송완료</option>
                                    </select>
                                </div>

                                <button type="submit" class="btn btn-primary w-100">상태 변경</button>
                            </form>

                            <p class="text-muted small mt-3 mb-0">
                                결제완료 → 배송준비 → 배송중 → 배송완료 순서로만 변경할 수 있습니다.
                            </p>
                        </div>
                    </div>
                </div>

            </div>
        </main>

    </div>

</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
