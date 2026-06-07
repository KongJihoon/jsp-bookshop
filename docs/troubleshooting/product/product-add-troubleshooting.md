# Multipart 파일 업로드 용량 초과 (Maximum upload size exceeded)

## 문제 상황

도서 등록 기능 구현 후 대표 이미지를 업로드하는 과정에서 다음과 같은 예외가 발생했다.

```text
org.springframework.web.multipart.MaxUploadSizeExceededException
Maximum upload size exceeded
```

이미지 파일의 크기가 크지 않았음에도 업로드가 실패했다.

---

## 원인 분석

Multipart 설정을 확인하던 중 다음과 같이 설정되어 있었다.

```properties
spring.servlet.multipart.max-file-size=20B
spring.servlet.multipart.max-request-size=100MB
```

처음에는 `20B`를 `20MB`로 착각하고 설정하였다.

하지만 실제로는 다음과 같은 의미였다.

```text
20B = 20 Byte
```

즉, 업로드 가능한 최대 파일 크기가 20B로 제한되어 있었기 때문에 이미지 파일 업로드 시 예외가 발생하였다.

실제 로그에도 다음과 같이 출력되었다.
```text
thumbnailImage exceeds its maximum permitted size of 20 bytes
```
---

## 해결 방법

Multipart 최대 파일 크기를 올바르게 수정하였다.

```properties
spring.servlet.multipart.max-file-size=20MB
spring.servlet.multipart.max-request-size=100MB
```

설정 변경 후 서버를 재시작하였고 정상적으로 이미지 업로드가 가능해졌다.

---

## 배운 점

Multipart 설정 시 단위를 정확하게 지정해야 한다.

```text
B   = Byte
KB  = Kilobyte
MB  = Megabyte
GB  = Gigabyte
```

특히 설정값은 정상적으로 적용되더라도 단위를 잘못 입력하면 예상치 못한 업로드 제한이 발생할 수 있다.

따라서 Multipart 설정 시 단위까지 반드시 확인하는 습관이 필요하다.

---

# Mybatis Generated Key를 활용한 상품 PK 조회

## 문제 상황

도서 등록 기능 구현 중 카테고리 조회를 `List`로 받아 페이지에서 선택하게 구현하였다.
하지만 카테고리 조회가 되지않았고, 로그를 찍어보니 List에 아무 값이 전달되지 않았다.

## 원인 분석

Mybatis.xml 파일에서 쿼리문에서 Category테이블에 categoryId와 일치해야 조회가 되는 조건문이 존재했다.
단순 실수였지만 변수로 categoryId를 받는 상황이 아니었고 페이지에 존재하는 카테고리 리스트를 보여줘야하는 상황이었다.


## 문제 해결

`List`로 카테고리 객체를 받는 쿼리문을 아래와 같이 변경해주었다.

```java
<select id="findAllByCategories" resultType="hello.bookshop.category.domain.Category">

        SELECT
            category_id,
            parent_id,
            category_name,
            category_status,
            created_at,
            updated_at
        FROM category
        WHERE category_status = 'ACTIVE'
        ORDER BY category_id ASC


</select>
```

## 결과

쿼리 조건에서 `category_id = #{categoryId}` 조건을 제거한 후, `ACTIVE` 상태의 카테고리 목록이 정상적으로 조회되었다.

이후 Controller에서 조회한 카테고리 목록을 Model에 담아 JSP로 전달하였다.

```java
model.addAttribute("categories", categoryService.findAllByCategories());
```

JSP에서는 전달받은 `categories`를 반복문으로 출력하였다.

```jsp
<c:forEach items="${categories}" var="category">
    <option value="${category.categoryId}">
        ${category.categoryName}
    </option>
</c:forEach>
```

## 배운점

전체 목록 조회와 단건 조회는 쿼리 조건이 달라야 한다.

이번 문제는 카테고리 전체 목록을 조회해야 하는 상황에서 단건 조회 조건인 category_id = #{categoryId}를 남겨두어 발생하였다.

앞으로 조회 쿼리를 작성할 때는 다음을 먼저 구분해야겠다고 느꼈다.

```text
단건 조회
→ 특정 id 조건 필요

목록 조회
→ 상태값, 정렬 조건 중심

검색 조회
→ keyword, categoryId 등 선택 조건 필요
```

