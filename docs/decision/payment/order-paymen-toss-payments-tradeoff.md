# 토스 페이먼츠 API 결제 연동 트레이드오프

## 배경

기존 주문 기능은 사용자가 주문 버튼을 누르면 즉시 주문이 생성되고, 재고 차감 및 해당 주문 장바구니 상품 삭제까지 한 번에 처리되는 구조다.

```text
주문하기
-> 주문 생성
-> 재고 차감
-> 장바구니 상품 삭제
-> 주문 완료
```
이 구조는 결제 기능이 존재하지 않는다면 단순하고 이해하기 쉽지만, 실제 쇼핑몰 흐름과는 차이가 크다.

결제 기능이 추가되면 주문은 다음과 같은 단계로 분리해야된다.
```text
주문하기
-> 주문 대기 생성(READY)
-> 결제창 호출
-> 결제 승인
-> 주문 확정
-> 재고 차감
-> 장바구니 상품 삭제
-> 주문 완료
```

---

## 전체 흐름

현제 결제 흐름은 다음과 같다.
```text
1. 사용자가 장바구니에서 주문할 상품 선택
2. 주문서 화면 진입
3. 배송 정보 작성
4. 주문하기 버튼 클릭
5. order 테이블에 READY 상태의 주문 생성
6. order_item 테이블에 주문 상품 스냅샷 저장
7. payment 테이블에 READY 결제 정보 저장
8. 토스페이먼츠 결제창 호출
9. 결제 성공 시 /payments/toss/success로 redirect
10. 서버에서 토스 결제 승인 API 호출
11. 결제 금액 검증
12. payment 상태 변경 -> PAID
13. orders 상태 -> PAID 
14. 상품 재고 차감
15. 장바구니 상품 삭제
16. 주문 완료 화면 이동.
```

실패 흐름은 다음과 같다.
```text
1. 결제창에서 사용자가 결제 취소 또는 결제 실패
2. checkout.jsp의 catch 실행
3. /payments/toss/fail로 이동
4. payment 상태 FAILED 변경
5. orders 상태 FAILED 변경
6. 장바구니는 유지
7. 장바구니 화면으로 redirect
8. 실패 메시지 toast 출력
```

---

## 테이블 설계

**orders**

주문 자체의 정보를 저장
```text
orders
- order_id
- member_id
- order_status
- receiver_name
- receiver_phone
- zipcode
- address
- address_detail
- total_price
- ordered_at
- canceled_at
```

`order_status`는 결제 및 배송 흐름을 표현한다.
```java
READY("주문대기"),
PAID("결제완료"),
PREPARING("배송준비"),
SHIPPING("배송중"),
DELIVERED("배송완료"),
CANCELED("주문취소"),
FAILED("결제실패")
```

---

**order_item**

```text
order_item
- order_item_id
- order_id
- cart_item_id
- product_id
- product_name
- price
- quantity
- item_total_price
- created_at
```

---

**payment**

```text
payment
- payment_id
- order_id
- toss_order_id
- payment_key
- amount
- payment_method
- payment_status
- approved_at
- failed_reason
- created_at
- updated_at
```

`payment_status`는 결제 자체의 상태를 표현

```java
READY("결제대기"),
PAID("결제완료"),
FAILED("결제실패"),
CANCELED("결제취소")
```

`payment_method`는 실제 결제 수단을 저장한다.

```java
CARD("카드"),
TOSS_PAY("토스페이"),
KAKAO_PAY("카카오페이"),
NAVER_PAY("네이버페이"),
PAYCO("페이코"),
EASY_PAY("간편결제"),
UNKNOWN("알 수 없음")
```

---

## 트레이드 오프 1. 주문 생성과 결제 승인 기능의 분리

### 대안 1. 주문 버튼 클릭 시 바로 주문 확정

```text
주문하기
-> 주문 생성
-> 재고 차감
-> 장바구니 삭제
-> 결제창 호출
```

#### 장점
- 기존 주문 로직의 흐름과 큰 차이점이 없다.
- 기존 주문 흐름에 결제 기능을 마지막에 추가해주면 된다.


#### 단점
- 결제 실패 시 이미 차감한 재고를 복구하는 로직이 필요하다.
- 결제 실패 시 삭제한 장바구니 상품의 복구 로직 필요.
- 결제되지 않은 주문도 완료 주문처럼 보일 가능성이 있다.

