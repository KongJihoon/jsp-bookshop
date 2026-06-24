# 장바구니 조회 및 수량 변경 트레이드오프

## 배경

장바구니 담기 기능 구현 이후, 사용자가 자신의 장바구니 상품 목록을 조회하고 수량을 변경할 수 있도록 기능을 확장했다.

장바구니 화면에서는 단순 상품의 출력뿐 아니라 다음과 같은 기능이 필요했다.

- 장바구니 상품 목록 조회
- 상품별 수량 출력
- 상품별 총 금액 계산
- 전체 선택 및 개별 선택 기능
- 선택된 상품 기준 주문 예상 금액 계산
- 수량 변경 시 DB에 반영

---

## 트레이드 오프 1. 장바구니 조회 결과를 List로 내려줄 것인가, CartResponse로 감싸서 내려줄 것인가.

### 대안 1. List<CartItemResponse>만 반환

```java
List<CartItemResponse> items = cartMapper.findCartItemsByMemberId(memberId);
```

#### 장점
- 구조가 단순하다.
- JSP에서 반복 출력하기 쉽다.

#### 단점
- 장바구니 전체 금액 같은 화면 단위 데이터가 별도로 필요하다.
- JSP에서 합계 계산을 해야 할 가능성이 높다.
- 주문 기능으로 확장 시 응답 구조가 복잡해진다.

---

### 대안 2. CartResponse로 감싸서 반환

```java
public class CartResponse {
    private final List<CartItemResponse> items;
    private final Integer totalPrice;
}
```

#### 장점
- 장바구니 화면에 필요한 데이터를 하나의 응답 객체로 묶을 수 있다.
- 총 상품 금액 계산 책임을 JSP가 아닌 Java 코드로 이동
- 이후 배송비, 선택 상품 금액, 결제 예정 금액 등 확장성이 높다.
- JSP는 계산보다 출력에 집중.

#### 단점
- DTO 클래스 추가
- 단순 목록 조회에 비해 구조가 조금 늘어난다.

#### 결정
`CartResponse` DTO를 추가하였다.
장바구니 화면은 단순 상품 목록 조회가 아니라, 합계 금액과 이후 주문 기능 확장 시 화면 단위 데이터가 필요하다.


---

## 트레이드 오프 2. 장바구니 합계를 JSP에서 계산할 것인가, 서버에서 계산할 것인가.

### 대안 1. JSP에서 계산

```jsp
<c:set var="totalPrice" value="${totalPrice + cartItem.price * cartItem.quantity}"/>
```

#### 장점
- 별도 DTO 계산 로직이 필요하지 않다.
- 화면에서 바로 처리 가능

#### 단점
- JSP가 계산 책임까지 가진다.
- 화면 코드가 복잡해진다.
- 테스트가 어려워진다.
- 주문 화면 등 다른 곳에서 동일 계산이 필요하면 중복 코드 증가.

---

### 대안 2. 서버에서 계산

```java
this.totalPrice = items.stream()
        .mapToInt(CartItemResponse::getItemTotalPrice)
        .sum();
```

#### 장점
- 계산 로직을 Java 코드에서 관리
- 테스트 가능
- JSP는 출력만 담당
- 주문 기능 확장성

#### 단점
- 화면에서 선택 해제한 상품 기준 합계는 Javascript에서 다시 계산.

#### 결정
초기 전체 합계는 `CartResponse`에서 계산하고, 사용자 체크박스 선택 계산은 Javascript로 넘긴다.

---

## 트레이드 오프 3. 수량 변경을 form submit으로 처리 or fetch API 활용

### 대안 1. form submit + redirect

```text
POST /cart/items/{cartItemId}/quantity
→ redirect:/cart
```

#### 장점
- JSP 프로젝트 흐름에 잘 맞는다.
- 구현이 단순
- 기존 toast/redirect 패턴 재사용

#### 단점
- 수량 버튼 클릭 시 페이지 새로고침
- UX가 떨어질 수 있다.
- 현재 구현한 실시간 합계 계산 UI와 어울리지 않는다.


---

### 대안 2. fetch API + JSON 응답

```text
POST /cart/items/{cartItemId}/quantity
→ JSON 응답
→ 화면 일부 갱신
```

#### 장점
- 페이지 새로고침 없이 수량 변경 가능
- 사용자가 즉시 변경 금액 확인
- 현재 장바구니 UI의 전체 선택/ 합계 계산 흐름과 잘 맞는다.

#### 단점
- JSP 프로젝트 안에 일부 JSON API 흐름이 섞인다.
- fetch 요청 전용 에러 응답 처리 필요
- 서버 flashAttribute 기반 toast 재사용 불가

#### 결정
수량 변경은 `fetch API + JSON 응답`으로 구현했다.

장바구니 수량 변경은 화면 일부만 갱신하면 되는 기능이고, 버튼 클릭 시마다 전체 페이지를 새로고침하는것은 UX가 좋지 않다고 판단했다.

