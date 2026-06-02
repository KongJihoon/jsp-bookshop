<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>회원정보</title>
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
            <!-- 상단 헤더: 라인 제거 및 폰트 두께 교정 -->
            <div class="d-flex justify-content-between align-items-center pb-3 mb-4 border-bottom border-1">
                <h3 class="fw-bold text-dark mb-0" style="letter-spacing: -0.5px;">회원정보 확인</h3>
                <a href="#" class="btn btn-outline-primary btn-sm px-3 py-2 rounded-pill fw-medium">
                    <i class="bi bi-pencil-square me-1"></i> 정보 수정하기
                </a>
            </div>

            <!-- 회원정보 카드 대시보드 스타일 -->
            <div class="card border-0 shadow-sm rounded-4 overflow-hidden">
                <div class="card-body p-0">

                    <!-- 항목 1: 아이디 -->
                    <div class="row align-items-center py-3 px-4 m-0 border-bottom bg-light bg-opacity-25">
                        <div class="col-sm-3 fw-semibold text-secondary mb-1 mb-sm-0">아이디</div>
                        <div class="col-sm-9 text-dark fw-medium">${member.loginId}</div>
                    </div>

                    <!-- 항목 2: 비밀번호 -->
                    <div class="row align-items-center py-3 px-4 m-0 border-bottom">
                        <div class="col-sm-3 fw-semibold text-secondary mb-1 mb-sm-0">비밀번호</div>
                        <div class="col-sm-9 text-muted">
                            <span class="me-2">••••••••</span>
                            <small class="text-secondary bg-light px-2 py-1 rounded border" style="font-size: 0.75rem;">보안 유지중</small>
                        </div>
                    </div>

                    <!-- 항목 3: 이름 -->
                    <div class="row align-items-center py-3 px-4 m-0 border-bottom bg-light bg-opacity-25">
                        <div class="col-sm-3 fw-semibold text-secondary mb-1 mb-sm-0">이름</div>
                        <div class="col-sm-9 text-dark fw-medium">${member.name}</div>
                    </div>

                    <!-- 항목 4: 이름 -->
                    <div class="row align-items-center py-3 px-4 m-0 border-bottom bg-light bg-opacity-25">
                        <div class="col-sm-3 fw-semibold text-secondary mb-1 mb-sm-0">이메일</div>
                        <div class="col-sm-9 text-dark fw-medium">${member.email}</div>
                    </div>

                    <!-- 항목 5: 연락처 (기존 '이메' 오타 및 'phone' 필드 매칭 수정) -->
                    <div class="row align-items-center py-3 px-4 m-0 border-bottom">
                        <div class="col-sm-3 fw-semibold text-secondary mb-1 mb-sm-0">연락처</div>
                        <div class="col-sm-9 text-dark">${member.phone}</div>
                    </div>

                    <!-- 항목 6: 배송지 정보 그룹 -->
                    <div class="row align-items-start py-4 px-4 m-0 bg-light bg-opacity-25">
                        <div class="col-sm-3 fw-semibold text-secondary mb-2 mb-sm-0 pt-sm-1">배송지 주소</div>
                        <div class="col-sm-9">
                            <div class="d-flex flex-column gap-2">
                                <div>
                            <span class="badge bg-secondary bg-opacity-10 text-secondary border border-secondary border-opacity-25 px-2 py-1.5 fw-medium">
                                우편번호 ${member.zipcode}
                            </span>
                                </div>
                                <div class="text-dark fw-medium mt-1">${member.address}</div>
                                <div class="text-secondary small">${member.addressDetail}</div>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
        </section>



    </div>


</main>



<jsp:include page="../common/footer.jsp"/>


</body>