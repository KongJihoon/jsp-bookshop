<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>주문서 | BookShop</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link rel="stylesheet"
          href="${contextPath}/css/bookshop.css">
</head>
<body>

<jsp:include page="../common/header.jsp"/>


<main class="bg-light py-5">

    <div class="container">
        <div class="mb-4">
            <h2 class="fw-bold mb-1">주문서</h2>
            <p class="text-muted mb-0">주문 상품과 배송 정보를 확인해주세요.</p>
        </div>

        <div class="row g-4">
            <div class="col-lg-8">
                <div class="card border-0 shadow-sm rounded-4 mb-4">
                    <div class="card-body p-4">
                        <h5 class="fw-bold mb-4">주문 상품</h5>
                        <c:forEach var="item" items="${orderItems}">

                            <div class="d-flex gap-3 py-3 border-bottom">
                                <a href="${contextPath}/products/${item.productId}"
                                    class="border rounded-4 bg-white d-flex align-items-center justify-content-center text-decoration-none"
                                    style="width: 90px; height: 120px; overflow: hidden; flex-shrink: 0;">
                                    <c:choose>

                                        <c:when test="${not empty item.imagePath}">
                                            <img src="${contextPath}${item.imagePath}" alt="${item.productName}"
                                                class="img-fluid"
                                                style="width: 100%; height: 100%; object-fit: cover;">
                                        </c:when>

                                        <c:otherwise>
                                            <span class="text-muted small">도서 이미지</span>
                                        </c:otherwise>
                                    </c:choose>
                                </a>

                                <div class="flex-grow-1">
                                    <h6 class="fw-bold mb-2">${item.productName}</h6>
                                    <p class="text-muted small mb-2">${item.author} 지음 · ${item.publisher}</p>

                                    <div class="d-flex justify-content-between align-items-end">
                                        <div>
                                            <p class="mb-1">
                                                <span class="text-muted">수량</span>
                                                <span class="fw-semibold">${item.quantity}개</span>
                                            </p>
                                            <p class="mb-0">
                                                <fmt:formatNumber value="${item.price}" pattern="#,###"/>원
                                            </p>
                                        </div>
                                        <strong class="text-primary">
                                            <fmt:formatNumber value="${item.itemTotalPrice}" pattern="#,###"/>원
                                        </strong>
                                    </div>
                                </div>

                            </div>

                        </c:forEach>
                    </div>
                </div>

                <div class="card border-0 shadow-sm rounded-4">
                    <div class="card-body p-4">
                        <h5 class="fw-bold mb-4">배송 정보</h5>
                        <form id="orderCreateForm"
                              action="${contextPath}/orders"
                              method="post">

                            <c:forEach var="item" items="${orderItems}">
                                <input type="hidden"
                                    name="cartItemIds"
                                    value="${item.cartItemId}">
                            </c:forEach>

                            <div class="mb-3">
                                <label for="receiverName" class="form-label fw-semibold">받는 사람 <span class="text-danger">*</span></label>
                                <input type="text"
                                    id="receiverName"
                                    name="receiverName"
                                       value="${orderCreateRequest.receiverName}"
                                       class="form-control"
                                    required>
                            </div>

                            <div class="mb-3">
                                <label for="receiverPhone" class="form-label fw-semibold">
                                    연락처 <span class="text-danger">*</span>
                                </label>
                                <input type="text"
                                    id="receiverPhone"
                                    name="receiverPhone"
                                       value="${orderCreateRequest.receiverPhone}"
                                       class="form-control"
                                    placeholder="010-1234-5678"
                                    required>
                            </div>

                            <div class="mb-3">
                                <label for="zipcode" class="form-label fw-semibold">
                                    우편번호 <span class="text-danger">*</span>
                                </label>

                                <div class="d-flex gap-2">
                                    <input type="text"
                                        id="zipcode"
                                        name="zipcode"
                                        placeholder="우편번호"
                                           value="${orderCreateRequest.zipcode}"
                                           class="form-control zipcode-input"
                                        readonly
                                        required>
                                    <button type="button"
                                        class="btn btn-outline-primary address-search-btn flex-shrink-0"
                                        onclick="openPostcode()">
                                        주소 검색
                                    </button>
                                </div>
                            </div>
                            <div class="mb-3">
                                <label for="address" class="form-label fw-semibold">기본 주소 <span class="text-danger">*</span></label>
                                <input type="text"
                                       id="address"
                                       name="address"
                                       value="${orderCreateRequest.address}"
                                       placeholder="기본 주소"
                                       class="form-control"
                                       readonly
                                       required>
                            </div>

                            <div>
                                <label for="addressDetail" class="form-label fw-semibold">
                                    상세 주소 <span class="text-danger">*</span>
                                </label>
                                <input type="text"
                                       id="addressDetail"
                                       name="addressDetail"
                                       value="${orderCreateRequest.addressDetail}"
                                       placeholder="상세 주소를 입력하세요."
                                       class="form-control"
                                       required>

                            </div>


                        </form>

                    </div>
                </div>

            </div>

            <div class="col-lg-4">
                <div class="card border-0 shadow-sm rounded-4 position-sticky"
                    style="top: 120px;">
                    <div class="card-body p-4">
                        <h5 class="fw-bold mb-4">결제 예정 금액</h5>
                        <div class="d-flex justify-content-between mb-3">
                            <span class="text-muted">상품 금액</span>
                            <span>
                                <fmt:formatNumber value="${orderForm.totalPrice}" pattern="#,###"/>원
                            </span>
                        </div>
                        <hr/>

                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <span class="fw-bold">총 결제 금액</span>
                            <strong class="fs-4 text-primary">
                                <fmt:formatNumber value="${orderForm.totalPrice}" pattern="#,###"/>원
                            </strong>
                        </div>

                        <button type="submit"
                            class="btn btn-primary btn-lg w-100"
                            data-bs-toggle="modal"
                            data-bs-target="#commonConfirmModal"
                            data-title="주문 확인"
                            data-message="선택한 상품을 주문하시겠습니까?"
                            data-confirm-text="주문하기"
                            data-confirm-class="btn-primary"
                            data-action-type="submit"
                            data-form-id="orderCreateForm">
                            주문 하기
                        </button>

                        <a href="${contextPath}/cart"
                            class="btn btn-light btn-lg w-100 mt-2">
                            장바구니로 돌아가기
                        </a>
                    </div>
                </div>
            </div>

        </div>
    </div>


</main>
<jsp:include page="../common/confirm-modal.jsp"/>
<jsp:include page="../common/footer.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js">
</script>

<script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js">
</script>

<script>
    function openPostcode() {
        new daum.Postcode({
            oncomplete: function (data) {
                document.getElementById("zipcode").value = data.zonecode;
                document.getElementById("address").value = data.address;
                document.getElementById("addressDetail").focus();
            }
        }).open();
    }
</script>

</body>

</html>