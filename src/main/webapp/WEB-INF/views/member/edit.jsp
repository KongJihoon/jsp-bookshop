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
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>

<jsp:include page="../common/header.jsp"/>

<main class="mypage">


    <jsp:include page="../common/mypage-menu.jsp"/>


    <div class="mypage-layout">

        <jsp:include page="../common/mypage-sidebar.jsp"/>

        <section class="mypage-content">
            <!-- 상단 헤더: 라인 제거 및 폰트 두께 교정 -->
            <div class="d-flex justify-content-between align-items-center pb-3 mb-4 border-bottom border-1">
                <h3 class="fw-bold text-dark mb-0" style="letter-spacing: -0.5px;">회원정보 수정</h3>
            </div>

            <form action="${contextPath}/member/edit" method="post">
                <div class="card border-0 shadow-sm rounded-4 overflow-hidden">
                    <div class="card-body p-0">

                        <!-- 항목 1: 아이디 수정 불가-->
                        <div class="row align-items-center py-3 px-4 m-0 border-bottom bg-light bg-opacity-25">
                            <div class="col-sm-3 fw-semibold text-secondary mb-1 mb-sm-0">아이디</div>
                            <div class="col-sm-9 text-dark fw-medium">${member.loginId}</div>
                        </div>

                        <!-- 항목 2: 비밀번호 수정 불가-->
                        <div class="row align-items-center py-3 px-4 m-0 border-bottom">
                            <div class="col-sm-3 fw-semibold text-secondary mb-1 mb-sm-0">비밀번호</div>
                            <div class="col-sm-9 text-muted">
                                <span class="me-2">••••••••</span>
                                <small class="text-secondary bg-light px-2 py-1 rounded border" style="font-size: 0.75rem;">보안 유지중</small>
                            </div>
                        </div>

                        <!-- 항목 3: 이름 수정 불가-->
                        <div class="row align-items-center py-3 px-4 m-0 border-bottom bg-light bg-opacity-25">
                            <div class="col-sm-3 fw-semibold text-secondary mb-1 mb-sm-0">이름</div>
                            <div class="col-sm-9 text-dark fw-medium">${member.name}</div>
                        </div>

                        <!-- 항목 4: 이메일 -->
                        <div class="row align-items-center py-3 px-4 m-0 border-bottom bg-light bg-opacity-25">
                            <label for="email" class="col-sm-3 fw-semibold text-secondary mb-1 mb-sm-0">이메일</label>
                            <div class="col-sm-9">
                                <input type="email" id="email" name="email" class="form-control" value="${member.email}">

                            </div>
                        </div>

                        <!-- 항목 5: 연락처 (기존 '이메' 오타 및 'phone' 필드 매칭 수정) -->
                        <div class="row align-items-center py-3 px-4 m-0 border-bottom">
                            <label for="phone" class="col-sm-3 fw-semibold text-secondary mb-1 mb-sm-0">
                                연락처
                            </label>
                            <div class="col-sm-9">
                                <input type="text"
                                       id="phone"
                                       name="phone"
                                       class="form-control"
                                       value="${member.phone}"
                                       placeholder="010-1234-5678">
                            </div>
                        </div>

                        <!-- 항목 6: 배송지 정보 그룹 -->
                        <div class="row align-items-start py-4 px-4 m-0 bg-light bg-opacity-25">
                            <div class="col-sm-3 fw-semibold text-secondary mb-2 mb-sm-0 pt-sm-1">배송지 주소</div>

                            <div class="col-sm-9">

                                <div class="d-flex gap-2 mb-2">
                                    <input type="text"
                                           id="zipcode"
                                           name="zipcode"
                                           class="form-control zipcode-input"
                                           value="${member.zipcode}"
                                           readonly>

                                    <button type="button"
                                            class="btn btn-outline-primary address-search-btn"
                                            onclick="openPostcode()">
                                        주소 검색
                                    </button>
                                </div>

                                <input type="text"
                                       id="address"
                                       name="address"
                                       class="form-control mb-2"
                                       value="${member.address}"
                                       readonly>

                                <input type="text"
                                       id="addressDetail"
                                       name="addressDetail"
                                       class="form-control"
                                       value="${member.addressDetail}"
                                       placeholder="상세주소를 입력하세요">
                            </div>
                        </div>

                    </div>
                </div>
                <div class="d-flex justify-content-end gap-2 mt-4">
                    <a href="${contextPath}/member/info" class="btn btn-outline-secondary">
                        취소
                    </a>
                    <button type="submit" class="btn btn-primary">
                        저장하기
                    </button>
                </div>


            </form>
            <!-- 회원정보 카드 대시보드 스타일 -->

        </section>



    </div>


</main>



<jsp:include page="../common/footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>


</body>
<script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>

<script src="${contextPath}/js/member/edit.js"></script>

</html>