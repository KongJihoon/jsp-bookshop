# Bootstrap Toast 표시 문제 트러블슈팅

## 문제 상황

로그인, 회원가입, 회원정보 수정 등 사용자에게 성공 및 실패 메시지를 표시하기 위해 기존 `alert` 대신 Bootstrap Toast 적용을 고민하였습니다.

기존에는 다음과 같이 브라우저 기본 알림을 이용하여 사용자에게 메시지를 표시했다.

```jsp
<c:if test="${not empty errorMessage}">
    <script>
        window.addEventListener("DOMContentLoaded", function () {
            alert("${errorMessage}");
        });
    </script>
</c:if>
```

기능적으로는 문제가 없지만 다음과 같은 아쉬움이 있었다.

- 사용자가 직접 확인 버튼을 눌러야 메시지를 닫을 수 있어 불편함
- 브라우저 기본 팝업이라 디자인 변경이 어렵다.
- 사용자 흐름을 강제로 끊어버리는 느낌이 있다.
- 성공과 실패 메시지를 일관된 UI로 표시하기 어렵다.

따라서 Bootstrap Toast를 적용하여 다음과 같은 개선을 시도하였다.

---

# Toast란?

Toast는 사용자에게 짧은 알림 메시지를 보여주는 UI 컴포넌트이다.

예를 들어 다음과 같은 상황에서 활용된다.


- 회원가입 완료
- 로그인 성공
- 회원정보 수정 완료
- 이메일 중복
- 로그인 실패

기존 `alert`와 달리 화면 한쪽에 잠시 표시되었다가 자동으로 사라진다.

## alert와 Toast 비교

| 항목 | alert | Toast |
|--------|--------|--------|
| 표시 방식 | 브라우저 기본 팝업 | 화면 내 컴포넌트 |
| 사용자 조작 | 확인 버튼 클릭 필요 | 자동 사라짐 |
| 디자인 | 브라우저 기본 스타일 | 커스터마이징 가능 |
| UX | 흐름 중단 | 흐름 유지 |
| 재사용성 | 낮음 | 높음 |

# 공통 Toast 컴포넌트 적용

회원가입, 로그인, 회원정보 수정 등 여러 화면에서 동일한 UI 알림을 사용하기 위해 공통 Toast 컴포넌트를 만들었다.

```jsp
<c:if test="${not empty errorMessage}">
    <c:set var="toastMessage" value="${errorMessage}"/>
    <c:set var="toastType" value="danger"/>
</c:if>

<c:if test="${not empty successMessage}">
    <c:set var="toastMessage" value="${successMessage}"/>
    <c:set var="toastType" value="success"/>
</c:if>
```

성공 메시지와 실패 메시지를 구분하여 Bootstrap 색상을 다르게 적용하였다.

| 메시지 종류 | 변수 | Bootstrap 색상 |
|------------|------|---------------|
| 성공 | successMessage | success |
| 실패 | errorMessage | danger |


# 성공 메시지 출력되이 않았던 문제

### 원인 1 - Redirect 발생 시 Model 데이터 소멸

`Model` 객체는 현재 요청(Request) 범위에서만 유효하다.

하지만 Redirect가 발생하면 새로운 요청이 생성된다.

```text
POST /member/edit
↓
redirect:/member/info
↓
GET /member/info
```

즉,

```java
model.addAttribute(...)
```

로 저장한 데이터는 새로운 요청까지 전달되지 않는다.

따라서 성공 메시지는 사라진다.

---


### 해결 방법
Redirect 이후에도 데이터를 전달하기 위해 `RedirectAttributes`를 사용하였다.

```java
redirectAttributes.addFlashAttribute(
        "successMessage",
        "회원정보가 수정되었습니다."
);

return "redirect:/member/info";
```

FlashAttribute는 Redirect 이후 한 번만 사용할 수 있는 임시 데이터이다.

성공 메시지 전달에 적합하다.

---


# 배운 점

이번 문제를 통해 단순한 UI 알림 기능도 여러 개념이 연결되어 있다는 것을 알 수 있었다.

## 학습 내용

### Redirect와 Model의 차이

- Model은 현재 요청에서만 사용 가능
- Redirect 이후에는 데이터 유지 불가

### FlashAttribute 사용

- Redirect 이후 데이터 전달 가능
- 한 번 사용 후 자동 삭제
- 성공 메시지 전달에 적합

### Bootstrap Toast

- alert보다 사용자 경험이 좋음
- 공통 컴포넌트로 관리 가능

### JavaScript 로딩 순서

- JS 라이브러리가 먼저 로딩되어야 사용 가능
- UI 문제처럼 보였지만 실제 원인은 로딩 순서 문제였음

### 공통 컴포넌트 분리

- toast.jsp로 분리하여 재사용성 향상
- 회원가입, 로그인, 회원정보 수정 모두 동일한 방식으로 처리 가능

---

# 결론

이번 트러블슈팅은 단순히 Toast를 적용하는 과정이 아니었다.

성공 메시지 전달 방식, Redirect 동작 원리, FlashAttribute, Bootstrap JS 로딩 순서까지 함께 이해해야 해결할 수 있는 문제였다.

특히 문제를 해결하기 위해

```text
메시지가 전달되지 않는 문제인가?
Toast가 생성되지 않는 문제인가?
Bootstrap JS가 로딩되지 않은 문제인가?
```

를 단계적으로 확인하며 원인을 좁혀 나간 경험이 의미 있었다.