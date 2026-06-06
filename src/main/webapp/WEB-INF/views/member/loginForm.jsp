<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>



<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>로그인</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${contextPath}/css/bookshop.css">
</head>
<body>
<jsp:include page="../common/header.jsp"/>

<main class="py-5 bg-light">

    <div class="container">
        <div class="mx-auto bg-white border rounded-3 p-4 p-md-5">
            <h2 class="h4 fw-bold mb-2">
                로그인
            </h2>
            <p class="text-muted">
                BookShop에 로그인하여 더 나은 서비스를 이용해보세요.
            </p>


            <form id="loginForm" action="${contextPath}/member/login" method="post">

                <div class="mb-3">
                    <label class="form-label fw-semibold" for="loginId">
                        아이디 <span class="text-danger">*</span>
                    </label>
                    <input type="text" name="loginId" id="loginId" class="form-control" placeholder="아이디를 입력해주세요."
                    value="${member.loginId}">
                </div>

                <div class="mb-3">
                    <label class="form-label fw-semibold" for="password">
                        비밀번호 <span class="text-danger">*</span>
                    </label>
                    <input type="password" name="password" id="password" class="form-control" placeholder="비밀번호를 입력해주세요.">
                </div>

                <div class="d-grid mt-4">

                    <button type="submit" class="btn btn-primary btn-lg">
                        로그인
                    </button>
                </div>

                <div class="d-flex justify-content-center gap-3 mt-4 small">

                    <a href="#" class="text-secondary text-decoration-none">아이디 찾기</a>
                    <span class="text-muted">|</span>
                    <a href="#" class="text-secondary text-decoration-none">비밀번호 찾기</a>
                    <span class="text-muted">|</span>
                    <a href="${contextPath}/member/signup" class="text-secondary text-decoration-none">회원가입</a>
                </div>


            </form>
        </div>



    </div>


</main>


<jsp:include page="../common/footer.jsp"/>

<script>
    const contextPath = "${contextPath}";
</script>

<c:if test="${not empty errorMessage}">

    <script>
        window.addEventListener("DOMContentLoaded", function () {
           alert("${errorMessage}");
        });
    </script>

</c:if>

</body>

</html>