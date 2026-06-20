<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>도서 목록 | BookShop</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link rel="stylesheet"
          href="${contextPath}/css/bookshop.css">
</head>

<body>

<jsp:include page="../common/header.jsp"/>

<main class="bg-white py-5">

    <div class="container">

    <%-- 경로 --%>
        <div class="mb-4 small text-muted">
            홈 &gt; ${product.categoryName} &gt; ${product.name}
        </div>

        <!-- 상품 상단 영역 -->
        <section class="card shadow-sm border-0 rounded-4 mb-5">
            <div class="card-body p-4 p-md-5">

                <div class="row g-5">

                    <!-- 왼쪽 : 이미지 영역 -->
                    <div class="col-md-5">

                        <div class="bg-white border rounded-4 d-flex justify-content-center align-items-center"
                        style="height: 520px; overflow: hidden;">
                            <c:set var="hasThumbnail" value="false"/>

                            <c:forEach var="image" items="${product.images}">
                                <c:if test="${image.imageType == 'THUMBNAIL'}">
                                    <c:set var="hasThumbnail" value="true"/>

                                    <img src="${contextPath}${image.imagePath}" alt="${product.name}"
                                    class="img-fluid"
                                    style="width: 100%; height: 100%; object-fit: cover;">

                                </c:if>

                            </c:forEach>

                            <c:if test="${not hasThumbnail}">
                                <span>도서 이미지</span>
                            </c:if>
                        </div>

                    </div>

                    <!-- 오른쪽: 상품 정보 -->
                    <div class="col-md-7">
                        <div class="border-bottom pb-4 mb-4">
                            <p class="text-primary fw-semibold mb-2">${product.categoryName}</p>

                            <h2 class="fw-bold mb-3">${product.name}</h2>

                            <p class="text-muted mb-0">${product.author} 지음 · ${product.publisher}</p>
                        </div>

                        <div class="mb-4">
                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <span class="text-muted">판매가</span>

                                <strong class="fs-4">
                                    <fmt:formatNumber value="${product.price}" pattern="#,###"/>원
                                </strong>
                            </div>

                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <span class="text-muted">배송 안내</span>

                                <span>무료 배송</span>
                            </div>

                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <span class="text-muted">판매 상태</span>

                                <c:choose>
                                    <c:when test="${product.status == 'ACTIVE'}">
                                        <span class="badge bg-success">판매중</span>
                                    </c:when>

                                    <c:otherwise>
                                        <span class="badge bg-secondary">품절</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                        <div class="bg-light rounded-4 p-4 mb-4">
                            <p class="fw-semibold mb-2">구매 전 안내</p>

                            <p class="text-muted small mb-0">장바구니 및 주문 기능은 준비 중입니다.</p>
                        </div>

                        <form id="cartAddForm" action="${contextPath}/cart/items" method="post">

                            <input type="hidden" name="productId" value="${product.productId}">

                            <div class="bg-light rounded-4 p-4 mb-4">
                                <div class="d-flex justify-content-between align-items-center mb-3">
                                    <span class="fw-semibold">수량</span>

                                    <div class="quantity-control">
                                        <button type="button"
                                        id="decreaseQuantity"
                                        class="quantity-btn"
                                        aria-label="수량 감소">
                                            -
                                        </button>
                                        <span id="quantityText" class="quantity-value">
                                            1
                                        </span>
                                        <button type="button"
                                        id="increaseQuantity"
                                        class="quantity-btn"
                                        aria-label="수량 증가">
                                            +
                                        </button>
                                    </div>

                                    <input type="hidden" id="quantity" name="quantity" value="1">

                                </div>
                                <div class="d-flex justify-content-between align-items-center">
                                    <span class="text-muted">총 상품금액</span>

                                    <strong id="totalPrice" class="fs-5 text-primary">
                                        <fmt:formatNumber value="${product.price}" pattern="#,###"/>원
                                    </strong>
                                </div>
                            </div>

                            <div class="d-grid gap-2 d-md-flex">
                                <c:choose>
                                    <c:when test="${empty sessionScope.loginMember}">
                                        <button type="button"
                                        class="btn btn-primary btn-lg flex-fill"
                                        data-bs-toggle="modal"
                                        data-bs-target="#commonConfirmModal"
                                        data-title="로그인이 필요합니다."
                                        data-message="장바구니는 로그인 후 이용하실 수 있습니다. 로그인하시겠습니까?"
                                        data-confirm-text="로그인"
                                        data-confirm-class="btn-primary"
                                        data-action-type="redirect"
                                        data-url="${contextPath}/member/login">
                                            장바구니 담기
                                        </button>
                                    </c:when>
                                    <c:otherwise>
                                        <button type="button"
                                        class="btn btn-primary btn-lg flex-fill"
                                        data-bs-toggle="modal"
                                        data-bs-target="#commonConfirmModal"
                                        data-title="장바구니 담기"
                                        data-message="선택한 상품을 장바구니에 담으시겠습니까?"
                                        data-confirm-text="담기"
                                        data-confirm-class="btn-primary"
                                        data-action-type="submit"
                                        data-form-id="cartAddForm">
                                            장바구니 담기
                                        </button>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                        </form>


                    </div>

                </div>



            </div>

        </section>

        <!-- 상세 정보 탭 -->

        <section class="card shadow-sm border-0 rounded-4 mb-4">
            <div class="card-header bg-white border-bottom">
                <ul class="nav nav-tabs card-header-tabs">
                    <li class="nav-item">
                        <a class="nav-link active" href="#description">상품 정보</a>
                    </li>

                    <li class="nav-item">
                        <a class="nav-link" href="#detailImages">상세 이미지</a>
                    </li>
                </ul>
            </div>

            <div id="description" class="card-body p-4 p-md-5">
                <h2 class="h4 fw-bold mb-4">도서 소개</h2>

                <div class="text-secondary lh-lg" style="white-space: pre-line;">
                    ${product.description}
                </div>
            </div>

        </section>

        <section id="detailImages" class="card shadow-sm border-0 rounded-4">
            <div class="card-header bg-white fw-bold py-3">
                상세 이미지
            </div>

            <div class="card-body p-4 p-md-5">
                <c:set var="detailImageCount" value="0"/>

                <c:forEach var="image" items="${product.images}">
                    <c:if test="${image.imageType == 'DETAIL'}">

                        <c:set var="detailImageCount" value="${detailImageCount + 1}"/>

                        <div class="text-center mb-4">
                            <img class="img-fluid rounded-4 border" src="${contextPath}${image.imagePath}" alt="상세 이미지 ${detailImageCount}">
                        </div>

                    </c:if>
                </c:forEach>

                <c:if test="${detailImageCount == 0}">
                    <div class="text-center text-muted py-5">등록된 상세 이미지가 없습니다.</div>
                </c:if>

            </div>
        </section>

    </div>


</main>
<jsp:include page="../common/confirm-modal.jsp"/>
<jsp:include page="../common/footer.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js">
</script>

<script>
    const quantityInput = document.getElementById("quantity");
    const quantityText = document.getElementById("quantityText");
    const decreaseButton = document.getElementById("decreaseQuantity");
    const increaseButton = document.getElementById("increaseQuantity");
    const totalPriceElement = document.getElementById("totalPrice");

    const productPrice = Number("${product.price}");
    const minQuantity = 1;

    function updateQuantity(nextQuantity) {
        let quantity = Number(nextQuantity);

        if (Number.isNaN(quantity) || quantity < minQuantity) {
            quantity = minQuantity;
        }

        quantityInput.value = quantity;
        quantityText.textContent = quantity;
        decreaseButton.disabled = quantity <= minQuantity;

        const totalPrice = productPrice * quantity;
        totalPriceElement.textContent = totalPrice.toLocaleString("ko-KR") + "원";

    }

    decreaseButton.addEventListener("click", function () {
        updateQuantity(Number(quantityInput.value) - 1);
    })

    increaseButton.addEventListener("click", function () {
        updateQuantity(Number(quantityInput.value) + 1);
    })

    updateQuantity(quantityInput.value);


</script>

</body>

</html>