<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>아이디 찾기</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">
    <link rel="stylesheet" href="${contextPath}/css/bookshop.css">
</head>

<body>
<jsp:include page="../common/header.jsp"/>

<jsp:include page="../common/toast.jsp"/>


<main class="py-5 bg-light">
    <div class="py-5 bg-white">

        <div class="container">

            <div class="mx-auto bg-white border rounded-3 p-4 p-md-5"
                style="max-width: 560px;">
                <h2 class="h4 fw-bold mb-2">
                    아이디 찾기
                </h2>

                <p class="text-muted mb-4">가입 시 입력한 이름, 이메일, 휴대폰번호를 입력해주세요.</p>

                <form action="${contextPath}/member/find-id" method="post">
                    <div class="mb-4">
                        <label class="form-label fw-semibold" for="name">이름 <span class="text-danger">*</span></label>

                        <input type="text"
                               id="name"
                               name="name"
                               class="form-control"
                               placeholder="이름을 입력해주세요."
                               value="${memberFindIdRequest.name}">
                    </div>

                    <div class="mb-4">
                        <label class="form-label fw-semibold"
                               for="email">
                            이메일<span class="text-danger">*</span>
                        </label>
                        <input type="email"
                               id="email"
                               name="email"
                               class="form-control"
                               placeholder="example@email.com"
                               value="${memberFindIdRequest.email}">

                    </div>

                    <div class="mb-4">
                        <label class="form-label" for="phone">
                            휴대폰 번호<span class="text-danger">*</span>
                        </label>

                        <input type="text"
                               id="phone"
                               name="phone"
                               class="form-control"
                               placeholder="010-1234-5678"
                               value="${memberFindIdRequest.phone}">
                    </div>

                    <div class="d-grid mt-4">
                        <button type="submit" class="btn btn-primary btn-lg">
                            아이디 찾기
                        </button>
                    </div>

                    <div class="d-flex justify-content-center gap-3 mt-4 small">

                        <a href="${contextPath}/member/login" class="text-secondary text-decoration-none">
                            로그인
                        </a>

                        <span class="text-muted">|</span>
                        <a href="${contextPath}/member/find-password"
                           class="text-secondary text-decoration-none">
                            비밀번호 찾기
                        </a>

                        <span class="text-muted">|</span>

                        <a href="${contextPath}/member/signup" class="text-secondary text-decoration-none">
                            회원가입
                        </a>
                    </div>
                </form>

            </div>

        </div>

    </div>


</main>

<jsp:include page="../common/footer.jsp"/>

</body>


</html>