### 대안 2. 주문 대기 상태를 먼저 생성하고 결제 성공 후 확정

```text
주문하기
-> 주문 READY 생성
-> payment READY 생성
-> 결제창 호출
-> 결제 성공
-> 주문 PAID 변경
-> 재고 차감
-> 장바구니 삭제
```

#### 장점
- 결제 성공 전에 주문과 결제 완료 주문을 명확히 구분할 수 있다.
- 결제 실패 시 재고 및 장바구니 상품의 복구 로직이 따로 필요하지 않다.
- 결제 승인 이후에만 주문을 확정하므로 실제 쇼핑몰 흐름과 유사하다.

#### 단점
- 결제 실패 주문과 READY 주문을 어떻게 관리할지 추가 정책이 필요하다.
- 기존 주문 생성 로직의 분리가 필요하다.

### 결정

`orders`는 먼저 `READY`상태로 저장하고, 결제 승인 성공 후 `PAID`상태로 변경하는 구조를 선택했다.

결제 성공 전까지는 재고를 차감하지 않고 장바구니도 삭제하지않는다.

---

## 트레이드 오프2. 결제 정보를 orders 테이블에 넣을지 payment 테이블로 분리할 것인가

### 대안 1. orders테이블에 결제 정보 컬럼 추가

```text
orders
- payment_key
- payment_method
- payment_status
- approved_at
```

#### 장점
- 테이블 수가 늘어나지 않는다.
- 주문 조회 시 JOIN 불필요.
- 구현이 단순하다.

#### 단점
- 주문 정보와 결제 정보의 책임이 섞여버린다.
- 주문 테이블에 결제 관련 정보들이 엮여서 테이블이 비대해진다.

### 대안 2. payment 테이블로 분리

#### 장점
- 주문과 결제의 책임 분리
- 결제수단 확장, 결제 취소, 환불 기능으로 확장성 용이.
- 주문 상태와 결제 상태를 분리하여 표현할 수 있다.

#### 단점
- 주문 상세 조회 시 payment테이블 JOIN 필요
- 초기 구현량이 늘어난다.

### 결정

주문과 결제는 성격이 다르므로 `payment`테이블을 별도로 분리했다.

`orders.order_status`는 주문의 흐름을 표현하고, `payment.payment_status`는 결제 흐름을 표현하도록 분리.


---

## 트레이드 오프4. 결제 금액을 클라이언트 값으로 저장할 것인가.

### 문제 상황

토스 결제 성공 후 successUrl로 다음 값들이 전달된다.
```text
paymentKey
orderId
amount
```

여기서 `amount`는 클라이언트 흐름을 통해 전달되는 값이다.
사용자가 요청 값을 조작할 가능성을 고려하면 이 값을 그대로 저장하는 것은 위험하다.

### 대안. 서버에 저장된 payment.amount와 비교

```java
if (!payment.getAmount().equals(amount)) {
    throw new OrderInfoException("결제 금액이 일치하지 않습니다.");
}
```

#### 장점
- 클라이언트 금액 조작을 방어할 수 있다.
- 서버가 생성한 주문 금액 기준으로 결제 승인 여부 결정

#### 단점
- payment 조회 필요
- 결제 승인 전에 검증 로직이 추가.

### 결정

successUrl로 전달된 `amount`와 DB에 저장된 `payment.amount`를 비교한 뒤 일치하지 않으면 예외 발생.

---

## 주요 구현 코드 설명

### 1. OrderService.createReadyCartOrder()

```java
@Transactional
public PaymentCheckoutResponse createReadyCartOrder(Long memberId, OrderCreateRequest request) {
    validateLoginMember(memberId);

    OrderFormResponse orderForm = getCartOrderForm(memberId, request.getCartItemIds());

    Order order = Order.create(
            memberId,
            request.getReceiverName(),
            request.getReceiverPhone(),
            request.getZipcode(),
            request.getAddress(),
            request.getAddressDetail(),
            orderForm.getTotalPrice()
    );

    orderMapper.saveOrder(order);

    for (OrderFormItemResponse item : orderForm.getItems()) {
        OrderItem orderItem = OrderItem.create(
                order.getOrderId(),
                item.getProductId(),
                item.getCartItemId(),
                item.getProductName(),
                item.getPrice(),
                item.getQuantity()
        );

        orderMapper.saveOrderItem(orderItem);
    }

    String tossOrderId = "BOOKSHOP-" + order.getOrderId();

    Payment payment = Payment.ready(
            order.getOrderId(),
            tossOrderId,
            order.getTotalPrice()
    );

    paymentMapper.save(payment);

    return new PaymentCheckoutResponse(
            order.getOrderId(),
            tossOrderId,
            order.getTotalPrice(),
            createOrderName(orderForm.getItems()),
            "BookShop 회원",
            "member-" + memberId
    );
}
```

