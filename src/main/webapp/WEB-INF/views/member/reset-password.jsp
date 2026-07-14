<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>비밀번호 재설정</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">
    <link rel="stylesheet" href="${contextPath}/css/bookshop.css">
</head>

<body>
<jsp:include page="../common/header.jsp"/>

<main class="py-5 bg-light">

    <div class="container">

        <div class="mx-auto bg-white border rounded-3 p-4 p-md-5"
             style="max-width: 560px;">

            <h2 class="h4 fw-bold mb-2">
                비밀번호 재설정
            </h2>

            <p class="text-muted mb-4">
                새로 사용할 비밀번호를 입력해주세요.
            </p>

            <form action="${contextPath}/member/find-password/reset" method="post">

                <div class="mb-4">
                    <label class="form-label fw-semibold" for="newPassword">
                        새 비밀번호 <span class="text-danger">*</span>
                    </label>

                    <input type="password"
                           id="newPassword"
                           name="newPassword"
                           class="form-control"
                           placeholder="새 비밀번호를 입력해주세요.">
                </div>

                <div class="mb-4">
                    <label class="form-label fw-semibold" for="confirmPassword">
                        새 비밀번호 확인 <span class="text-danger">*</span>
                    </label>

                    <input type="password"
                           id="confirmPassword"
                           name="confirmPassword"
                           class="form-control"
                           placeholder="새 비밀번호를 다시 입력해주세요.">
                </div>

                <div class="form-text mb-4">
                    8자 이상, 영문/숫자/특수문자를 포함한 비밀번호 사용을 권장합니다.
                </div>

                <div class="d-grid mt-4">
                    <button type="submit" class="btn btn-primary btn-lg">
                        비밀번호 변경
                    </button>
                </div>

                <div class="text-center mt-4 small">
                    <a href="${contextPath}/member/login"
                       class="text-secondary text-decoration-none">
                        로그인으로 돌아가기
                    </a>
                </div>

            </form>

        </div>

    </div>

</main>

<jsp:include page="../common/footer.jsp"/>

</body>
</html>