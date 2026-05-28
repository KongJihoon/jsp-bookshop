const signupForm = document.getElementById("signupForm");

signupForm.addEventListener("submit", function (event) {

    const loginIdChecked = document.getElementById("loginIdChecked");
    const emailChecked = document.getElementById("emailChecked");

    if (loginIdChecked.value !== "true") {

        event.preventDefault();

        alert("아이디 중복 확인을 해주세요.");

        document.getElementById("loginId").focus();

        return;
    }

    if (emailChecked.value !== "true") {

        event.preventDefault();

        alert("이메일 중복 확인을 해주세요.");

        document.getElementById("email").focus();

        return;
    }
});


/**
 * 공통 중복 확인 필수 요소
 */
function setupDuplicateCheck(options) {
    const {
        input,
        button,
        messageElement,
        hiddenInput,
        url,
        regex,
        emptyMessage,
        invalidMessage,
        availableMessage,
        duplicateMessage
    } = options;

    /**
     * 메시지 출력
     */
    function showMessage(message, color) {
        messageElement.textContent = message;
        messageElement.style.color = color;
    }

    /**
     * 중복 확인 상태 초기화
     */

    function resetState() {

        hiddenInput.value = "false";
        messageElement.textContent = "";

        button.disabled = false;
        button.textContent = "중복 확인";

        input.readOnly = false;
    }

    /**
     * 값 변경 감지
     */

    input.addEventListener("input", function () {
        if (input.readOnly) {
            return;
        }

        resetState();
    })

    button.addEventListener("click", function () {

        const value = input.value.trim();

        /**
         * 빈 값 검증
         */
        if (value === "") {
            showMessage(emptyMessage, "#dc3545");

            input.focus();
            return;
        }

        /**
         * 정규식 검증
         */
        if (!regex.test(value)) {
            showMessage(invalidMessage, "#dc3545");

            input.focus();
            return;
        }

        /**
         * 서버 중복 검사
         */
        fetch(contextPath + url + encodeURIComponent(value))
            .then(response => {
                if (!response.ok) {
                    throw new Error("네트워크 응답이 올바르지 않습니다.");
                }
                return response.json();
            })
            .then(available => {

                if (available) {
                    showMessage(availableMessage, "#198754");

                    hiddenInput.value = "true";

                    input.readOnly = true;

                    button.disabled = true;

                    button.textContent = "확인 완료";
                } else {

                    /**
                     * 중복 발생
                     */

                    showMessage(duplicateMessage, "#dc3545");

                    hiddenInput.value = "false";

                    input.focus();

                }

            })
            .catch(error => {
                console.error("중복 확인 중 오류 발생:", error);
                showMessage("중복 확인 중 오류가 발생했습니다. 다시 시도해주세요.", "#dc3545");
            })


    })


}





/**
 * 아이디 중복 확인 설정
 */

setupDuplicateCheck({

    input : document.getElementById("loginId"),
    button : document.getElementById("loginIdCheckBtn"),
    messageElement : document.getElementById("loginCheckMessage"),
    hiddenInput : document.getElementById("loginIdChecked"),

    url: "/member/signup/check-login-id?loginId=",

    regex : /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{2,20}$/,

    emptyMessage : "아이디를 입력해주세요.",

    invalidMessage : "로그인 아이디는 2~20자의 영문과 숫자 조합이어야 합니다.",

    availableMessage : "사용 가능한 아이디입니다.",

    duplicateMessage : "이미 사용 중인 아이디입니다.",

})

setupDuplicateCheck({

    input : document.getElementById("email"),
    button : document.getElementById("emailCheckBtn"),
    messageElement : document.getElementById("emailCheckMessage"),
    hiddenInput : document.getElementById("emailChecked"),

    url: "/member/signup/check-email?email=",

    regex : /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,

    emptyMessage : "이메일을 입력해주세요.",

    invalidMessage : "유효하지 않은 이메일 형식입니다.",

    availableMessage : "사용 가능한 이메일입니다.",

    duplicateMessage : "이미 사용 중인 이메일입니다.",


})

/**
 * 다음 주소 API
 */
function openPostcode() {
    new daum.Postcode({
        oncomplete: function(data) {
            // 팝업에서 검색결과 항목을 클릭했을 때 실행할 코드를 작성하는 부분입니다.
            // 예제를 참고하여 다양한 활용법을 확인해 보세요.
            document.getElementById('zipcode').value = data.zonecode; //5자리 새우편번호 사용
            document.getElementById('address').value = data.address;
        }
    }).open();
}

window.openPostcode = openPostcode;

