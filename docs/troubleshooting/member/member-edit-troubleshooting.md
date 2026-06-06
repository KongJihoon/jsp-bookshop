# 회원 정보 수정 기능 구현 중 트러블 슈팅 정리

## 1. 회원정보 수정 시 이메일 중복 검증 문제

### 문제

회원가입에서 사용하던 이메일 중복 검증 로직을 회원정보 수정에도 그대로 적용하였다.
```java
if (memberMapper.existsByEmail(request.getEmail())) {
    throw new DuplicateMemberException("email", "이메일");
}
```

이로 인해 회원정보 수정 시 기존 이메일과 동일한 이메일을 입력해도 중복으로 인식하여 예외가 발생했다.

---

### 원인

회원가입과 회원정보 수정은 이메일 중복 검증의 기준이 다르다.

```text
현재 DB에 존재하면 중복
```

회원정보 수정

```text
현재 로그인한 회원은 제외하고 검사
```

즉, 현재 사용자의 이메일은 허용되어야 한다.

---

### 해결

현재 회원을 제외한 이메일 중복 여부를 확인하는 쿼리문을 추가하였다.

Mapper
```java
boolean existsByEmailAndMemberIdNot(String email, Long memberId);
```

Mybatis XML
```xml
<select id="existsByEmailAndMemberIdNot" resultType="boolean">
    SELECT EXISTS (
        SELECT 1
        FROM member
        WHERE email = #{email}
        AND member_id != #{memberId}
        AND withdrawal_date IS NULL
    )
</select>
```


Service

```java
if (memberMapper.existsByEmailAndMemberIdNot(
        request.getEmail(),
        memberId
)) {
    throw new DuplicateMemberException("email", "이메일");
}
```

--- 

### 결과

```text
회원가입
→ 이메일 존재 여부 검사

회원정보 수정
→ 현재 회원을 제외한 이메일 존재 여부 검사
```

동일한 중복 검증이라도 비즈니스 요구사항에 따라 조건이 달라질 수 있음을 확인할 수 있었다.

---

## 2. JPA와 Mybatis의 Update 방식 차이

### 문제

기존 Spring Boot + JPA 프로젝트에서는 엔티티 상태만 변경하면 수정이 자동으로 반영되었다.

하지만 JSP + Mybatis 프로젝트에서는 명시적으로 Update 쿼리를 작성하여 실행해야 했다.


### 원인

JPA는 영속성 컨텍스트와 더티채킹(Dirty Checking) 메커니즘을 통해 엔티티의 상태 변화를 감지하여 자동으로 Update 쿼리를 실행한다.

```text
엔티티 조회
→ 상태 변경
→ 트랜잭션 종료
→ 변경 감지
→ UPDATE SQL 자동 실행
```

반면 Mybatis는 SQL Mapper이므로 객체 상태를 변경해도 DB에 반영되지 않는다.

---

### 해결

도메인 객체 상태 변경 후 Mapper의 update()를 직접 호출하도록 수정하였다.

```java
member.updateMemberInfo(
        request.getEmail(),
        request.getPhone(),
        request.getZipcode(),
        request.getAddress(),
        request.getAddressDetail()
);

memberMapper.update(member);
```

---

### 결과

JPA

```text
상태 변경
→ 더티 체킹
→ 자동 UPDATE
```

MyBatis

```text
상태 변경
→ update SQL 직접 호출
```

JPA와 MyBatis의 데이터 수정 방식 차이를 다시 한번 확인할 수 있었다.

---

## 3. 회원정보 수정 시 세션 정보 갱신 여부 고민

### 문제

### 문제

회원정보 수정 기능을 구현하면서 수정 완료 후 Session에 저장된 사용자 정보를 다시 갱신해야 하는지 고민하였다.

현재 Session에는 다음 정보만 저장되어 있다.

```java
public class SessionMemberDto {

    private Long memberId;

    private String loginId;

    private String name;

}
```

---

### 분석

현재 수정 가능한 정보

```text
이메일
전화번호
우편번호
주소
상세주소
```

현재 Session 저장 정보

```text
memberId
loginId
name
```

즉 수정 대상과 Session 저장 대상이 겹치지 않는다.

---

### 결론

현재 구조에서는 Session 갱신이 필요하지 않다.

```text
세션 정보
→ memberId
→ loginId
→ name

수정 정보
→ email
→ phone
→ address
```

다만 향후 이름 수정 기능을 추가한다면 Session에 저장된 name 값도 함께 갱신해야 한다.

이를 통해 Session에는 최소한의 정보만 저장하는 것이 유지보수에 유리하다는 점을 다시 확인할 수 있었다.