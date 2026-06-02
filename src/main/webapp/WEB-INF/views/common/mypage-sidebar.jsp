<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>


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
            <li><a href="${contextPath}/member/edit">회원정보 수정</a></li>
            <li><a href="#">회원탈퇴</a></li>
        </ul>

    </div>

</aside>