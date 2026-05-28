# 회원가입 기능 구현 중 트러블 슈팅 정리

## 1. JSP 정적 리소스(CSS/JS) 경로 문제

### 문제

다음처럼 CSS를 연결했지만 적용되지 않았다.

```jsp
<link rel="stylesheet" href="/src/main/resources/static/css/bookshop.css">
```

### 원인

브라우저는 프로젝트 내부 경로를 알 수 없다.

Spring Boot는:

```text
src/main/resources/static
```

하위 파일을 웹 경로 `/`로 매핑한다.

### 해결

```jsp
<link rel="stylesheet" href="${contextPath}/css/bookshop.css">
```

---

# 2. JSP 직접 접근 문제

## 문제

회원가입 페이지 이동 시 JSP 경로로 직접 접근하려고 했다.

```jsp
href="/WEB-INF/views/member/signup.jsp"
```

## 원인

`WEB-INF` 내부 파일은 외부에서 직접 접근할 수 없다.

반드시 Controller를 거쳐야 한다.

## 해결

```jsp
href="${contextPath}/member/signup"
```

Controller:

```java
@GetMapping("/signup")
public String signup(Model model) {

    model.addAttribute("member", new MemberSignUpRequest());

    return "member/signup";
}
```

---

# 3. @ModelAttribute DTO 값이 null로 바인딩되는 문제

## 문제

요청 파라미터는 존재했지만 DTO 값은 null이었다.

```text
param loginId = test123
request loginId = null
```

## 원인

Spring MVC는 기본적으로 setter를 통해 요청값을 바인딩한다.

DTO에 setter가 없었다.

## 해결

```java
@Getter
@Setter
public class MemberSignUpRequest {
}
```

## 정리

Setter 지양은 Entity 기준으로 많이 이야기한다.

Request DTO는 HTTP 요청 데이터를 담는 객체이므로 setter 사용이 일반적이다.

---


# 4. Validation 에러 메시지를 JSP alert로 출력

## 문제

Validation 실패 시 화면은 유지되지만 에러 메시지가 사용자에게 보이지 않았다.

## 해결

Controller:

```java
model.addAttribute(
        "errorMessage",
        bindingResult.getAllErrors()
                .get(0)
                .getDefaultMessage()
);
```

JSP:

```jsp
<c:if test="${not empty errorMessage}">
    <script>
        window.addEventListener("DOMContentLoaded", function () {
            alert("${errorMessage}");
        });
    </script>
</c:if>
```

## 주의

다음처럼 문자열 따옴표가 없으면 JS 오류가 발생할 수 있다.

```jsp
alert(${errorMessage});
```

---

# 5. JSTL ClassNotFoundException 문제

## 문제

다음 오류 발생:

```text
ClassNotFoundException:
jakarta.servlet.jsp.jstl.core.ConditionalTagSupport
```

## 원인

Spring Boot 3은 `jakarta` 기반 JSTL 의존성이 필요하다.

## 해결

```gradle
implementation 'jakarta.servlet.jsp.jstl:jakarta.servlet.jsp.jstl-api:3.0.2'
implementation 'org.glassfish.web:jakarta.servlet.jsp.jstl:3.0.1'
```

JSP:

```jsp
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
```

---

# 6. Ajax 중복 확인 URL 문제

## 문제

404 발생:

```text
GET /signup/check-login-id 404
```

## 원인

Controller 상단에 클래스 레벨 매핑이 존재했다.

```java
@RequestMapping("/member")
```

실제 URL은 다음이어야 했다.

```text
/member/signup/check-login-id
```

## 해결

```javascript
fetch(
    contextPath
    + "/member/signup/check-login-id?loginId="
    + encodeURIComponent(value)
)
```

---

# 7. form submit 검증이 동작하지 않은 문제

## 문제

중복 확인을 하지 않아도 회원가입이 진행되었다.

## 원인

다음 코드가 header 검색 form을 선택하고 있었다.

```javascript
document.querySelector("form")
```

header.jsp 내부 form이 먼저 선택되었다.

## 해결

회원가입 form에 id 부여:

```jsp
<form id="signupForm">
```

JS 수정:

```javascript
const signupForm =
    document.getElementById("signupForm");
```

---

# 8. 클라이언트 검증과 서버 검증 역할 분리

## 정리

JS 검증은 사용자 편의를 위한 1차 검증이다.

```text
아이디 형식 검사
이메일 형식 검사
중복 확인 여부 검사
```

하지만 JS는 우회 가능하다.

따라서 서버에서는 반드시 최종 검증이 필요하다.

```text
DTO Validation
Service 중복 검사
DB 제약조건
```

현재 구조:

```text
JSP/JS
→ 사용자 편의 검증

Controller + BindingResult
→ 입력값 검증

Service
→ 비즈니스 검증

DB
→ 최종 데이터 저장
```

---

# 느낀 점

회원가입 기능은 단순 CRUD가 아니라 다음 흐름을 함께 이해해야 하는 기능이었다.

```text
JSP 렌더링
Form submit
DTO 바인딩
Validation
BindingResult
Service 검증
JS 비동기 통신
```

특히 Spring MVC에서:

```text
요청
→ Controller
→ Validation
→ Service
→ JSP 재렌더링
```

흐름을 직접 경험할 수 있었다.