#### 역할

이 메서드는 결제 전 주문 준비를 담당한다.

즉 주문을 최종 완료하는 메서드가 아니라, 결제창을 호출하기 위한 주문 대기 데이터를 만드는 메서드다.

#### 흐름

```text
1. 로그인 회원 검증
2. 장바구니 상품 조회
3. 재고 검증
4. orders READY 저장
5. order_item 저장
6. tossOrderId 생성
7. payment READY 저장
8. 결제창 호출에 필요한 checkout 응답 반환
```

#### 중요한 점

이 단계에서는 재고 및 장바구니 상품 삭제가 발생하지 않는다.

결제 성공 시에만 재고 차감과 장바구니 상품 삭제가 이루어진다.

---

### 2. PaymentCheckoutResponse

```java
@Getter
@AllArgsConstructor
public class PaymentCheckoutResponse {

    private Long orderId;
    private String tossOrderId;
    private Integer amount;
    private String orderName;
    private String customerName;
    private String customerKey;
}
```

#### 역할

`check.jsp`에서 토스 결제창을 호출하기 위해 필요한 값을 담는 DTO다.

#### 필드의 의미
```text
orderId
- 내부 DB orders_order_id

tossOrderId
- 토스페이먼츠에 전달할 외부 주문번호

amount
- 결제 금액

orderName
- 토스 결제창에 표시할 주문명

customerName
- 구매자 이름

customerKey
- 토스페이먼츠에서 구매자를 식별하기 위한 키
```

---

### 3. checkout.jsp

```javascript
payment.requestPayment({
    method: "CARD",
    amount: {
        currency: "KRW",
        value: ${checkout.amount}
    },
    orderId: "${checkout.tossOrderId}",
    orderName: "${checkout.orderName}",
    successUrl: window.location.origin + "${contextPath}/payments/toss/success",
    failUrl: window.location.origin + "${contextPath}/payments/toss/fail",
    customerName: "${checkout.customerName}"
}).catch(function (error) {
    const message = encodeURIComponent(
        error.message || "결제가 취소되었습니다."
    );

    window.location.href =
        "${contextPath}/payments/toss/fail"
        + "?orderId=${checkout.tossOrderId}"
        + "&message=" + message;
});
```

#### 역할

토스페이먼츠 결제창을 브라우저에서 호출한다.

#### 주요 값

```text
method
- 결제 방식, 현재는 CARD 기반 결제창 호출

amount.value
- 결제 금액

orderId
- 토스페이먼츠에 전달할 주문번호

orderName
- 결제창에 표시될 주문명

successUrl
- 결제 인증 성공 후 이동할 URL

failUrl
- 결제 실패 후 이동할 URL
```

#### catch의 의미
사용자가 결제 실패시 `requestPayment()`의 실패를 catch하여 직접 failUrl로 이동시킨다.

---

### 4. PaymentController.success()

```java
@GetMapping("/success")
public String success(
        @RequestParam String paymentKey,
        @RequestParam String orderId,
        @RequestParam Integer amount,
        @SessionAttribute(SessionConst.LOGIN_MEMBER) SessionMemberDto loginMember,
        RedirectAttributes redirectAttributes
) {
    try {
        Long savedOrderId = paymentService.confirmPayment(
                loginMember.getMemberId(),
                paymentKey,
                orderId,
                amount
        );

        return "redirect:/orders/" + savedOrderId + "/complete";

    } catch (CustomException e) {
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

        return "redirect:/cart";
    }
}
```

#### 역할

토스 결제 성공 시 redirect를 받는 컨트롤러

#### 주의할 점

이 시점은 아직 최종 결제가 아니다.

토스에서 전달받은 `paymentKey`, `orderId`, `amount`를 가지고 서버에서 결제 승인 API를 호출해야 결제가 최종 완료된다.

---

### 5. PaymentService.confirmPayment()

