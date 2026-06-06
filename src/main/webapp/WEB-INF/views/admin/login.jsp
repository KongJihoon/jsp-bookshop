<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />


<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bookshop Admin Login</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

</head>
<body class="bg-light">

<jsp:include page="../common/toast.jsp"/>

<main class="min-vh-100 d-flex justify-content-center align-items-center">

    <div class="container">

        <div class="row justify-content-center">

            <div class="col-sm-10 col-md-6 col-lg-4">

                <div class="text-center mb-4">
                    <h1 class="fw-bold mb-2">BookShop Admin</h1>
                    <p class="text-muted mb-0">관리자 계정으로 로그인하세요.</p>
                </div>

                <div class="card border-0 shadow-sm rounded-4">
                    <div class="card-body p-4 p-md-5">
                        <form action="${contextPath}/admin/login" method="post">

                            <div class="mb-3">
                                <label for="loginId" class="form-label fw-semibold">아이디</label>
                                <input
                                        type="text"
                                        id="loginId"
                                        name="loginId"
                                        class="form-control"
                                        placeholder="관리자 아이디를 입력해주세요."
                                        autocomplete="username"
                                        value="${admin.loginId}">

                            </div>
                            <div class="mb-4">
                                <label for="password" class="form-label fw-semibold">비밀번호</label>
                                <input
                                        type="password"
                                        id="password"
                                        name="password"
                                        class="form-control"
                                        placeholder="비밀번호를 입력해주세요."
                                        autocomplete="current-password">
                            </div>

                            <button type="submit" class="btn btn-dark w-100 py-2 fw-semibold">
                                관리자 로그인
                            </button>

                        </form>

                        <div class="text-center mt-4">
                            <a href="${contextPath}/" class="text-secondary text-decoration-none small">← BookShop 홈으로 돌아가기</a>
                        </div>

                    </div>
                </div>

                <p class="text-center text-muted small mt-4 mb-0">관리자 계정은 일반 회원가입으로 생성할 수 없습니다.</p>

            </div>

        </div>


    </div>


</main>


<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>