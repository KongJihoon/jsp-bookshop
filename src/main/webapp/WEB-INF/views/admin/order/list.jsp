<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>주문 관리 | BookShop Admin</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${contextPath}/css/bookshop.css" rel="stylesheet">
</head>

<body class="bg-light">

<jsp:include page="../../common/toast.jsp"/>

<div class="d-flex">

<jsp:include page="../../common/dashboard-sidebar.jsp"/>

    <div class="flex-grow-1">
        <header class="bg-white border-bottom px-4 py-3 d-flex justify-content-between align-items-center">
            <div>
                <h1 class="h5 mb-0 fw-bold">주문 관리</h1>
            </div>
            <div class="text-muted small">
                ${sessionScope.loginMember.name}님
            </div>
        </header>

        <main class="p-4">

            <div class="mb-4">
                <h2 class="h4 fw-bold mb-1">주문 관리</h2>
                <p class="text-muted mb-0">회원 주문 내역과 배송 상태를 관리합니다.</p>
            </div>

            <div class="card border-0 shadow-sm rounded-4">
                <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
                    <h5 class="fw-bold mb-0">주문 목록</h5>
                    <span class="text-muted small">
                        총 ${pageResponse.totalCount}건
                    </span>
                </div>

                <div class="card-body p-0">
                    <table class="table align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th>주문번호</th>
                                <th>주문자</th>
                                <th>대표 상품</th>
                                <th>주문금액</th>
                                <th>주문상태</th>
                                <th>주문일</th>
                                <th class="text-end">관리</th>
                            </tr>
                        </thead>

                        <tbody>
                        <c:choose>
                            <c:when test="${empty orders}">
                                <tr>
                                    <td colspan="7" class="text-center text-muted py-5">
                                        주문 내역이 없습니다.
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>

                                    <c:forEach var="order" items="${orders}">
                                        <tr>
                                        <td class="fw-bold">#${order.orderId}</td>

                                        <td>${order.memberName}</td>

                                        <td>
                                            ${order.representativeProductName}
                                            <c:if test="${order.totalItemCount > 1}">
                                                외 ${order.totalItemCount - 1}건
                                            </c:if>
                                        </td>

                                        <td>
                                            <fmt:formatNumber value="${order.totalPrice}" pattern="#,###"/>원
                                        </td>

                                        <td>
                                            <span class="badge bg-primary-subtle text-primary">${order.orderStatusDescription}</span>
                                        </td>

                                        <td>
                                            ${order.orderedAtText}
                                        </td>

                                        <td class="text-end">
                                            <a href="${contextPath}/admin/orders/${order.orderId}"
                                                class="btn btn-sm btn-outline-primary">
                                                상세 보기
                                            </a>
                                        </td>
                                        </tr>
                                    </c:forEach>

                            </c:otherwise>
                        </c:choose>
                        </tbody>


                    </table>
                </div>

                <c:if test="${pageResponse.totalPages > 1}">
                    <div class="card-footer bg-white border-0 py-3">
                        <nav aria-label="주문 목록 페이지">
                            <ul class="pagination justify-content-center mb-0">
                                <li class="page-item ${!pageResponse.hasPrevious ? 'disabled' : ''}">
                                    <a class="page-link" href="${contextPath}/admin/orders?page=${pageResponse.page - 1}">이전</a>
                                </li>

                                <c:forEach var="pageNumber" begin="1" end="${pageResponse.totalPages}">
                                    <li class="page-item ${pageResponse.page == pageNumber ? 'active' : ''}">
                                        <a class="page-link" href="${contextPath}/admin/orders?page=${pageNumber}">${pageNumber}</a>
                                    </li>
                                </c:forEach>

                                <li class="page-item ${!pageResponse.hasNext ? 'disabled' : ''}">
                                    <a class="page-link" href="${contextPath}/admin/orders?page=${pageResponse.page + 1}">다음</a>
                                </li>
                            </ul>
                        </nav>
                    </div>
                </c:if>
            </div>

        </main>
    </div>

</div>


<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>

</html>

