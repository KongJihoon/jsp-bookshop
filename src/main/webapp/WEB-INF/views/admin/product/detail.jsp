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

<div class="d-flex">

    <!-- Sidebar -->

<jsp:include page="../../common/dashboard-sidebar.jsp"/>


    <main class="flex-grow-1 p-4">

        <!-- 제목 -->
        <div class="d-flex justify-content-between align-items-center mb-4">

            <div>
                <h2 class="fw-bold mb-1">도서 상세 정보</h2>
                <p class="text-muted mb-0">등록된 도서의 상세 정보를 확인합니다.</p>
            </div>

            <div>
                <button class="btn btn-outline-secondary">목록</button>
                <a href="${contextPath}/admin/product/${productId}/edit" class="btn btn-primary">수정</a>
            </div>



        </div>

        <!-- 기본 정보 -->
        <div class="card shadow-sm border-0 mb-4">

            <div class="card-header fw-bold">기본 정보</div>

            <div class="card-body">

                <div class="row mb-3">
                    <div class="col-md-6">
                        <label class="form-label fw-semibold" for="">카테고리</label>

                        <div class="form-control bg-light">${product.categoryName}</div>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold" for="">판매 상태</label>
                        <div>
                            <c:choose>
                                <c:when test="${product.status == 'ACTIVE'}">
                                    <span class="badge bg-success">판매중</span>
                                </c:when>

                                <c:when test="${product.status == 'SOLD_OUT'}">
                                    <span class="badge bg-secondary">품절</span>
                                </c:when>

                                <c:otherwise>
                                    <span class="badge bg-danger">판매중지</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>


                <div class="mb-3">
                    <label class="form-label fw-semibold" for="">도서명</label>
                    <div class="form-control bg-light">${product.name}</div>
                </div>

                <div class="row mb-3">
                    <div class="col-md-6">

                        <label class="form-label fw-semibold" for="">저자</label>
                        <div class="form-control bg-light">${product.author}</div>

                    </div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold" for="">출판사</label>
                        <div class="form-control bg-light">${product.publisher}</div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-6">
                        <label class="form-label fw-semibold" for="">가격</label>
                        <div class="form-control bg-light">${product.price}</div>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold" for="">재고</label>
                        <div class="form-control bg-light">${product.stockQuantity}</div>
                    </div>
                </div>


            </div>


        </div>

        <!-- 대표 이미지 -->

        <div class="card shadow-sm border-0 mb-4">

            <div class="card-header fw-bold">대표 이미지</div>

            <div class="card-body text-center">

                <c:forEach var="image" items="${product.images}">
                    <c:if test="${image.imageType == 'THUMBNAIL'}">
                        <img
                                src="${contextPath}${image.imagePath}"
                                alt="대표 이미지"
                                class="img-fluid rounded border"
                                style="max-width: 260px;"
                        >
                    </c:if>
                </c:forEach>

            </div>

        </div>
        <!-- 상세 이미지 -->
        <div class="card shadow-sm border-0 mb-4">
            <div class="card-header fw-bold">상세 이미지</div>

            <div class="card-body">

                <div class="row g-3">

                    <c:forEach var="image" items="${product.images}">
                        <c:if test="${image.imageType == 'DETAIL'}">
                            <div class="col-md-3">
                                <img src="${contextPath}${image.imagePath}"
                                     alt="상세이미지"
                                     class="img-fluid rounded border"
                                >
                            </div>
                        </c:if>
                    </c:forEach>

                </div>

            </div>
        </div>

        <!-- 도서 설명 -->

        <div class="card shadow-sm border-0 mb-4">
            <div class="card-header fw-bold">
                도서 설명
            </div>
            <div class="card-body">

                <div style="min-height: 200px;">자바 기초부터 실전까지 학습할 수 있는 도서입니다.</div>
            </div>
        </div>

        <button type="button"
                class="btn btn-outline-danger"
                data-bs-toggle="modal"
                data-bs-target="#commonConfirmModal"
                data-title="도서 삭제"
                data-message="삭제한 도서는 복구할 수 없습니다."
                data-confirm-text="삭제"
                data-confirm-class="btn-danger"
                data-action-type="submit"
                data-form-id="deleteProductForm">
            도서 삭제
        </button>

        <form id="deleteProductForm"
              action="${contextPath}/admin/product/${product.productId}/delete"
              method="post">
        </form>

    </main>

</div>

<jsp:include page="../../common/confirm-modal.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>

</html>