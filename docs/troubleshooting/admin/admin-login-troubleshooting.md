# 관리자 구현 중 PasswordEncoder 관련 문제 해결


## 1. 문제 상황

관리자 로그인 기능 구현 후 정상적인 아이디와 비밀번호를 입력했음에도 로그인이 실패하고

관리자 권한 검증 예외가 발생하지 않는 문제가 발생했다.


```text
아이디 또는 비밀번호가 일치하지 않습니다.
```

DB에 저장된 관리자 계정 정보와 입력값을 확인했지만 동일한 문제가 반복적으로 발생하였다.

---

## 2. 원인 분석

비밀번호 검증 시 `PasswordEncoder`의 `matches()` 메서드의 인자 순서를 잘못 사용하고 있었다.

기존 코드

```java
if (!passwordEncoder.matches(
        member.getPassword(),
        request.getPassword()
)) {
    throw new AdminLoginFailedException(
            "아이디 또는 비밀번호가 일치하지 않습니다."
    );
}
```

`member.getPassword()`는 BCrypt로 암호화된 비밀번호이고,

`request.getPassword()`는 사용자가 입력한 평문 비밀번호이다.

하지만 `matches()`는 다음 순서로 호출해야 한다.

```java
matches(
        rawPassword,
        encodedPassword
)
```

즉,

```java
matches(
        사용자 입력 비밀번호,
        DB 암호화 비밀번호
)
```

순서로 전달해야 한다.

---

## 3. 해결

인자 순서를 올바르게 수정하여 문제를 해결했다.

수정 코드

```java
if (!passwordEncoder.matches(
        request.getPassword(),
        member.getPassword()
)) {
    throw new AdminLoginFailedException(
            "아이디 또는 비밀번호가 일치하지 않습니다."
    );
}
```

---

## 4. 결과

- 관리자 로그인 정상 동작
- USER 계정 로그인 검증 정상 동작
- ADMIN 권한 검증 단계 정상 진입
- 관리자 대시보드 접근 가능

## 5. 회고

단순한 실수였지만 인자 순서를 반대로 전달할 항상 검증에 실패하므로 로그인 기능 구현 시 주의가 필요하다.