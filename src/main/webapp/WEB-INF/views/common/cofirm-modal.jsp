<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<div class="modal fade"
    id="commonConfirmModal"
    tabindex="-1"
    aria-hidden="true">

    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow rounded-4">

            <div class="modal-header border-0">
                <h5 id="confirmModalTitle" class="modal-title fw-bold">확인</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal">
                </button>
            </div>

            <div class="modal-body">
                <p id="confirmModalMessage" class="mb-0"></p>
            </div>

            <div class="modal-footer border-0">
                <button type="button"
                        class="btn btn-light"
                        data-bs-dismiss="modal">취소</button>

                <button type="button" id="confirmModalButton" class="btn btn-danger">확인</button>
            </div>


        </div>

    </div>



</div>

<script>

    const confirmModalElement = document.getElementById("commonConfirmModal");

    confirmModalElement.addEventListener("show.bs.modal", function (event) {
        const button = event.relatedTarget;

        const title = button.dataset.title;
        const message = button.dataset.message;
        const formId = button.dataset.formId;

        document.getElementById("confirmModalTitle").textContent = title;
        document.getElementById("confirmModalMessage").textContent = message;
        document.getElementById("confirmModalButton").onclick = function () {
            document.getElementById(formId).submit();
        }
    })

</script>