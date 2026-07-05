<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<div class="modal fade"
    id="commonConfirmModal"
    tabindex="-1"
    aria-hidden="true">

    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow rounded-4">

            <div class="modal-header border-0 pb-0">
                <h5 id="confirmModalTitle" class="modal-title fw-bold">확인</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="닫기">
                </button>
            </div>

            <div class="modal-body py-4">
                <p id="confirmModalMessage" class="mb-0 text-secondary"></p>
            </div>

            <div class="modal-footer border-0 pt-0">
                <button type="button"
                        id="confirmModalCancelButton"
                        class="btn btn-light px-4"
                        data-bs-dismiss="modal">취소</button>

                <button type="button" id="confirmModalConfirmButton" class="btn btn-primary px-4">확인</button>
            </div>


        </div>

    </div>



</div>

<script>

    const confirmModalElement = document.getElementById("commonConfirmModal");

    confirmModalElement.addEventListener("show.bs.modal", function (event) {

        const button = event.relatedTarget;

        const title = button.dataset.title || "확인";
        const message = button.dataset.message || "진행하시겠습니까?";
        const confirmText = button.dataset.confirmText || "확인";
        const confirmClass = button.dataset.confirmClass || "btn-primary";
        const actionType = button.dataset.actionType;
        const formId = button.dataset.formId;
        const url = button.dataset.url;

        const titleElement = document.getElementById("confirmModalTitle");
        const messageElement = document.getElementById("confirmModalMessage");
        const confirmButton = document.getElementById("confirmModalConfirmButton");

        titleElement.textContent = title;
        messageElement.textContent = message;
        confirmButton.textContent = confirmText;

        confirmButton.className = "btn px-4 " + confirmClass;

        confirmButton.onclick = function () {
            if (actionType === "submit") {
                document.getElementById(formId).submit();
                return;
            }

            if (actionType === "redirect") {
                location.href = url;
            }

            if (actionType === "fetch-delete") {
                const deleteEvent = new CustomEvent("confirm:fetch-delete", {
                    detail: {
                        triggerButton: button
                    }
                });
                document.dispatchEvent(deleteEvent);
                const modal = bootstrap.Modal.getInstance(confirmModalElement);
                modal.hide();

                setTimeout(function () {
                    document.querySelectorAll(".modal-backdrop").forEach(function (backdrop) {
                        backdrop.remove();
                    });

                    document.body.classList.remove("modal-open");
                    document.body.style.removeProperty("overflow");
                    document.body.style.removeProperty("padding-right");
                }, 300);

                return;
            }
        };

    });

</script>