<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>BookShop Admin Dashboard</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>

<body class="bg-light">

<jsp:include page="../common/toast.jsp"/>

<div class="d-flex">

    <!-- Sidebar -->

<jsp:include page="../common/dashboard-sidebar.jsp"/>


    <!-- Main Content -->
    <div class="flex-grow-1">

        <!-- Header -->
        <header class="bg-white border-bottom px-4 py-3 d-flex justify-content-between align-items-center">
            <div>
                <h1 class="h5 mb-0 fw-bold">관리자 대시보드</h1>
            </div>

            <div class="text-muted small">
                ${sessionScope.loginMember.name}님
            </div>
        </header>

        <main class="p-4">

            <div class="mb-4">
                <h2 class="h4 fw-bold mb-1">대시보드</h2>
                <p class="text-muted mb-0">BookShop 운영 현황을 확인합니다.</p>
            </div>

            <!-- Summary Cards -->
            <div class="row g-4 mb-4">
                <div class="col-md-3">
                    <div class="card border-0 shadow-sm rounded-4">
                        <div class="card-body">
                            <p class="text-muted mb-1">전체 도서</p>
                            <h3 class="fw-bold mb-0">${dashboard.totalProductCount}</h3>
                        </div>
                    </div>
                </div>

                <div class="col-md-3">
                    <div class="card border-0 shadow-sm rounded-4">
                        <div class="card-body">
                            <p class="text-muted mb-1">전체 회원</p>
                            <h3 class="fw-bold mb-0">${dashboard.totalMemberCount}</h3>
                        </div>
                    </div>
                </div>

                <div class="col-md-3">
                    <div class="card border-0 shadow-sm rounded-4">
                        <div class="card-body">
                            <p class="text-muted mb-1">오늘 주문</p>
                            <h3 class="fw-bold mb-0">0</h3>
                        </div>
                    </div>
                </div>

                <div class="col-md-3">
                    <div class="card border-0 shadow-sm rounded-4">
                        <div class="card-body">
                            <p class="text-muted mb-1">품절 도서</p>
                            <h3 class="fw-bold mb-0">${dashboard.soldOutProductCount}</h3>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 관리 바로가기 -->
            <div class="row g-4">
                <div class="col-md-4">
                    <a href="${contextPath}/admin/product/add" class="text-decoration-none">
                        <div class="card border-0 shadow-sm rounded-4 h-100">
                            <div class="card-body">
                                <h5 class="fw-bold text-dark">도서 등록</h5>
                                <p class="text-muted mb-0">
                                    새로운 도서를 등록합니다.
                                </p>
                            </div>
                        </div>
                    </a>
                </div>

                <div class="col-md-4">
                    <a href="${contextPath}/admin/product/list" class="text-decoration-none">
                        <div class="card border-0 shadow-sm rounded-4 h-100">
                            <div class="card-body">
                                <h5 class="fw-bold text-dark">도서 관리</h5>
                                <p class="text-muted mb-0">
                                    등록된 도서를 수정하거나 삭제합니다.
                                </p>
                            </div>
                        </div>
                    </a>
                </div>

                <div class="col-md-4">
                    <a href="${contextPath}/admin/orders" class="text-decoration-none">
                        <div class="card border-0 shadow-sm rounded-4 h-100">
                            <div class="card-body">
                                <h5 class="fw-bold text-dark">주문 관리</h5>
                                <p class="text-muted mb-0">
                                    회원 주문 내역을 확인합니다.
                                </p>
                            </div>
                        </div>
                    </a>
                </div>
            </div>

        </main>

        <div class="card shadow-sm mt-4">

            <div class="card-header fw-bold">
                병기련
            </div>

            <div class="card-body text-center">

                <img
                        src="${contextPath}/images/friends.jpeg"
                        alt="friend"
                        class="img-fluid rounded shadow-sm"
                        style="max-height:400px;">

                <p class="mt-3 text-muted">
                    병신 헬창련
                </p>

            </div>

        </div>
    </div>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>