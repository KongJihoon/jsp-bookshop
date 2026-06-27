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
                <form id="cartOrderForm"
                      action="${contextPath}/orders/form/cart"
                      method="post">

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
                                                       name="cartItemIds"
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

                                                    <button type="button" class="btn-close" aria-label="장바구니 상품 삭제"
                                                            data-bs-toggle="modal"
                                                            data-bs-target="#commonConfirmModal"
                                                            data-title="장바구니 상품 삭제"
                                                            data-message="선택한 상품을 장바구니에서 삭제하시겠습니까?"
                                                            data-confirm-text="삭제"
                                                            data-confirm-class="btn-danger"
                                                            data-action-type="fetch-delete">

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

                                    <button type="submit"
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

                </form>





            </c:otherwise>


        </c:choose>


    </div>



</main>
<jsp:include page="../common/confirm-modal.jsp"/>
<jsp:include page="../common/footer.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js">
</script>

<script>
    const selectedAllCheckBox = document.getElementById("selectAllCartItems");
    const itemCheckboxes = document.querySelectorAll(".cart-item-checkbox");
    const cartItemCards = document.querySelectorAll(".cart-item-card");
    const selectedTotalPriceText = document.getElementById("selectedTotalPriceText");
    const paymentTotalPriceText = document.getElementById("paymentTotalPriceText");
    const cartOrderForm = document.getElementById("cartOrderForm");

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

        if (!selectedTotalPriceText || !paymentTotalPriceText) {
            return;
        }

        let totalPrice = 0;

        document.querySelectorAll(".cart-item-card").forEach(function (card) {
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
        const currentCheckBoxes = document.querySelectorAll(".cart-item-checkbox");
        const checkedCount = document.querySelectorAll(".cart-item-checkbox:checked").length;

        selectedAllCheckBox.checked = currentCheckBoxes.length > 0 && checkedCount === currentCheckBoxes.length;
    }

    function handleCartApiResponse(response, data, fallbackMessage) {
        if (response.status === 401 && data.code === "NOT_LOGIN") {
            window.location.href = data.redirectUrl || "${contextPath}/member/login";
            return null;
        }

        if (!response.ok) {
            throw new Error(data.message || fallbackMessage);
        }

        return data;
    }

    function updateQuantityOnServer(card, quantity) {
        const cartItemId = card.dataset.cartItemId;

        return fetch("${contextPath}/cart/items/" + cartItemId + "/quantity", {
            method: "POST",
            headers: {
                "Content-Type" : "application/json",
                "X-Requested-With": "XMLHttpRequest"
            },
            body: JSON.stringify({
                quantity: quantity
            })
        }).then(function (response) {
            return response.json().then(function (data) {
                return handleCartApiResponse(response, data, "수량 변경에 실패했습니다.");
            })
        })

    }



    if (selectedAllCheckBox) {
        selectedAllCheckBox.addEventListener("change", function () {
            document.querySelectorAll(".cart-item-checkbox").forEach(function (checkbox) {
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
                   if (result === null) {
                       return;
                   }

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
                    if (result === null) {
                        return;
                    }

                    quantityInput.value = result.quantity;

                    updateItemTotalPrice(card);
                    updateSelectedTotalPrice();
                }).catch(function (error) {
                showToast(error.message, "danger")
            });

        })

        updateItemTotalPrice(card);
    });

    updateSelectedTotalPrice();

    function deleteCartItemOnServer(card) {
        const cartItemId = card.dataset.cartItemId;

        return fetch("${contextPath}/cart/items/" + cartItemId + "/delete", {
            method: "POST",
            headers: {
                "X-Requested-With": "XMLHttpRequest"
            }
        }).then(function (response) {
            return response.json().then(function (data) {
                return handleCartApiResponse(response, data, "장바구니 상품 삭제에 실패하였습니다.");
            })
        })
    }

    function removeCartItemCard(card) {
        card.remove();

        updateSelectedTotalPrice();
        updateSelectAllState();

        const remainingItems = document.querySelectorAll(".cart-item-card");

        if (remainingItems.length === 0) {
            setTimeout(function () {
                location.reload();
            },800)
        }
    }

    document.addEventListener("confirm:fetch-delete", function (event) {
        const button = event.detail.triggerButton;
        const card = button.closest(".cart-item-card");

        if (!card) {
            return;
        }

        deleteCartItemOnServer(card)
            .then(function (data) {
                if (data === null) {
                    return;
                }

                removeCartItemCard(card);
                showToast(data.message, "success");
            }).catch(function (error) {
                showToast(error.message, "danger");
        })
    })

    if (cartOrderForm) {
        cartOrderForm.addEventListener("submit", function (event) {

            const checkItems = document.querySelectorAll(".cart-item-checkbox:checked");

            if (checkItems.length === 0) {
                event.preventDefault();
                showToast("주문할 상품을 선택해주세요.", "danger")
            }
        });
    }

</script>

</body>

</html>