```java
@Transactional
public Long confirmPayment(
        Long memberId,
        String paymentKey,
        String tossOrderId,
        Integer amount
) {
    Payment payment = paymentMapper.findByTossOrderId(tossOrderId)
            .orElseThrow(() -> new OrderInfoException("결제 정보를 찾을 수 없습니다."));

    if (!payment.getAmount().equals(amount)) {
        throw new OrderInfoException("결제 금액이 일치하지 않습니다.");
    }

    TossConfirmResponse response = tossPaymentClient.confirm(
            new TossConfirmRequest(paymentKey, tossOrderId, amount)
    );

    List<OrderFormItemResponse> items =
            orderMapper.findOrderItemsForPayment(payment.getOrderId());

    for (OrderFormItemResponse item : items) {
        int updateCount = orderMapper.decreaseProductStock(
                item.getProductId(),
                item.getQuantity()
        );

        if (updateCount == 0) {
            throw new StockQuantityExceedException("재고 수량을 초과한 상품이 있습니다.");
        }
    }

    PaymentMethod paymentMethod = PaymentMethod.from(
            response.getMethod(),
            response.getEasyPayProvider()
    );

    paymentMapper.updatePaid(
            payment.getPaymentId(),
            response.getPaymentKey(),
            paymentMethod
    );

    orderMapper.updateOrderStatus(payment.getOrderId(), OrderStatus.PAID);

    int deletedCount =
            orderMapper.deletePaidOrderCartItems(memberId, payment.getOrderId());

    if (deletedCount != items.size()) {
        throw new OrderInfoException("주문한 장바구니 삭제에 실패하였습니다.");
    }

    return payment.getOrderId();
}
```

#### 역할

결제 승인 이후 주문을 최종 확정하는 핵심 메서드

#### 흐름
```text
1. tossOrderId로 payment 조회
2. 결제 금액 검증
3. 토스 결제 승인 API 호출
4. 주문 상품 조회
5. 재고 차감
6. 결제 수단 enum 반환
7. payment PAID 변경
8. orders PAID 변경
9. 장바구니 상품 삭제
10. orderId 반환
```

---

### 6. TossPaymentClient

```java
@Component
@RequiredArgsConstructor
public class TossPaymentClient {

    private final TossProperties tossProperties;
    private final RestClient restClient = RestClient.create();

    public TossConfirmResponse confirm(TossConfirmRequest request) {
        return restClient.post()
                .uri(tossProperties.getConfirmUrl())
                .header(HttpHeaders.AUTHORIZATION, createAuthorization())
                .body(request)
                .retrieve()
                .body(TossConfirmResponse.class);
    }

    private String createAuthorization() {
        String value = tossProperties.getSecretKey() + ":";

        String encoded = Base64.getEncoder()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));

        return "Basic " + encoded;
    }
}
```

#### 역할

토스 페이먼츠 결제 승인 API를 호출하는 클라이언트

**confirm()**
```java
public TossConfirmResponse confirm(TossConfirmRequest request)
```

토스 승인 API에 POST 요청을 보낸다.

**createAuthorization**

토스 시크릿 키를 Basic 인증 헤더 형식으로 변환

SecretKey는 절대 JSP나 Javascript에 노출시키면 안된다.


---

## 현재 구조의 한계

현재 `PaymentService.confirmPayment()`는 하나의 `@Transaction` 안에서 외부 API를 호출과 DB 변경을 함께 처리한다.

```text
DB 조회
-> 토스 승인 API 호출
-> 재고 차감
-> 상태 변경
-> 장바구니 삭제
```

하지만 외부 API 호출은 네트워크 지연이나 실패 가능성이 있으므로, DB 트랜잭션 안에서 오래 잡고 있는 구조는 주의가 필요하다.

또한 현재 구조에서는 다음 문제가 발생할 수 있다.

```text
토스 결제 승인 성공
-> 재고 차감 실패
-> DB 트랜잭션 롤뱁
-> 하지만 토스 결제는 이미 승인 상태
```

---

## 📌 정리

결제 연동에서 가장 중요한 점은 주문 생성과 결제 확정의 기능을 분리한 것이다.

```text
READY 주문 생성
-> 토스 결제 승인
-> PAID 주문 확정
```

이를 통해 결제되지 않은 주문과 결제 완료 주문을 명확히 구분할 수 있었다.





