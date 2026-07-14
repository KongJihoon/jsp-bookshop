<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>비밀번호 찾기</title>

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
                비밀번호 찾기
            </h2>

            <p class="text-muted mb-4">
                가입한 아이디와 이메일을 입력하면 인증번호를 발송해드립니다.
            </p>

            <form action="${contextPath}/member/find-password/email" method="post">

                <div class="mb-4">
                    <label class="form-label fw-semibold" for="loginId">
                        아이디 <span class="text-danger">*</span>
                    </label>

                    <input type="text"
                           id="loginId"
                           name="loginId"
                           class="form-control"
                           placeholder="아이디를 입력해주세요."
                           value="${passwordFindRequest.loginId}">
                </div>

                <div class="mb-4">
                    <label class="form-label fw-semibold" for="email">
                        이메일 <span class="text-danger">*</span>
                    </label>

                    <input type="email"
                           id="email"
                           name="email"
                           class="form-control"
                           placeholder="example@email.com"
                           value="${passwordFindRequest.email}">
                </div>

                <div class="d-grid mt-4">
                    <button type="submit" class="btn btn-primary btn-lg">
                        인증번호 발송
                    </button>
                </div>

                <div class="d-flex justify-content-center gap-3 mt-4 small">

                    <a href="${contextPath}/member/login"
                       class="text-secondary text-decoration-none">
                        로그인
                    </a>

                    <span class="text-muted">|</span>

                    <a href="${contextPath}/member/find-id"
                       class="text-secondary text-decoration-none">
                        아이디 찾기
                    </a>

                    <span class="text-muted">|</span>

                    <a href="${contextPath}/member/signup"
                       class="text-secondary text-decoration-none">
                        회원가입
                    </a>

                </div>

            </form>

        </div>

    </div>

</main>

<jsp:include page="../common/footer.jsp"/>

</body>
</html>