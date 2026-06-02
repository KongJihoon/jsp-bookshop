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

    <section class="mypage-title">
        <h2>마이페이지</h2>
        <p>회원님의 쇼핑 정보와 회원정보를 확인할 수 있습니다.</p>
    </section>

    <section class="member-summary">

        <div>
            <h3>${sessionScope.loginMember.name}님 안녕하세요.</h3>
            <p>BookShop을 이용해 주셔서 감사합니다.</p>
        </div>

        <div class="summary-list">
            <div class="summary-item">
                <strong>0건</strong>
                <span>최근 주문</span>
            </div>
            <div class="summary-item">
                <strong>0개</strong>
                <span>장바구니</span>
            </div>
        </div>

    </section>


    <div class="mypage-layout">

        <aside class="mypage-sidebar">
            <h3>MY PAGE</h3>

            <div class="menu-group">
                <h4>쇼핑정보</h4>
                <ul>
                    <li><a href="#">주문배송 조회</a></li>
                    <li><a href="#">주문내역</a></li>
                </ul>
            </div>

            <div class="menu-group">
                <h4>회원정보</h4>
                <ul>
                    <li><a href="${contextPath}/member/info">회원정보 조회</a></li>
                    <li><a href="#">회원정보 수정</a></li>
                    <li><a href="#">회원탈퇴</a></li>
                </ul>

            </div>

        </aside>

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