<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>


<header class="site-header border-bottom">
    <!-- 상단 유틸 메뉴-->
    <div class="bg-light border-bottom">
        <div class="container d-flex justify-content-between align-items-center py-2">
            <ul class="nav small">
                <li class="nav item">
                    <a class="nav-link px-2 text-secondary" href="#">배송 안내</a>
                </li>
                <li class="nav item">
                    <a class="nav-link px-2 text-secondary" href="#">관리자 페이지</a>
                </li>

            </ul>

            <ul class="nav small">
                <c:choose>
                    <c:when test="${not empty sessionScope.loginMember}">
                        <li class="nav-item d-flex align-items-center">
                            <span class="text-secondary me-3">
                                ${sessionScope.loginMember.name}님 환영합니다.
                            </span>
                        </li>

                        <li class="nav-item">
                            <a class="nav-link px-2 text-secondary" href="${contextPath}/member/logout">로그아웃</a>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li class="nav-item">
                            <a class="nav-link px-2 text-secondary" href="${contextPath}/member/login">로그인</a>
                        </li>
                        <li class="nav item">
                            <a class="nav-link px-2 text-secondary" href="${contextPath}/member/signup">회원가입</a>
                        </li>
                    </c:otherwise>
                </c:choose>

            </ul>
        </div>
    </div>


    <!-- 로고/ 검색/ 사용자 메뉴 -->
    <div class="container py-4">
        <div class="row align-items-center g-3">
            <div class="col-md-3 text-center text-md-start">
                <a class="brand-logo text-decoration-none" href="${contextPath}/">BookShop</a>
            </div>

            <div class="col-md-6">
                <form action="#" method="get" class="input-group">
                    <input type="text" name="keyword" placeholder="도서명, 저자 검색" class="form-control">
                    <button type="submit" class="btn btn-primary">검색</button>
                </form>
            </div>

            <div class="col-md-3">
                <div class="header-user-menu d-flex justify-content-center justify-content-md-end gap-3 small">
                    <a href="${contextPath}/member/mypage" class="header-user-link">마이페이지</a>
                    <a href="#" class="header-user-link">장바구니</a>
                </div>
            </div>
        </div>
    </div>

    <!-- 카테고리 -->
    <nav class="category-nav border-top">
        <div class="container">
            <ul class="nav justify-content-center flex-nowrap overflow-auto gap-4 py-2">
                <li class="nav-item">
                    <a class="nav-link fw-bold fs-5 text-dark" href="#">베스트셀러</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link fw-bold fs-5 text-dark" href="#">신간도서</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link fw-bold fs-5 text-dark" href="#">소설</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link fw-bold fs-5 text-dark" href="#">IT·컴퓨터</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link fw-bold fs-5 text-dark" href="#">자기계발</a>
                </li>
            </ul>
        </div>

    </nav>

</header>