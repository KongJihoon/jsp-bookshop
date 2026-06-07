<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>BookShop Admin Dashboard</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>

<body class="bg-light">

<jsp:include page="../../common/toast.jsp"/>

<%--Sidebar--%>

<div class="d-flex">
    <jsp:include page="../../common/dashboard-sidebar.jsp"/>

    <main class="flex-grow-1 p-4">

        <div class="mb-4">
            <h2 class="fw-bold mb-1">도서 등록</h2>
            <p class="text-muted mb-0">판매할 도서의 정보를 입력하고 이미지를 등록합니다.</p>
        </div>

        <form action="${contextPath}/admin/product/add" method="post" enctype="multipart/form-data">


            <div class="row g-4">

                <!-- 좌측 : 도서 기본 정보 -->
                <div class="col-lg-8">

                    <div class="card shadow-sm border-0 rounded-4 mb-4">
                        <div class="card-header bg-white fw-bold py-3">기본 정보</div>

                        <div class="card-body p-4">
                            <div class="mb-3">
                                <label for="name">도서명</label>
                                <input type="text"
                                       id="name"
                                       name="name"
                                       class="form-control"
                                       value="${product.name}"
                                       placeholder="도서명을 입력하세요.">
                            </div>

                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="author">저자</label>
                                    <input type="text"
                                           id="author"
                                           name="author"
                                           class="form-control"
                                           value="${product.author}"
                                           placeholder="저자를 입력하세요.">
                                </div>

                                <div class="col-md-6 mb-3">
                                    <label for="publisher">출판사</label>
                                    <input type="text"
                                           id="publisher"
                                           name="publisher"
                                           class="form-control"
                                           value="${product.publisher}"
                                           placeholder="출판사를 입력하세요.">
                                </div>
                            </div>

                            <div class="mb-3">
                                <label for="categoryId" class="form-label fw-semibold">카테고리</label>
                                <select name="categoryId" id="categoryId" class="form-select">

                                    <option value="">카테고리를 선택하세요.</option>

                                    <c:forEach items="${categories}" var="category">

                                        <option value="${category.categoryId}"
                                        ${product.categoryId == category.categoryId ? 'selected' : ''}>

                                            ${category.categoryName}

                                        </option>


                                    </c:forEach>

                                </select>

                            </div>

                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="price">가격</label>
                                    <div class="input-group">
                                        <input type="number"
                                               id="price"
                                               name="price"
                                               class="form-control"
                                               value="${product.price}"
                                               placeholder="가격">
                                        <span class="input-group-text">원</span>
                                    </div>
                                </div>

                                <div class="col-md-6 mb-3">
                                    <label for="stockQuantity">재고 수량</label>
                                    <div class="input-group">
                                        <input type="number"
                                               id="stockQuantity"
                                               name="stockQuantity"
                                               class="form-control"
                                               value="${product.stockQuantity}"
                                               placeholder="재고">
                                        <span class="input-group-text">권</span>
                                    </div>
                                </div>
                            </div>

                            <div class="mb-0">
                                <label for="description" class="form-label fw-semibold">도서 소개</label>
                                <textarea name="description" id="description"
                                          rows="8"
                                          class="form-control"
                                          placeholder="도서 소개를 입력하세요."
                                >${product.description}</textarea>
                            </div>

                        </div>

                    </div>
                </div>

                <!-- 우측 : 이미지 등록 -->

                <div class="col-lg-4">
                    <div class="card shadow-sm border-0 rounded-4 mb-4">
                        <div class="card-header bg-white fw-bold py-3">
                            대표 이미지
                        </div>

                        <div class="card-body p-4">
                            <div id="thumbnailPreview" class="border rounded-4 d-flex justify-content-center align-items-center text-muted mb-3" style="height: 260px; overflow: hidden">
                                이미지 미리보기</div>
                            <input type="file"
                                   id="thumbnailImage"
                                   name="thumbnailImage"
                                   class="form-control"
                                   accept="image/*">
                            <small class="text-muted d-block mt-2">
                                대표 이미지는 필수입니다.
                            </small>
                        </div>

                    </div>

                    <div class="card shadow-sm border-0 rounded-4">
                        <div class="card-header bg-white fw-bold py-3">상세 이미지</div>

                        <div class="card-body p-4">
                            <input type="file"
                                   id="detailImages"
                                   name="detailImages"
                                   accept="image/*"
                                   class="form-control"
                                   multiple>

                            <small class="text-muted d-block mt-2">상세 이미지는 최대 5장까지 등록할 수 있습니다.</small>
                        </div>
                    </div>
                </div>



            </div>

            <div class="d-flex justify-content-end gap-2 mt-4">

                <button type="submit" class="btn btn-primary me-2">도서 등록</button>

                <a href="${contextPath}/admin/dashboard" class="btn btn-outline-secondary">취소</a>

            </div>

        </form>

    </main>

</div>


<script>
    const thumbnailInput = document.getElementById("thumbnailImage");
    const thumbnailPreview = document.getElementById("thumbnailPreview")

    thumbnailInput.addEventListener("change", function () {

        const file = thumbnailInput.files[0];

        if (!file) {
            thumbnailPreview.innerHTML = "이미지 미리보기";
            return;
        }
        const reader = new FileReader();

        reader.onload = function (e) {
            thumbnailPreview.innerHTML =
                '<img src="' + e.target.result + '" style="width:100%; height:100%; object-fit:cover">';
        }

        reader.readAsDataURL(file);

    })


</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>

</html>