# 사용자 상품 목록 이미지 조회 방식 결정

## 1. 배경

사용자 상품 목록 화면을 구현하면서 상품 정보와 함께 대표이미지를 출력해야 했다.

상품과 이미지는 다음과 같이 분리되어 있다.

```text
product
product_image
```

상품 하나는 여러개의 이미지를 가질 수 있고(1:N), 이미지는 타입으로 분류

```text
THUMBNAIL
DETAIL
```

목록 화면에서는 상세 이미지는 제외하고 대표이미지 1장만 필요하다.

---
## 2. 선택지 비교

### 1. 상품 목록 조회 후 상품마다 이미지 조회

상품 목록을 먼저 조회한 뒤에 각 상품의 대표이미지를 상품을 순회하면서 조회하는 방식이다.

```java
List<Product> products = productMapper.findProducts();

for (Product product : products) {
    ProductImage thumbnail = productMapper.findThumbnailImageByProductId(product.productId);    
}
```

#### 장점
- 상품 조회와 이미지 조회 책임이 분리된다.
- 쿼리가 단순하다.

#### 단점
- 상품 개수만큼 이미지 조회 쿼리가 추가적으로 발생한다.
- 상품 8개를 조회하면 상품 목록 1번 + 이미지 조회 8번이 발생
- 목록 페이지에서 N+1 형태의 반복 조회 문제가 생길 수 있다.

---

### 2. 상품 목록 조회 시 대표 이미지 LEFT JOIN

상품 목록을 조회할 때 대표 이미지만 함께 조회하는 방식이다.

```sql
LEFT JOIN product_image pi
    ON p.product_id = pi.product_id
    AND pi.image_type = 'THUMBNAIL'
```

#### 장점
- 상품 목록과 대표이미지를 한 번의 쿼리로 조회할 수 있다.
- 목록 화면에서 필요한 데이터만 가져올 수 있다.
- 상품 개수가 늘어나도 이미지 조회 쿼리가 추가적으로 발생하지 않는다.

#### 단점
- 목록 조회 쿼리가 이미지 테이블에 의존한다.
- 상품 대표 이미지가 여러 개 잘못 저장되면 중복 row가 발생할 수 있다.
- 상세 이미지까지 함께 조회하기에는 부적합.

---

## 3. 최종 선택

사용자 상품 목록에서는 대표 이미지만 필요하므로 `LEFT JOIN`으로 대표 이미지를 함께 조회.

```sql
SELECT
    p.product_id,
    p.name,
    p.author,
    p.publisher,
    p.price,
    p.stock_quantity,
    pi.image_path
FROM product p
LEFT JOIN product_image pi
    ON p.product_id = pi.product_id
    AND pi.image_type = 'THUMBNAIL'
WHERE p.deleted_at IS NULL
  AND p.status = 'ACTIVE'
ORDER BY p.created_at DESC
LIMIT #{size}
OFFSET #{offset}
```

---

## 4. 선택 이유

상품 목록 화면에서는 상세 이미지가 불필요하다.

따라서 상품마다 이미지를 개별 조회하는 방식보다, 대표 이미지만 `JOIN`으로 가져오는 방식이 더 적절하다고 판단하였다.

---

## 5. 결과
- 사용자 상품 목록에서 대표 이미지가 함께 출력되었다.
- 홈 최신 도서 목록도 같은 방식으로 대표 이미지를 조회할 수 있었다.
- 목록 조회에서 반복 이미지 조회를 피할 수 있었다.
- 상세 화면에서는 상품 단건 조회 후 이미지 목록을 별도로 조회하도록 분리했다.