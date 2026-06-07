<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

    <!-- Sidebar -->
    <aside class="bg-dark text-white min-vh-100 p-3" style="width: 260px;">
        <h4 class="fw-bold mb-4">BookShop Admin</h4>

        <ul class="nav flex-column gap-2">
            <li class="nav-item">
                <a href="${contextPath}/admin/dashboard" class="nav-link text-white bg-secondary rounded">
                    대시보드
                </a>
            </li>

            <li class="nav-item">
                <a href="${contextPath}/admin/books" class="nav-link text-white">
                    도서 관리
                </a>
            </li>

            <li class="nav-item">
                <a href="${contextPath}/admin/product/add" class="nav-link text-white">
                    도서 등록
                </a>
            </li>

            <li class="nav-item">
                <a href="${contextPath}/admin/members" class="nav-link text-white">
                    회원 관리
                </a>
            </li>

            <li class="nav-item">
                <a href="${contextPath}/admin/orders" class="nav-link text-white">
                    주문 관리
                </a>
            </li>
        </ul>

        <hr class="border-secondary my-4">

        <a href="${contextPath}/" class="d-block text-secondary text-decoration-none mb-2">
            쇼핑몰로 이동
        </a>

        <a href="${contextPath}/member/logout" class="d-block text-secondary text-decoration-none">
            로그아웃
        </a>
    </aside>