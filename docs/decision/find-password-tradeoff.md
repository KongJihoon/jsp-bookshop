# 비밀번호 찾기 이메일 인증 기능 트레이드 오프

## 배경

비밀번호 찾기 기능은 사용자의 계정 접근 권한을 복구하는 기능이다.

단순히 아이디와 이메일이 일치한다는 이유로 비밀번호를 복구할 수 있게 하면 보안적으로 위험할 수 있다.

따라서 비밀번호 재설정 기능에는 이메일 인증번호로 검증하는 단계를 추가하였다.

이메일 인증 기능은 다음과 같은 요구사항을 함께 고려해야 한다.

- 로그인 아이디와 이메일 기반 1차 회원 검증
- 인증번호 생성 및 이메일 발송
- 인증번호 만료 시간 관리
- 인증번호 입력 화면에서 남은 시간 표시
- 인증 성공 후에만 비밀번호 변경 허용
- 비밀번호 변경 완료 후 인증 상태 제거

---

## 선택한 구조

```text
1. 사용자가 로그인 아이디와 이메일 입력
2. DB에서 loginId + email + withdrawn_at IS NULL 조건으로 회원 검증
3. 인증번호 생성
4. Redis에 인증번호 저장
5. Google SMTP를 통해 인증번호 비동기 전송
6. Session에 비밀번호 재설정 대상 loginId/email 저장
7. 사용자 인증번호 입력
8. Redis 인증번호 검증
9. 인증 성공 시 Redis에 verified 상태 저장
11. 새 비밀번호 입력
12. 비밀번호 변경
13. Redis 인증 정보와 Session 정보 제거
```

---

## 트레이드 오프 1. 인증번호를 DB에 저장할 것인가, Redis에 저장할 것인가.

### 대안 1. DB 테이블에 인증번호 저장

```text
password_reset_token
- token_id
- member_id
- code
- expired_at
- verified
- created_at
```

#### 장점
- 인증 요청 이력을 DB에 남길 수 있다.
- 운영 중 인증번호 발송 기록을 추적하기 쉽다.
- Redis 같은 외부 저장소가 필요없다.

#### 단점
- 만료된 인증번호를 주기적으로 삭제해야한다.
- 인증번호와 같은 임시 데이터를 DB에 계속 저장하게 된다.
- TTL 처리를 직접 구현해야 한다.

### 대안 2. Redis 인증번호 저장

```text
password-reset:code:{loginId}:{email}
```

#### 장점
- TTL을 사용해 인증번호 만료를 자연스럽게 처리할 수 있다.
- 인증번호처럼 짧게 사용되는 임시 데이터에 적합하다.
- DB에 불필요한 임시데이터를 저장하지 않는다.

#### 단점
- Redis 의존성이 추가된다.
- Redis 장애 시 인증번호 검증 기능이 영향을 받는다.
- 운영 환경에서는 Redis 설정과 모니터링이 필요하다.

### 결정
인증번호는 Redis에 저장했다.
비밀번호 찾기 인증번호는 장기 보존이 필요한 데이터가 아니라 짧은 시간 동안만 유효한 임시 데이터이다.
따라서 TTL 관리가 쉬운 Redis를 사용하는 것이 더 적절하다고 판단했다.

---

## 트레이트 오프 2. loginId/email을 hidden input으로 넘길 것인가, Session에 저장할 것인가.

### 문제 상황
비밀번호 찾기 흐름은 여러 요청으로 나뉜다.
```text
POST /member/find-password/email
POST /member/find-password/verify
POST /member/find-password/reset
```

처음 요청에서 검증한 `loginId`, `email`을 이후 인증번호 확인과 비밀번호 변경 요청에서도 알아야 한다.

### 대안 1. hidden input으로 loginId/email 전달
```jsp
<input type="hidden" name="loginId" value="${loginId}">
<input type="hidden" name="email" value="${email}">
```

#### 장점
- 구현이 단순하다.
- 서버 세션에 의존하지 않는다.
- 요청마다 필요한 값을 명시적으로 전달할 수 있다.

#### 단점
- hidden input은 사용자가 조작할 수 있다.
- JSP 화면에 재설정 대상 정보가 계속 포함된다.

### 대안 2. Session에 loginId/email 저장

```java
session.setAttribute(SessionConst.PASSWORD_RESET_LOGIN_ID, request.getLoginId());
session.setAttribute(SessionConst.PASSWORD_RESET_EMAIL, request.getEmail());
```

#### 장점
- 재설정 대상 정보를 클라이언트에 노출하지 않아도 된다.
- 인증번호 확인과 비밀번호 변경 단계에서 서버가 대상 회원을 유지할 수 있다.
- 현재 프로젝트가 Session 기반 인증 구조이므로 흐름이 자연스럽다.

#### 단점
- 세션 상태에 의존한다.
- 여러 탭에서 다른 계정의 비밀번호 찾기를 동시에 진행하면 값이 덮어씌워질 수 있다.
- 세션 만료 시 비밀번호 찾기를 다시 진행해야 한다.

### 결정

`loginId`, `email`은 Session에 저장했다.

현재 프로젝트는 Session 기반 로그인 구조를 사용하고 있으며, JSP 서버 사이드 렌더링 방식이다.

따라서 비밀번호 재설정 대상 정보를 hidden input으로 노출하기 보다 서버 세션에 보관하는 방식이 더 자연스럽다고 판단하였다.

---

## 트레이드 오프 3. 메일 발송을 동기 처리할 것인가, 비동기 처리할 것인가.

### 대안 1. 동기 메일 발송

#### 장점
- 구현이 단순해진다.
- 메일 발송 성공/ 실패를 요청 흐름 안에서 바로 확인 가능하다.

#### 단점
- SMTP 응답이 느리면 사용자 요청도 함께 느려진다.
- 외부 메일 서버 상태에 따라 화면 응답 시간이 영향을 받는다.

### 대안 2. 비동기 메일 발송
```java
@Async
public void sendPasswordResetCode(String to, String code) {
    mailSender.send(message);
}
```

#### 장점
- 사용자는 메일 발송 요청 후 빠르게 다음 화면으로 이동할 수 있다.
- SMTP 지연이 전체 요청 응답 시간을 막지 않는다.
- 외부 I/O 작업을 비동기로 분리할 수 있다.

#### 단점
- 메일 발송 실패를 사용자에게 즉시 알려주기 어렵다.
- 운영 환경에서는 비동기 예외 로깅과 재시도 정책이 필요하다.

### 결정
메일 발송은 JavaMailSender와 @Async를 사용해 비동기로 처리했다.

비밀번호 찾기 화면에서는 인증번호 입력 화면으로 빠르게 이동하고, 실제 SMTP 전송은 별도 스레드에서 수행하도록 분리했다.

---

## 최종 선택

최종적으로 다음과 같은 구조를 선택하였다.
```text
1. loginId + email로 회원 1차 검증
2. Redis에 인증번호 저장, TTL 3분 설정
3. Google SMTP로 인증번호 비동기 발송
4. Session에 재설정 대상 loginId/email 저장
5. 인증번호 입력 화면에서 Redis TTL 기반 타이머 표시
6. 인증 성공 시 Redis에 verified 상태 저장, TTL 10분 설정
7. 새 비밀번호 입력 후 비밀번호 변경
8. Redis 인증 정보 삭제
9. Session 재설정 대상 정보 삭제
```