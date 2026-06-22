<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>장바구니 | BookShop</title>

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
            <h2 class="fw-bold mb-1">장바구니</h2>
            <p class="text-muted mb-0">주문할 상품과 수량을 확인해주세요.</p>
        </div>

        <c:choose>
            <c:when test="${empty cartItems}">
                <div class="card border-0 shadow-sm rounded-4">
                    <div class="card-body text-center py-5">
                        <p class="text-muted mb-4">장바구니에 담긴 상품이 없습니다.</p>
                        <a class="btn btn-primary" href="${contextPath}/products">쇼핑 계속하기</a>
                    </div>
                </div>
            </c:when>

            <c:otherwise>

                <div class="row g-4">
                    <!-- 장바구니 상품 목록 -->
                    <div class="col-lg-8">

                        <!-- 전체 선택 -->
                        <div class="card border-0 shadow-sm rounded-4 mb-3">
                            <div class="card-body py-3 px-4">
                                <div class="form-check mb-0">
                                    <input type="checkbox"
                                        id="selectAllCartItems"
                                        class="form-check-input"
                                        checked>

                                    <label for="selectAllCartItems">전체 선택</label>
                                </div>
                            </div>
                        </div>

                        <c:forEach var="cartItem" items="${cartItems}">

                            <div class="card border-0 shadow-sm rounded-4 mb-3 cart-item-card"
                                data-price="${cartItem.price}"
                                data-cart-item-id="${cartItem.cartItemId}">
                                <div class="card-body p-4">

                                    <div class="d-flex gap-3">
                                        <!-- 개별선택 -->
                                        <div class="pt-2">
                                            <input type="checkbox"
                                                class="form-check-input cart-item-checkbox"
                                                value="${cartItem.cartItemId}"
                                                checked>
                                        </div>

                                        <!-- 상품 이미지 -->
                                        <a href="${contextPath}/products/${cartItem.productId}"
                                           class="border rounded-4 bg-white d-flex align-items-center justify-content-center text-decoration-none"
                                           style="width: 120px; height: 160px; overflow: hidden; flex-shrink: 0;">

                                            <c:choose>
                                                <c:when test="${not empty cartItem.imagePath}">
                                                    <img src="${contextPath}${cartItem.imagePath}" alt="${cartItem.productName}"
                                                        class="img-fluid"
                                                        style="width: 100%; height: 100%; object-fit: cover">
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-muted small">도서 이미지</span>
                                                </c:otherwise>
                                            </c:choose>

                                        </a>
                                        <!-- 상품 정보 -->
                                        <div class="flex-grow-1">
                                            <div class="d-flex justify-content-between gap-3">
                                                <div>
                                                    <a href="${contextPath}/products/${cartItem.productId}"
                                                        class="text-decoration-none text-dark">
                                                        <h5 class="fw-bold mb-2">${cartItem.productName}</h5>
                                                    </a>

                                                    <p class="text-muted small mb-3">${cartItem.author} 지음 · ${cartItem.publisher}</p>

                                                    <p class="fw-bold mb-0">
                                                        <fmt:formatNumber value="${cartItem.price}" pattern="#,###"/>원
                                                    </p>
                                                </div>

                                                <button type="button" class="btn-close" aria-label="장바구니 상품 삭제">

                                                </button>
                                            </div>
                                            <!-- 수량 / 상품 금액-->
                                            <div class="d-flex justify-content-between align-items-start mt-4">
                                                <div>
                                                    <p class="form-label small text-muted mb-2">수량</p>

                                                    <div class="quantity-control">
                                                        <button type="button"
                                                            class="quantity-btn cart-quantity-decrease"
                                                            aria-label="수량 감소">
                                                            -
                                                        </button>

                                                        <span class="quantity-value cart-quantity-value">${cartItem.quantity}</span>

                                                        <button type="button"
                                                            class="quantity-btn cart-quantity-increase">
                                                            +
                                                        </button>
                                                    </div>
                                                    <input type="hidden" value="${cartItem.quantity}" class="cart-quantity-input">

                                                </div>

                                                <div class="text-end">
                                                    <p class="text-muted small mb-1">상품 금액</p>
                                                    <strong class="fs-5 text-primary cart-item-total-price">
                                                        <fmt:formatNumber value="${cartItem.itemTotalPrice}" pattern="#,###"/>원
                                                    </strong>
                                                </div>
                                            </div>
                                        </div>
                                    </div>


                                </div>
                            </div>


                        </c:forEach>


                    </div>

                    <!-- 주문 요약 -->
                    <div class="col-lg-4">
                        <div class="card border-0 shadow-sm rounded-4 position-sticky"
                            style="top: 120px">
                            <div class="card-body p-4">
                                <h5 class="fw-bold mb-4">주문 예상 금액</h5>

                                <div class="d-flex justify-content-between mb-3">
                                    <span class="text-muted">선택 상품 금액</span>

                                    <span id="selectedTotalPriceText">
                                        <fmt:formatNumber value="${cart.totalPrice}" pattern="#,###"/>원
                                    </span>
                                </div>

                                <hr/>

                                <div class="d-flex justify-content-between align-items-center mb-4">
                                    <span class="fw-bold">총 결제 예정 금액</span>

                                    <strong id="paymentTotalPriceText" class="fs-4 text-primary">
                                        <fmt:formatNumber value="${cart.totalPrice}" pattern="#,###"/>원
                                    </strong>
                                </div>

                                <button type="button"
                                    class="btn btn-primary btn-lg w-100">
                                    선택 상품 주문하기
                                </button>

                                <a href="${contextPath}/products"
                                    class="btn btn-light btn-lg w-100 mt-2">
                                    쇼핑 계속하기
                                </a>


                            </div>
                        </div>
                    </div>


                </div>



            </c:otherwise>


        </c:choose>


    </div>



