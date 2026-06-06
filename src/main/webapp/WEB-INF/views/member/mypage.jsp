<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>로그인</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${contextPath}/css/bookshop.css">
    <link rel="stylesheet" href="${contextPath}/css/mypage.css">
</head>
<body>

<jsp:include page="../common/header.jsp"/>

<main class="mypage">


    <jsp:include page="../common/mypage-menu.jsp"/>


    <div class="mypage-layout">

        <jsp:include page="../common/mypage-sidebar.jsp"/>


        <section class="mypage-content">

            <article class="content-section">
                <div class="section-header">
                    <h3>최근 주문정보</h3>
                    <a href="#">더보기</a>
                </div>
                <div class="empty-box">최근 주문 내역이 없습니다.</div>
            </article>

            <article class="content-section">
                <div class="section-header">
                    <h3>장바구니 상품</h3>
                    <a href="#">더보기</a>
                </div>
                <div class="empty-box">장바구니에 담긴 상품이 없습니다.</div>
            </article>

            <article class="content-section">
                <div class="section-header">
                    <h3>회원정보</h3>
                    <a href="#">조회하기</a>
                </div>

                <div class="info-box">
                    회원정보, 조회, 수정, 탈퇴 메뉴를 이용할 수 있습니다.
                </div>
            </article>

        </section>

    </div>


</main>



<jsp:include page="../common/footer.jsp"/>


</body>