<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>비밀번호 인증번호 확인</title>

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
                인증번호 확인
            </h2>

            <p class="text-muted mb-3">
                이메일로 발송된 인증번호를 입력해주세요.
            </p>

            <div class="bg-light border rounded-3 p-3 mb-4">
                <div class="d-flex justify-content-between align-items-center">
                    <span class="text-muted small">
                        인증번호 유효 시간
                    </span>

                    <span id="timer" class="fw-bold text-danger">
                        3:00
                    </span>
                </div>
            </div>

            <form action="${contextPath}/member/find-password/verify" method="post">

                <div class="mb-4">
                    <label class="form-label fw-semibold" for="code">
                        인증번호 <span class="text-danger">*</span>
                    </label>

                    <input type="text"
                           id="code"
                           name="code"
                           class="form-control"
                           placeholder="인증번호 6자리를 입력해주세요."
                           maxlength="6"
                           value="${passwordVerifyRequest.code}">
                </div>

                <div class="d-grid mt-4">
                    <button type="submit" class="btn btn-primary btn-lg">
                        인증번호 확인
                    </button>
                </div>

                <div class="text-center mt-4 small">
                    <a href="${contextPath}/member/find-password"
                       class="text-secondary text-decoration-none">
                        처음부터 다시 진행하기
                    </a>
                </div>

            </form>

        </div>

    </div>

</main>

<jsp:include page="../common/footer.jsp"/>

<script>
    let remainingSeconds = Number("${ttlSeconds}");

    if (Number.isNaN(remainingSeconds) || remainingSeconds <= 0) {
        remainingSeconds = 0;
    }

    const timerElement = document.getElementById("timer");
    const submitButton = document.getElementById("verifyButton");

    function renderTimer() {
        const minutes = Math.floor(remainingSeconds / 60);
        const seconds = remainingSeconds % 60;

        timerElement.textContent =
            String(minutes).padStart(2, "0") + ":" + String(seconds).padStart(2, "0");

        if (remainingSeconds <= 0) {
            timerElement.textContent = "만료";
            timerElement.classList.remove("text-danger");
            timerElement.classList.add("text-secondary");

            if (submitButton) {
                submitButton.disabled = true;
            }

            return;
        }

        remainingSeconds -= 1;
        setTimeout(renderTimer, 1000);
    }

    renderTimer();
</script>

</body>

</html>

