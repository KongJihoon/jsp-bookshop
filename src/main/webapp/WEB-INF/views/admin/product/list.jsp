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

        <!-- 페이지 제목 -->
        <div class="d-flex justify-content-between align-items-center mb-4">

            <div>
                <h2 class="fw-bold mb-1">도서 관리</h2>
                <p class="text-muted mb-0">등록된 도서를 조회하고 관리합니다.</p>
            </div>

            <a href="${contextPath}/admin/product/add" class="btn btn-primary">도서 등록</a>
        </div>

        <!-- 검색 영역 -->

        <div class="card shadow-sm border-0 mb-4">

            <div class="card-body">

               <form action="${contextPath}/admin/product/list" method="get">
                   <div class="row g-3">

                       <div class="col-md-3">
                           <label class="form-label fw-semibold">카테고리</label>

                           <select class="form-select" name="categoryId" onchange="this.form.submit()">
                               <option value="">전체</option>
                               <c:forEach var="category" items="${categories}">
                                   <option value="${category.categoryId}"
                                   ${selectedCategoryId == category.categoryId ? 'selected' : ''}>
                                           ${category.categoryName}
                                   </option>
                               </c:forEach>

                           </select>

                       </div>

                       <div class="col-md-3">

                           <label class="form-label fw-semibold">판매 상태</label>

                           <select class="form-select" name="status" id="" onchange="this.form.submit()">
                               <option value="">전체</option>
                               <option value="ACTIVE"
                                    ${selectedStatus == 'ACTIVE' ? 'selected' : ''}>판매중</option>
                               <option value="SOLD_OUT"
                                    ${selectedStatus == 'SOLD_OUT' ? 'selected' : ''}>품절</option>
                               <option value="DELETED"
                                    ${selectedStatus == 'DELETED' ? 'selected' : ''}>판매중지</option>
                           </select>

                       </div>

                       <div class="col-md-4">
                           <label class="form-label fw-semibold" for="">검색어</label>

                           <input class="form-control" type="text" name="keyword" value="${keyword}"
                                  placeholder="도서명 또는 저자 검색">
                       </div>

                       <div class="col-md-2 d-flex align-items-end">
                           <button type="submit" class="btn btn-dark w-100">🔎 검색</button>
                       </div>

                   </div>
               </form>

            </div>

        </div>

        <!-- 목록 영역 -->
        <div class="card shadow-sm border-0">

            <div class="card-body">

                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h5 class="fw-bold mb-0">도서 목록</h5>

                    <span class="text-muted">총 ${productPage.totalCount}권</span>
                </div>

                <div class="table-responsive">
                    <table class="table table-hover align-middle text-center">

                        <thead class="table-light">

                        <tr>
                            <th>번호</th>
                            <th class="text-start">도서명</th>
                            <th>저자</th>
                            <th>출판사</th>
                            <th>가격</th>
                            <th>재고</th>
                            <th>상태</th>
                            <th>등록일</th>
                            <th>관리</th>
                        </tr>

                        </thead>

                        <tbody>
                        <c:choose>

                            <c:when test="${empty products}">
                                <tr>
                                    <td colspan="9"
                                        class="text-center text-muted py-5">
                                        등록된 도서가 없습니다.
                                    </td>
                                </tr>
                            </c:when>

                            <c:otherwise>
                                <c:forEach var="product" items="${products}" varStatus="status">
                                    <tr>
                                        <td>
                                            ${(productPage.page - 1) * productPage.size + status.count}
                                        </td>

                                        <td class="text-start">${product.name}</td>

                                        <td>${product.author}</td>

                                        <td>${product.publisher}</td>

                                        <td>${product.price}원</td>

                                        <td>${product.stockQuantity}권</td>

                                        <td>
                                           <c:choose>
                                               <c:when test="${product.status == 'ACTIVE'}">
                                                   <span class="badge bg-success">판매중</span>
                                               </c:when>

                                               <c:when test="${product.status == 'SOLD_OUT'}">
                                                   <span class="badge bg-secondary">품절</span>
                                               </c:when>
                                               <c:otherwise>
                                                   <span>판매 중지</span>
                                               </c:otherwise>
                                           </c:choose>
                                        </td>

                                        <td>${product.createdAt.toLocalDate()}</td>

                                        <td>
                                            <a href="#" class="btn btn-sm btn-outline-primary">상세</a>
                                            <a href="#" class="btn btn-sm btn-outline-secondary">수정</a>

                                        </td>

                                    </tr>


                                </c:forEach>
                            </c:otherwise>

                        </c:choose>

                        </tbody>

                    </table>

                </div>


                <!-- 페이징 UI -->

                <nav class="mt-4">

                    <ul class="pagination justify-content-center">

                        <li class="page-item ${productPage.hasPrevious ? '' : 'disabled'}">
                            <a class="page-link" href="${contextPath}/admin/product/list?page=${productPage.page - 1}&categoryId=${selectedCategoryId}&status=${selectedStatus}&keyword=${keyword}">이전</a>
                        </li>

                        <c:forEach begin="1" end="${productPage.totalPages}" var="pageNumber">

                            <li class="page-item ${productPage.page == pageNumber ? 'active' : ''}">
                                <a class="page-link" href="${contextPath}/admin/product/list?page=${pageNumber}&categoryId=${selectedCategoryId}&status=${selectedStatus}&keyword=${keyword}">
                                    ${pageNumber}
                                </a>
                            </li>

                        </c:forEach>

                        <li class="page-item ${productPage.hasNext ? '' : 'disabled'}">
                            <a class="page-link" href="${contextPath}/admin/product/list?page=${productPage.page + 1}&categoryId=${selectedCategoryId}&status=${selectedStatus}&keyword=${keyword}">
                                다음
                            </a>
                        </li>


                    </ul>
                </nav>


            </div>

        </div>


    </main>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>

</html>