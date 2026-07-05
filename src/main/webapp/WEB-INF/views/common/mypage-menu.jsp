<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

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
            <strong>
                <c:choose>
                    <c:when test="${not empty myPageHome}">
                        ${myPageHome.recentOrderCount}건
                    </c:when>
                    <c:otherwise>
                        0건
                    </c:otherwise>
                </c:choose>
            </strong>
            <span>최근 주문</span>
        </div>
        <div class="summary-item">
            <strong>
                <c:choose>
                    <c:when test="${not empty myPageHome}">
                        ${myPageHome.cartItemCount}개
                    </c:when>
                    <c:otherwise>
                        0개
                    </c:otherwise>
                </c:choose>
            </strong>
            <span>장바구니</span>
        </div>
    </div>

</section>