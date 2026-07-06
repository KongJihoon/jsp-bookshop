## 1. 관리자 주문 목록에서 본문 미출력 문제

### 문제 상황

관리자 주문 목록 JSP 작성을 했지만 화면에서 공통 사이드바만 출력이 되고 메인 영역이 출력되지 않았다.

---

### 원인 분석

관리자 대시보드는 사이드바와 본문을 `d-flex`로 감싸는 구조다.

```jsp
<div class="d-flex">
    <jsp:include page="../common/dashboard-sidebar.jsp"/>

    <div class="flex-grow-1">
        본문
    </div>
</div>
```

하지만 주문 목록 JSP에서는 사이드바 include 이후 바로 `main` 태그를 배치했다.

```jsp
<jsp:include page="../../common/dashboard-sidebar.jsp"/>

<main class="admin-main">
    ...
</main>
```
`dashboard-sidebar.jsp`의 `<aside>`는 `min-vh-100`을 가지고 있었기 때문에 본문이 사이드바 오른쪽이 아니라 아래로 밀려버렸다.

---

### 해결 방법

관리자 대시보드와 동일하게 `d-flex`구조로 수정하였다.

```jsp
<div class="d-flex">

    <jsp:include page="../../common/dashboard-sidebar.jsp"/>

    <div class="flex-grow-1">
        <main class="p-4">
            ...
        </main>
    </div>

</div>
```

---

### 정리

공통 사이드바를 사용하는 JSP는 동일한 레이아웃 구조를 유지해야 한다.

---

## 2. 주문 상세 화면에서 totalPrice 프로퍼티 오류 발생

### 문제 상황

관리자 주문 상세 화면에서 다음 오류가 발생하였다.

```text
Property [totalPrice] not found on type [OrderDetailItemResponse]
```

---

### 원인 분석

JSP에서 주문 상품별 금액을 출력할 때 아래와 같이 작성하였다.

```jsp
<fmt:formatNumber value="${item.totalPrice}" pattern="#,###"/>원
```

하지만 `OrderDetailItemResponse`에는 `totalPrice` 필드가 없었다.

DTO 구조는 다음과 같다.

```java
private Integer price;
private Integer quantity;
private Integer itemTotalPrice;
```

`totalPrice`는 주문 전체 금액을 의미하는 필드이고, 주문 상품 한 줄의 총 금액은 `ItemTotalPrice`다.

---

### 해결 방법

JSP에서 필드명을 변경하였다.

```jsp
<fmt:formatNumber value="${item.itemTotalPrice}" pattern="#,###"/>원
```

---

### 정리

주문 전체 금액과 주문 상품별 금액의 의미를 구분해야 한다.

```text
order.totalPrice = 주문 전체 결제 금액
item.price = 상품 1개 가격
item.quantity = 주문 수량
item.itemTotalPrice = 상품별 총 금액
```
