
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<%--최초 GET은 product, 검증 실패 POST는 productUpdateRequest 사용--%>

<c:set var="formProduct" value="${pageContext.request.method eq 'POST' ? productUpdateRequest : product}"/>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>도서 수정 | BookShop Admin</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">
</head>

<body class="bg-light">

<jsp:include page="../../common/toast.jsp"/>

<div class="d-flex">

    <!-- Sidebar -->

    <jsp:include page="../../common/dashboard-sidebar.jsp"/>
    <!-- 도서 수정 메인 콘텐츠 -->
    <main class="flex-grow-1 p-4">

        <!-- 페이지 제목 -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h2 class="fw-bold mb-1">도서 수정</h2>
                <p class="text-muted mb-0">등록된 도서 정보와 판매 상태를 수정합니다.</p>
            </div>

            <a href="${contextPath}/admin/product/${product.productId}" class="btn btn-outline-secondary">상세 화면으로</a>
        </div>

        <!-- 도서 수정 폼 -->
        <form action="${contextPath}/admin/product/${product.productId}/edit" method="post" enctype="multipart/form-data">


            <div class="row g-4">
                <!-- 좌측: 도서 정보 -->
                <div class="col-lg-8">
                    <!-- card -->
                    <div class="card shadow-sm border-0 rounded-4 mb-4">

                        <!-- card-header -->
                        <div class="card-header bg-white fw-bold py-3">기본 정보</div>

                        <!-- card-body -->
                        <div class="card-body p-4">
                            <div class="mb-3">
                                <label for="name" class="form-label fw-semibold">도서명
                                    <span class="text-danger">*</span>
                                </label>
                                <input type="text"
                                       id="name"
                                       name="name"
                                       value="${formProduct.name}"
                                       class="form-control"
                                       placeholder="도서명을 입력하세요.">
                            </div>

                            <div class="row">

                                <div class="col-md-6 mb-3">
                                    <label for="author" class="form-label fw-semibold">저자
                                        <span class="text-danger">*</span>
                                    </label>
                                    <input type="text"
                                           id="author"
                                           name="author"
                                           value="${formProduct.author}"
                                           class="form-control"
                                           placeholder="저자를 입력하세요.">
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label for="publisher" class="form-label fw-semibold">출판사
                                        <span class="text-danger">*</span>
                                    </label>
                                    <input type="text"
                                           id="publisher"
                                           name="publisher"
                                           value="${formProduct.publisher}"
                                           class="form-control"
                                           placeholder="출판사를 입력하세요.">
                                </div>


                            </div>

                            <div class="row">

                                <div class="col-md-6 mb-3">
                                    <label for="categoryId" class="form-label fw-semibold">카테고리
                                        <span class="text-danger">*</span>
                                    </label>
                                    <select name="categoryId" id="categoryId" class="form-select">
                                        <option value="">카테고리를 선택하세요.</option>
                                        <c:forEach var="category" items="${categories}">

                                            <option value="${category.categoryId}"
                                                    ${formProduct.categoryId == category.categoryId
                                                    ?'selected' : ''}>
                                                ${category.categoryName}
                                            </option>

                                        </c:forEach>

                                    </select>
                                </div>

                                <div class="col-md-6 mb-3">
                                    <label for="status" class="form-label fw-semibold">판매 상태
                                        <span class="text-danger">*</span>
                                    </label>
                                    <select name="status" id="status" class="form-select">
                                        <option value="ACTIVE"
                                        ${formProduct.status == 'ACTIVE'
                                        ? 'selected' : ''}>판매중</option>
                                        <option value="SOLD_OUT"
                                        ${formProduct.status == 'SOLD_OUT'
                                        ? 'selected' : ''}>품절</option>
                                        <option value="DELETED"
                                        ${formProduct.status == 'DELETED'
                                        ? 'selected' : ''}>판매중지</option>
                                    </select>
                                </div>

                            </div>

                            <div class="row">

                                <div class="col-md-6 mb-3">
                                    <label for="price" class="form-label fw-semibold">가격
                                        <span class="text-danger">*</span>
                                    </label>
                                    <div class="input-group">
                                        <input type="number"
                                               id="price"
                                               name="price"
                                               value="${formProduct.price}"
                                               class="form-control"
                                               min="0"
                                               placeholder="가격">
                                        <span class="input-group-text">원</span>
                                    </div>
                                </div>


                                <div class="col-md-6 mb-3">
                                    <label for="stockQuantity" class="form-label fw-semibold">재고 수량
                                        <span class="text-danger">*</span>
                                    </label>
                                    <div class="input-group">
                                        <input type="number"
                                               id="stockQuantity"
                                               name="stockQuantity"
                                               value="${formProduct.stockQuantity}"
                                               class="form-control"
                                               min="0"
                                               placeholder="재고">
                                        <span class="input-group-text">권</span>
                                    </div>
                                </div>

                            </div>

                            <div>
                                <label for="description" class="form-label fw-semibold">도서 소개
                                    <span class="text-danger">*</span>
                                </label>
                                <textarea name="description" id="description"
                                          rows="9" class="form-control" placeholder="도서 소개를 입력하세요.">${formProduct.description}</textarea>
                            </div>


                        </div>


                    </div>

                    <div class="card shadow-sm border-0 rounded-4">
                        <div class="card-header bg-white fw-bold py-3">수정 안내</div>

                        <div class="card-body p-4">
                            <ul class="text-muted small mb-0 ps-3">
                                <li class="mb-2">새로운 이미지를 선택하지 않으면 기존 이미지가 유지됩니다.</li>
                                <li class="mb-2">대표이미지를 선택하면 기존 대표이미지가 교체됩니다.</li>
                                <li>상세이미지를 선택하면 기존 상태 이미지 전체가 교체됩니다.</li>
                            </ul>
                        </div>
                    </div>
                </div>


                <!-- 우측 : 이미지 수정 -->

                <div class="col-lg-4">
                    <!-- 대표 이미지 -->
                    <div class="card shadow-sm border-0 rounded-4 mb-4">
                        <div class="card-header bg-white fw-bold py-3">대표 이미지</div>

                        <div class="card-body p-4">
                            <p class="small fw-semibold mb-2">현재 이미지</p>

                            <div id="thumbnailPreview" class="border rounded-4 bg-light d-flex flex-column
                            justify-content-center align-items-center text-muted mb-3"
                            style="height: 260px; overflow: hidden">


                                <c:set var="hasThumbnail" value="false"/>

                                <c:forEach var="image" items="${product.images}">

                                    <c:if test="${image.imageType == 'THUMBNAIL'}">
                                        <c:set var="hasThumbnail" value="true"/>
                                        <img src="${contextPath}${image.imagePath}"
                                             alt="현재 이미지"
                                             style="width: 100%; height: 100%; object-fit: cover">
                                    </c:if>

                                </c:forEach>

                                <c:if test="${not hasThumbnail}">
                                    등록된 대표 이미지가 없습니다.
                                </c:if>


                            </div>

                            <label for="thumbnailImage" class="form-label fw-semibold">새 대표 이미지</label>

                            <input type="file"
                                   id="thumbnailImage"
                                   name="thumbnailImage"
                                   class="form-control"
                                   accept="image/*">
                            <div class="form-text">변경할 경우에만 이미지를 선택해주세요.</div>
                        </div>
                    </div>

                    <!-- 상세 이미지 -->
                    <div class="card shadow-sm border-0 rounded-4">
                        <div class="card-header bg-white fw-bold py-3">상세 이미지</div>

                        <div class="card-body p-4">
                            <p class="small fw-semibold mb-2">현재 이미지</p>
                            <div id="detailImagePreview" class="row g-2 mb-3">

                                <c:set var="detailImageCount" value="0"/>

                                <c:forEach var="image" items="${product.images}">

                                    <c:if test="${image.imageType == 'DETAIL'}">
                                        <c:set var="detailImageCount" value="${detailImageCount + 1}"/>

                                        <div class="col-6">
                                            <img src="${contextPath}${image.imagePath}"
                                            alt="현재 상세 이미지 ${detailImageCount}"
                                            class="rounded-3 border"
                                            style="width: 100%; height: 110px; object-fit: cover;">
                                        </div>
                                    </c:if>

                                </c:forEach>

                                <c:if test="${detailImageCount == 0}">
                                    <div class="col-12">
                                        <div class="border rounded-3 bg-light d-flex justify-content-center align-items-center text-muted"
                                             style="height: 110px">등록된 상세 이미지가 없습니다.</div>
                                    </div>
                                </c:if>



                            </div>

                            <label for="detailImages" class="form-label fw-semibold">새 상세 이미지</label>

                            <input type="file"
                                   id="detailImages"
                                   name="detailImages"
                                   class="form-control"
                                   accept="image/*"
                                   multiple>

                            <div class="form-text">최대 5장까지 선택할 수 있습니다.</div>
                        </div>
                    </div>


                </div>



            </div>

            <!-- 하단 버튼 -->

            <div class="d-flex justify-content-end gap-2 mt-4">
                <a href="${contextPath}/admin/product/${product.productId}" class="btn btn-outline-secondary px-4">취소</a>
                <button type="submit" class="btn btn-primary px-4">수정 내용 저장</button>
            </div>

        </form>



    </main>
