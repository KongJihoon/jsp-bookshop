<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>아이디 찾기 결과</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">
    <link rel="stylesheet" href="${contextPath}/css/bookshop.css">
</head>

<body>
<jsp:include page="../common/header.jsp"/>

<main class="py-5 bg-light">

    <div class="container">

        <div class="mx-auto bg-white border rounded-3 p-4 p-md-5 text-center"
            style="max-width: 560px;">

            <h2 class="h4 fw-bold mb-2">
                아이디 찾기 결과
            </h2>

            <p class="text-muted mb-4">
                입력하신 정보와 일치하는 회원 아이디를 찾았습니다.
            </p>

            <div class="bg-light border rounded-3 py-4 px-3 mb-4">

                <div class="text-muted small mb-2">
                    회원 아이디
                </div>

                <div class="fs-4 fw-bold text-primary">
                    ${findIdResponse.maskedLoginId}
                </div>

                <div class="d-grid gap-2">

                    <a href="${contextPath}/member/login"
                       class="btn btn-primary btn-lg">
                        로그인하기
                    </a>

                    <a href="${contextPath}/member/find-password"
                       class="btn btn-outline-secondary btn-lg">
                        비밀번호 찾기
                    </a>

                </div>

                <div class="mt-4 small">
                    <a href="${contextPath}/member/find-id"
                       class="text-secondary text-decoration-none">
                        다시 찾기
                    </a>
                </div>
            </div>
        </div>

    </div>

</main>

<jsp:include page="../common/footer.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>


</body>

</html>