<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>회원가입</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${contextPath}/css/bookshop.css">
</head>
<body>

<jsp:include page="../common/header.jsp"/>

<main class="py-5 bg-light">

    <div class="container">

        <div class="mx-auto bg-white border rounded-3 p-4 p-md-5">
            <h2 class="h4 fw-bold mb-2">회원가입</h2>
            <p class="text-muted mb-3">
                BookShop 이용을 위한 회원 정보를 입력해주세요.
            </p>
            
            <form id="signupForm" action="${contextPath}/member/signup"
                  method="post">



                <!-- 기본 정보 -->
                <div class="mb-4">
                    <h3 class="h6 fw-bold border-bottom pb-2 mb-3">
                        기본 <정보></정보>
                    </h3>

                    <!-- 아이디 -->
                    <div class="mb-3">
                        <label class="form-label fw-semibold" for="loginId">
                            아이디 <span class="text-danger">*</span>
                        </label>
                        

                        <div class="d-flex align-items-center gap-2">
                            <input type="text" name="loginId" id="loginId"
                                class="form-control"
                                placeholder="아이디를 입력해주세요."
                                value="${member.loginId}">
                            <button type="button"
                                    id="loginIdCheckBtn"
                                    class="btn btn-outline-dark rounded-3 px-3 flex-shrink-0">
                                중복 확인
                            </button>
                        </div>

                        <div id="loginCheckMessage" class="small mt-1"></div>
                        <input type="hidden"
                               id="loginIdChecked"
                               name="loginIdChecked"
                               value="false">

                            
                        
                    </div>

                    <div class="form-text">
                        영문, 숫자를 조합하여 입력해주세요.
                    </div>
                </div>

                <!-- 비밀번호 -->
                <div class="mb-4">
                    <label class="form-label fw-semibold" for="password">
                        비밀번호 <span class="text">*</span>
                    </label>

                    <input type="password"
                        id="password"
                        name="password"
                        placeholder="비밀번호를 입력해주세요."
                        class="form-control"
                    >
                    <div>
                        8자 이상, 영문/숫자/특수문자를 포함해주세요.
                    </div>

                </div>

                <!-- 비밀번호 확인 -->
                <div class="mb-4">
                    <label class="form-label fw-semibold" for="checkPassword">
                        비밀번호 확인<span class="text-danger">*</span>
                    </label>

                    <input type="password"
                        id="checkPassword"
                        name="checkPassword"
                        placeholder="비밀번호를 다시 입력해주세요."
                        class="form-control"
                    >
                </div>

                <!-- 이름  -->
                <div class="mb-4">
                    <label class="form-label fw-semibold" for="name">
                        이름 <span class="text-danger">*</span>
                    </label>
                    <input type="text"
                        id="name"
                        name="name"
                        placeholder="이름을 입력해주세요."
                        class="form-control"
                        value="${member.name}">
                </div>

                <!-- 이메일 -->
                <div class="mb-4">
                    <label class="form-label fw-semibold" for="email">
                        이메일 <span class="text-danger">*</span>
                    </label>
                
                    <div class="d-flex align-items-center gap-2">
                        
                        <input type="email" name="email" id="email"
                            placeholder="example@email.com"
                            class="form-control"
                            value="${member.email}">
                            
                        
                        <button id="emailCheckBtn" type="button" class="btn btn-outline-dark rounded-3 px-3 flex-shrink-0">
                            중복 확인
                        </button>
                    </div>
                    <div id="emailCheckMessage" class="small mt-1"></div>
                    <input type="hidden"
                           id="emailChecked"
                           name="emailChecked"
                           value="false">
                    
                    
                </div>

                <!-- 휴대폰 -->
                <div class="mb-4">
                    <label class="form-label fw-semibold" for="phone">
                        휴대폰 번호 <span class="text-danger">*</span>
                    </label>

                    <input type="text"
                    id="phone"
                    name="phone"
                    placeholder="010-1234-5678"
                    class="form-control"
                    value="${member.phone}">
                </div>

                <!-- 주소 정보 -->

                <div class="mb-4">
                    <h3 class="h6 fw-bold border-bottom pb-2 mb-3">주소 정보</h3>

                    <!-- 우편 번호 -->
                    <div class="mb-3">
                        <label class="form-label fw-semibold" for="zipcode">
                            우편번호 <span class="text-danger">*</span>
                        </label>

                        <div class="d-flex align-items-center gap-2">

                            <input type="text" name="zipcode" id="zipcode"
                            placeholder="우편번호" 
                            class="form-control"
                            value="${member.zipcode}"
                                   readonly>
                            <button type="button"
                                    class="btn btn-outline-dark rounded-3 px-3 flex-shrink-0"
                                    onclick="openPostcode()"
                            >

                                주소 찾기
                            </button>
                        </div>
                    </div>
                    
                    <!-- 기본 주소 -->
                    <div class="mb-3">
                        <label class="form-label fw-semibold" for="address">
                            기본 주소 <span class="text-danger">*</span>
                        </label>
                        <input type="text" name="address" id="address"
                        placeholder="기본 주소" 
                        class="form-control"
                        value="${member.address}"
                               readonly>
                    </div>
                    <!-- 상세 주소 -->
                    <div class="mb3">
                        <label class="form-label fw-semibold" for="addressDetail">
                            상세 주소 <span class="text-danger">*</span>
                        </label>
                        <input type="text" name="addressDetail" id="addressDetail"
                        placeholder="상세 주소"
                        class="form-control"
                        value="${member.addressDetail}">
                    </div>
                </div>
                
                

                <!-- 버튼 -->
                <div class="d-flex justify-content-center gap-3 mt-5">
                    <button type="submit" class="btn btn-primary px-5 py-2 rounded-3">가입하기</button>
                    
                    <button type="button" class="btn btn-outline-secondary px-4 py-2 rounded-3">
                        취소
                    </button>
                </div>


            </form>
        </div>

        

    </div>

    <div id="customAlert" class="custom-alert">

        <div class="custom-alert-box">
            <p id="customAlertMessage" class="mb-3"></p>
            <button type="button" id="customAlertClose" class="btn btn-primary px-4"></button>
        </div>

    </div>


</main>

<jsp:include page="../common/footer.jsp"/>




<script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>

<script>
    const contextPath = "${contextPath}";
</script>

<script src="${contextPath}/js/member/signup.js"></script>
<c:if test="${not empty errorMessage}">
    <script>
        window.addEventListener("DOMContentLoaded", function () {
            alert("${errorMessage}");
        });
    </script>
</c:if>
</body>
</html>