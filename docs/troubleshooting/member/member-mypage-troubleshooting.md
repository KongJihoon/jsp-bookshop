# 회원 인증 및 마이페이지 구현 중 트러블 슈팅

## 1. 회원정보 조회 시 MemberNotFoundException 발생

### 문제 상황

회원정보 조회 화면(`/member/info`) 접근 시 다음 예외가 발생하였다.

```text
    MemberNotFoundException: 사용자를 찾을 수 없습니다.
```

### 원인 분석

로그인 성공 후 세션에 저장된 `SessionMemberDto`의 `memberId`값이 `null`이었다.

원인을 확인해보니 Mybatis 조회 결과에서 DB 컬럼명 `member_id`가 Java 필드명 memberId로 정상 매핑되지 않았다.

### 해결 과정

Mybatis의 snake_case -> camelCase 자동 매핑 설정을 추가.

또는 SQL에서 Alias를 명시적으로 지정하여 해결할 수 있다.

```sql
SELECT
    member_id AS memberId,
    login_id AS loginId
FROM member
```

### 배운 점

MyBatis를 사용할 때 DB 컬럼명과 Java 필드명이 다르면 자동 매핑 여부를 반드시 확인해야 한다.

특히 snake_case와 camelCase를 함께 사용하는 프로젝트에서는 자주 발생하는 문제이므로 초기에 설정하는 것이 좋다.

---

## 2. 로그인 사용자가 로그인 페이지에 접근 가능한 문제

### 문제 상황

로그인 성공 후에도 다음 URL 접근이 가능한 문제가 있었다.

```text
/member/login
/member/signup
```

이미 로그인한 사용자가 다시 로그인 페이지나 회원가입 페이지에 접근이 가능한 상태이다.

### 원인 분석

로그인 여부를 검사하는 공통 로직이 존재하지 않았다.

### 해결 과정

Spring MVC Interceptor를 도입하여 로그인 여부를 검사하는 공통 로직을 구현하였다.

```java
public class GuestOnlyInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {

        HttpSession session = request.getSession(false);

        if (session != null &&
            session.getAttribute(SessionConst.LOGIN_MEMBER) != null) {

            response.sendRedirect("/");
            return false;
        }

        return true;
    }
}
```

그리고 `WebConfig`에 등록하였다.

```java
    registry.addInterceptor(new GuestOnlyInterceptor())
            .addPathPatterns(
                    "/member/login",
                    "/member/signup"
            );
```

### 배운 점 

Controller마다 로그인 여부를 검사하는 것보다 Interceptor를 이용하여 공통 관심사를 분리하는 것이 유지보수에 훨씬 유리하다.

💡 프로젝트가 완성된 이후에는 Spring Security를 도입하여 인증 및 인가 처리를 SecurityConfig 중심으로 통합 관리할 예정이다.
이를 통해 현재의 세션 기반 인증 구조와 Spring Security 기반 인증 구조를 모두 경험하고, 각 방식의 장단점을 비교하며 학습할 계획이다.