</main>
<jsp:include page="../common/footer.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js">
</script>

<script>
    const selectedAllCheckBox = document.getElementById("selectAllCartItems");
    const itemCheckboxes = document.querySelectorAll(".cart-item-checkbox");
    const cartItemCards = document.querySelectorAll(".cart-item-card");
    const selectedTotalPriceText = document.getElementById("selectedTotalPriceText");
    const paymentTotalPriceText = document.getElementById("paymentTotalPriceText");

    function formatPrice(price) {
        return price.toLocaleString("ko-KR") + "원";
    }

    function updateItemTotalPrice(card) {

        const price = Number(card.dataset.price);
        const quantityInput = card.querySelector(".cart-quantity-input");
        const quantityValue = card.querySelector(".cart-quantity-value");
        const itemTotalPrice = card.querySelector(".cart-item-total-price");
        const decreaseButton = card.querySelector(".cart-quantity-decrease");

        let quantity = Number(quantityInput.value);

        if (Number.isNaN(quantity) || quantity < 1) {
            quantity = 1;
        }

        quantityInput.value = quantity;
        quantityValue.textContent = quantity;
        decreaseButton.disabled = quantity <= 1;

        itemTotalPrice.textContent = formatPrice(price * quantity);

    }

    function updateSelectedTotalPrice() {
        let totalPrice = 0;

        cartItemCards.forEach(function (card) {
            const checkbox = card.querySelector(".cart-item-checkbox");
            if (!checkbox.checked) {
                return;
            }

            const price = Number(card.dataset.price);
            const quantity = Number(card.querySelector(".cart-quantity-input").value);

            totalPrice += price * quantity;
        })

        selectedTotalPriceText.textContent = formatPrice(totalPrice);
        paymentTotalPriceText.textContent = formatPrice(totalPrice);
    }

    function updateSelectAllState() {
        const checkedCount = document.querySelectorAll(".cart-item-checkbox:checked").length;

        selectedAllCheckBox.checked = checkedCount === itemCheckboxes.length;
    }

    function updateQuantityOnServer(card, quantity) {
        const cartItemId = card.dataset.cartItemId;

        return fetch("${contextPath}/cart/items/" + cartItemId + "/quantity", {
            method: "POST",
            headers: {
                "Content-Type" : "application/json"
            },
            body: JSON.stringify({
                quantity: quantity
            })
        }).then(function (response) {
            return response.json().then(function (data) {
                if (!response.ok) {
                    throw new Error(data.message || "수량 변경에 실패했습니다.");
                }

                return data;
            })
        })

    }



    if (selectedAllCheckBox) {
        selectedAllCheckBox.addEventListener("change", function () {
            itemCheckboxes.forEach(function (checkbox) {
                checkbox.checked= selectedAllCheckBox.checked;
            })
            updateSelectedTotalPrice();
        })
    }

    itemCheckboxes.forEach(function (checkbox) {
        checkbox.addEventListener("change", function () {
            updateSelectAllState();
            updateSelectedTotalPrice();
        })
    });

    cartItemCards.forEach(function (card) {
       const decreaseButton = card.querySelector(".cart-quantity-decrease");
       const increaseButton = card.querySelector(".cart-quantity-increase");
       const quantityInput = card.querySelector(".cart-quantity-input");

       decreaseButton.addEventListener("click", function () {
           const nextQuantity = Number(quantityInput.value) - 1;

           if (nextQuantity < 1) {
               return
           }

           updateQuantityOnServer(card, nextQuantity)
               .then(function (result) {
                   quantityInput.value = result.quantity;

                   updateItemTotalPrice(card);
                   updateSelectedTotalPrice();
               }).catch(function (error) {
                   showToast(error.message, "danger")
           })

       })

        increaseButton.addEventListener("click", function () {

            const nextQuantity = Number(quantityInput.value) + 1;

            updateQuantityOnServer(card, nextQuantity)
                .then(function (result) {
                    quantityInput.value = result.quantity;

                    updateItemTotalPrice(card);
                    updateSelectedTotalPrice();
                })

        })

        updateItemTotalPrice(card);
    });

    updateSelectedTotalPrice();

</script>

</body>

</html>