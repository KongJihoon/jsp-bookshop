<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BookShop</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${cotextPath}/css/bookshop.css">
</head>
<body>

<jsp:include page="common/header.jsp"/>


<main class="main-content py-5 bg-light">
    <div class="container">
        <section>
            <h2 class="h4 mb-3">BookShop 메인 영역</h2>
            <p class="text-muted mb-4">
                메인 베너, 추천 도서, 상품 목록은 이후 개발 예정
            </p>
        </section>
    </div>
</main>

<jsp:include page="common/footer.jsp"/>



    
</body>
</html>