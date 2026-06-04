# Session에 어떤 객체를 저장할 것인가.

## 1. 배경

로그인 성공 후 Session에 어떤 데이터를 저장해야되는지 고민이 되었다.

### 후보

``` java
Member
```

``` java
MemberLoginResponse
```

``` java
Long MemberId
```


## 2. 선택지 비교

### (1) Member 객체 저장

#### 장점
- 구현이 단순하다.
- JSP에서 사용자 정보 접근이 쉽다.

#### 단덤
- Session에 불필요한 데이터가 저장될 수 있다.
- 사용자 정보 변경 시 Session 데이터와 DB 데이터가 달라질 수 있다.


### (2) DTO 저장



#### 장점

- 필요한 정보만 저장 가능
- Entity 노출 최소화

#### 단점

- DTO 추가 관리 필요
- DTO 변경 시 Session 구조도 변경될 수 있음

### (3) memberId만 저장

#### 장점

- Session 사용량 최소화
- 항상 최신 사용자 정보 조회 가능

#### 단점

- 요청마다 DB 조회 발생
- 구현 복잡도 증가

## 최종 선택

현재 프로젝트 규모에서는 Member 객체 저장 방식이 가장 적절하다고 판단하였다.

추후 Spring Security 또는 Redis Session 적용 시 저장 전략을 다시 검토할 예정이다.