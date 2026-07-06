<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>시스템 오류 | BookShop</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${contextPath}/css/bookshop.css" rel="stylesheet">
</head>

<body class="bg-light">

<div class="container py-5">
    <div class="row justify-content-center">

        <div class="col-md-7 col-lg-6">
            <div class="card border-0 shadow-sm rounded-4">
                <div class="card-body text-center p-5">
                    <h1 class="fw-bold mb-3">
                        문제가 발생하였습니다.
                    </h1>

                    <p class="text-muted mb-4">
                        요청을 처리하는 중 일시적인 오류가 발생하였습니다.<br>
                        잠시 후 다시 시도해주세요.
                    </p>

                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-light border text-muted small mb-4">
                            ${errorMessage}
                        </div>
                    </c:if>

                    <div class="d-flex justify-content-center gap-2">
                        <a href="${contextPath}/" class="btn btn-primary">홈으로</a>

                        <button type="button"
                                class="btn btn-outline-secondary"
                                onclick="history.back()">
                            이전으로
                        </button>
                    </div>
                </div>
            </div>
        </div>

    </div>
</div>


</body>

</html>