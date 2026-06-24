<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:if test="${not empty errorMessage}">
    <c:set var="toastMessage" value="${errorMessage}"/>
    <c:set var="toastType" value="danger"/>
</c:if>

<c:if test="${not empty successMessage}">
    <script>
        console.log("successMessage =", "${successMessage}");
    </script>
    <c:set var="toastMessage" value="${successMessage}"/>
    <c:set var="toastType" value="success"/>
</c:if>

<c:if test="${not empty toastMessage}">
    <div class="toast-container position-fixed top-0 end-0 p-3" style="z-index: 9999;">
    <div id="commonToast"
    class="toast text-bg-${toastType} border-0"
    role="alert"
    aria-live="assertive"
    aria-atomic="true">

    <div class="d-flex">
    <div class="toast-body">
    ${toastMessage}
    </div>

    <button type="button"
    class="btn-close btn-close-white me-2 m-auto"
    data-bs-dismiss="toast"
    aria-label="Close">
    </button>
    </div>
    </div>
    </div>

    <script>
        window.addEventListener("load", function () {
            const toastElement = document.getElementById("commonToast");

            if (!toastElement || !window.bootstrap) {
                return;
            }

            const toast = new bootstrap.Toast(toastElement, {
                delay: 3000,
                autohide: true
            });

            toast.show();
        });
    </script>
</c:if>

<script>
    function showToast(message, type) {
        const toastContainer = document.createElement("div");

        toastContainer.className = "toast-container position-fixed top-0 end-0 p-3";
        toastContainer.style.zIndex = "9999";

        toastContainer.innerHTML =
            '<div class="toast text-bg-' + type + ' border-0" role="alert">' +
            '  <div class="d-flex">' +
            '    <div class="toast-body">' + message + '</div>' +
            '    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>' +
            '  </div>' +
            '</div>';

        document.body.appendChild(toastContainer);

        const toastElement = toastContainer.querySelector(".toast");
        const toast = new bootstrap.Toast(toastElement, {
            delay: 2500,
            autohide: true
        });

        toast.show();

        toastElement.addEventListener("hidden.bs.toast", function () {
            toastContainer.remove();
        });
    }
</script>