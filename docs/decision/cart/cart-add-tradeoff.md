# 장바구니 기능 구현 트레이드 오프

## 배경

상품 상세 페이지에서 사용자가 원하는 수량을 선택한 뒤 장바구니에 상품을 담을 수 있도록 기능을 구현하였다.

장바구니는 주문 전 임시 보관 영역이기 때문에 상품, 회원, 주문 흐름 사이에 중간 역할을 한다.
따라서 단순히 상품 ID만 저장하는 것이 아니라 회원별 장바구니, 장바구니 상품, 수량 증가, 재고 검증, 로그인 검증 등을 함께 고려해야 한다.

## 테이블 설계

### 선택한 구조

```text
member 1 : 1 cart
cart 1 : N cart_item
product 1 : N cart_item
```

```sql
cart
- cart_id
- member_id
- created_at
- updated_at

cart_item
- cart_item_id
- cart_id
- product_id
- quantity
- created_at
- updated_at
```

### 선택 이유

회원과 장바구니를 직접 연결하고, 장바구니 상품은 별도 `cart_item`으로 분리하였다.

이를 통해 다음과 같은 확장이 가능하다.
- 회원별 장바구니 관리
- 장바구니 상품 여러 개 저장
- 같은 상품 중복 담기 방지
- 장바구니 단위 기능 확장
- 주문 생성 시 장바구니 상품 기반 주문 처리

---
## 트레이드 오프 1. cart 테이블을 둘 것인가, cart_item에 member_id만 둘 것인가.

### 대안 1. cart_item에 member_id 직접 저장

```text
cart_item
- cart_item_id
- member_id
- product_id
- quantity
```

#### 장점
- 테이블 구조가 단순하다.
- 장바구니 조회 쿼리가 간단하다.
- 초기 구현 속도가 빠르다.

#### 단점
- 장바구니 자체를 표현하는 개념이 없다.
- 장바구니 단위 상태나 생성일 관리가 어렵다.
- 비회원 장바구니, 선택 상품 주문, 장바구니 만료 같은 기능 확장이 불편하다.

### 대안 2. cart + cart_item 분리
```text
cart
cart_item
```

#### 장점
- 장바구니와 장바구니 상품의 책임이 분리된다.
- 회원은 하나의 장바구니를 가진다는 제약을 명확하게 표현할 수 있다.
- 주문 기능으로 확장하기 편리하다.
- 장바구니 단위 속성을 추가하기 쉽다.

#### 단점
- 테이블 증가
- 장바구니 담기 시 cart 조회 또는 생성 로직이 필요하다.
- 쿼리와 서비스 로직이 조금 복잡해진다.

### 결정
`cart`와 `cart_item`을 분리했다.
쇼핑몰 도메인에서 장바구니는 주문 전 중요한 중간 매개체이다. 이후 주문/배송 기능과 연결될 가능성이 높기 때문이다.

---

## 트레이드 오프 2. 장바구니에 가격을 저장할 것인가

### 대안 1. cart_item에 가격 저장

```text
cart_item
- product_id
- quantity
- price
```

#### 장점
- 장바구니에 담은 시점의 가격을 보존할 수 있다.
- 조회 시 `product`를 조인하지 않아도 금액을 계산할 수 있다.

#### 단점
- 상품 가격이 변경되었을 경우 장바구니 가격과 실제 상품 가격이 다를 수 있다.
- 가격 변경 안내 로직이 추가적으로 필요하다.

### 대안 2. cart_item에는 수량만 저장하고, 가격은 product에서 조회

```text
cart_item
- product_id
- quantity
```

```sql
p.price * ci.quantity AS item_total_price
```

#### 장점
- 항상 현재 상품 가격 기준으로 장바구니 금액 계산이 가능하다.
- 데이터 중복이 줄어든다.

#### 단점
- 조회 시 `product` 조인 필요
- 담은 당시 가격은 보존하지 않는다.

### 결정
장바구니에는 가격을 저장하지 않고 수량만 저장했다.

가격 스냅샷은 실제 구매가 확정되는 주문 시점에 `order_item.order_price`로 저장하는 것이 더 적절하다고 판단하였다.

---

## 트레이드 오프 3. 같은 상품을 다시 담을 때 row를 추가할 것인가, 수량을 증가시킬 것인가

### 대안 1. 같은 상품도 cart_item에 row 추가

```text
cart_item_id | cart_id | product_id | quantity
1            | 1       | 10         | 1
2            | 1       | 10         | 2
```

#### 장점
- insert만 수행하면 비교적 구현이 단순하다.

#### 단점
- 같은 상품이 장바구니에 여러 줄로 노출된다.
- 총 수량 계산이 복잡해진다.
- UX가 좋지 않다.


### 대안 2. 같은 상품이면 quantity 증가

```text
cart_item_id | cart_id | product_id | quantity
1            | 1       | 10         | 3
```

#### 장점
- 같은 상품이 한 줄로 유지
- 수량 변경과 주문 처리 로직이 단순해진다.

#### 단점
- 기존 cart_item 조회 후 분기 처리가 필요하다.
- 동시성 상황에서는 중복 insert 또는 수량 경쟁 문제가 발생할 수 있다.

### 결정
같은 상품이 이미 장바구니에 존재하면 quantity를 증가시키도록 구현하였다.
```java
cartMapper.findCartItemByCartIdAndProductId(cart.getCartId(), product.getProductId())
        .ifPresentOrElse(
                cartItem -> increaseCartItemQuantity(cartItem, product, quantity),
                () -> saveNewCartItem(cart, product, quantity)
        );
```
DB에는 `UNIQUE(cart_id, product_id)` 제약을 두어 중복 상품 row 생성을 방지할 수 있도록 설계했다.

