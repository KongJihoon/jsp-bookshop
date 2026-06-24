<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>주문 완료 | BookShop</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link rel="stylesheet"
          href="${contextPath}/css/bookshop.css">
</head>

<body>
<jsp:include page="../common/header.jsp"/>

<main class="bg-light py-5">
    <div class="container">

        <div class="mx-auto" style="max-width: 720px;">
            <div class="card border-0 shadow-sm rounded-4">
                <div class="card-body text-center p-5">

                    <div class="d-inline-flex align-items-center justify-content-center rounded-circle bg-primary bg-opacity-10 mb-4"
                         style="width: 72px; height: 72px;">
                        <span class="fs-2 text-primary fw-bold">✓</span>
                    </div>

                    <h2 class="fw-bold mb-3">주문이 완료되었습니다</h2>

                    <p class="text-muted mb-4">
                        주문해주셔서 감사합니다.<br>
                        주문 내역은 마이페이지에서 확인할 수 있습니다.
                    </p>

                    <div class="bg-light rounded-4 py-3 px-4 mb-4">
                        <div class="text-muted small mb-1">주문번호</div>
                        <div class="fw-bold fs-5">#${orderId}</div>
                    </div>

                    <div class="d-grid gap-2 d-sm-flex justify-content-center">
                        <a href="${contextPath}/products"
                           class="btn btn-primary px-4">
                            쇼핑 계속하기
                        </a>

                        <a href="${contextPath}/"
                           class="btn btn-outline-secondary px-4">
                            홈으로
                        </a>
                    </div>

                </div>
            </div>
        </div>

    </div>
</main>


<jsp:include page="../common/footer.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js">
</script>

</body>
</html>