</div>

<script>

    const thumbnailInput = document.getElementById("thumbnailImage");

    const thumbnailPreview = document.getElementById("thumbnailPreview");

    const detailImagePreview = document.getElementById("detailImagePreview");

    const detailImageInput = document.getElementById("detailImages");


    function showToast(message, type = 'danger') {
        let toastContainer = document.querySelector(".toast-container");

        if (!toastContainer) {
            toastContainer = document.createElement("div");

            toastContainer.className = "toast-container position-fixed top-0 end-0 p-3";

            toastContainer.style.zIndex = "9999";

            document.body.appendChild(toastContainer);
        }

        let toastElement = document.getElementById("commonToast");

        if (!toastElement) {
            toastElement = document.createElement("div");

            toastElement.id = "commonToast";
            toastElement.setAttribute("role", "alert");
            toastElement.setAttribute("aria-live", "assertive");
            toastElement.setAttribute("aria-atomic", "true");
            toastElement.innerHTML = `
                <div class="d-flex">
                    <div class="toast-body"></div>

                    <button type="button"
                            class="btn-close btn-close-white me-2 m-auto"
                            data-bs-dismiss="toast"
                            aria-label="Close">
                    </button>
                </div>
            `;

            toastContainer.appendChild(toastElement);

        }

        toastElement.className = "toast text-bg-" + type + " border-0";

        toastElement.querySelector(".toast-body").textContent = message;

        bootstrap.Toast.getOrCreateInstance(toastElement, {
            delay: 3000,
            autohide: true
        }).show();

    }

    function isImageFile(file) {
        return file.type.startsWith("image/");
    }

    thumbnailInput.addEventListener("change", function (){
        const file = this.files[0];

        if (!file) {
            return;
        }

        if (!isImageFile(file)) {
            showToast("이미지 파일만 선택할 수 있습니다.");
            this.value = "";
            return;
        }

        const imageUrl = URL.createObjectURL(file);

        thumbnailPreview.innerHTML = "";

        const image = document.createElement("img");

        image.src = imageUrl;
        image.alt = "새 대표 이미지 미리보기";
        image.style.width = "100%";
        image.style.height = "100%";
        image.style.objectFit = "cover";

        image.addEventListener("load", function () {
            URL.revokeObjectURL(imageUrl);
        });

        thumbnailPreview.appendChild(image);
    })

    detailImageInput.addEventListener("change", function () {

        const files = Array.from(this.files);

        if (files.length === 0) {
            return;
        }

        if (files.length > 5) {
            showToast("상세 이미지는 최대 5장까지 선택할 수 있습니다.");
            this.value = "";
            return;
        }

        if (files.some(file => !isImageFile(file))) {
            showToast("이미지 파일만 선택할 수 있습니다.");
            this.value = "";
            return;
        }


        detailImagePreview.innerHTML = "";

        files.forEach(function (file, index) {
            const imageUrl = URL.createObjectURL(file);

            const column = document.createElement("div");

            column.className = "col-6";

            const image = document.createElement("img");

            image.src = imageUrl;
            image.alt = "새 상세 이미지 " + (index + 1);
            image.className = "rounded-3 border";
            image.style.width = "100%";
            image.style.height = "110px";
            image.style.objectFit = "cover";

            image.addEventListener("load", function () {
                URL.revokeObjectURL(imageUrl);
            });

            column.appendChild(image);

            detailImagePreview.appendChild(column);

        });

    });



</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js">
</script>

</body>

</html>