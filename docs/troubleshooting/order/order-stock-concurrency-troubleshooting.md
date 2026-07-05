# 주문 재고 차감 동시성 트러블 슈팅

## 문제 상황

주문 기능 구현 후, 재고보다 많은 사용자가 동시에 요청을 보내었을 때, 재고의 초과 판매가 발생하는지 확인이 필요했다.

예를 들어, 5개의 상품에 대해 10명의 사용자가 동시에 1개씩 주문하면 다음 조건을 만족해야한다.

```text
성공 주문 수 = 5
실패 주문 수 = 5
최종 재고 = 0
주문 상품 수 = 5
장바구니에 남은 상품 수 = 5
```

단순히 재고를 조회한 뒤 애플리케이션에서 수량을 비교하고 차감하면, 동시에 여러 요청이 들어왔을 때 초과 판매가 발생할 수 있다.

---

## 원인 분석

초기에는 재고 차감 동시성 문제를 해결하는 방법에 대하여 아래와 같은 방법을 생각하였다.
- Java synchronized
- 비관적 락 SELECT FOR UPDATE
- Redis 분산락
- 조건부 UPDATE

하지만 현재 요구사항에서는 단일 상품 row의 재고를 차감하는 경우가 많고, MySQL의 InnoDB의 row-level lock을 활용할 수 있다.

따라서 별도 애플리케이션 락을 먼저 도입하지 않고, 재고 검증과 차감을 하나의 SQL문에서 처리하는 방식을 선택했다.

---

## 해결 방법
재고 차감 쿼리를 조건부 UPDATE로 작성하였다.

```sql
UPDATE product
SET stock_quantity = stock_quantity - #{quantity}
WHERE product_id = #{productId}
AND deleted_at IS NULL
AND status = 'ACTIVE'
AND stock_quantity >= #{quantity}
```

이 쿼리는 다음과 같은 상황을 하나의 UPDATE문에서 처리한다.
```text
1. 상품이 존재하는지 확인
2. 판매중 상태인지 확인
3. 재고가 충분한지 확인
4. 재고 차감
```
재고가 충분하면 update count가 1이 되고, 재고가 부족하면 update count가 0이 된다.

서비스에서는 update count가 0일 때 재고 부족 예외를 발생시켰다.

```java
int stockUpdateCount =
        orderMapper.decreaseProductStock(item.getProductId(), item.getQuantity());

if (stockUpdateCount == 0) {
    throw new StockQuantityExceedException("재고 수량을 초과한 상품이 있습니다.");
}
```
`createCartOrder()`는 `@Transactional`로 묶여 있으므로, 재고 차감 실패 시 이전에 저장된 주문 데이터도 함께 롤백된다.

---

## 동시성 테스트 구성

실제 MySQL의 row-level lock과 조건부 UPDATE 동작을 검증해야 하므로 Mockito 단위 테스트가 아니라 Spring Boot 통합 테스트로 작성하였다.

테스트 조건은 다음과 같다.

```text
상품 재고: 5
동시 주문 요청: 10
각 요청 수량: 1
```
각 스레드는 서로 다른 회원과 서로 다른 장바구니 상품을 사용하지만, 같은 상품을 주문하도록 구성
```text
thread 0 -> memberId 910001, cartItemId 930001
thread 1 -> memberId 910002, cartItemId 930002
...
thread 9 -> memberId 910010, cartItemId 930010
```

---

## 테스트 결과

동시성 테스트 결과는 다음과 같았다.
```text
성공 주문 수 = 5
실패 주문 수 = 5
최종 재고 = 0
생성된 주문 수 = 5
생성된 주문 상품 수 = 5
남은 장바구니 상품 수 = 5
```

SQL 로그에서는 재고가 남아 있는 요청은 update count가 1로 처리되었고, 재고가 부족한 요청은 update count가 0으로 처리되었다.

데드락은 발생하지 않았다.
현재 테스트는 데드락 재현 테스트가 아니라 초과 판매 방지 테스트이다.
같은 상품 row 하나에 대해 여러 요청이 동시에 UPDATE를 수행하면 InnoDB가 row lock을 통해 순차적으로 처리하며, 재고 부족 요청은 조건부 UPDATE의 영향 행 수가 0건으로 반환된다.