# 관리자 도서 목록 필터 조건이 페이지 이동 시 초기화 되는 문제

## 문제 상황

관리자 도서 목록 조회 기능 구현 중 카테고리 또는 판매 상태 필터를 적용한 후 페이지를 이동하면 선택했던 조건이 초기화되는 문제가 발생하였다.
예를 들어, 판매상태를 `판매중(ACTIVE)`로 선택한 뒤 2페이지로 이동하면 필터가 해제되고 전체 도서 목록이 조회되었다.

---

## 원인 분석

필터 적용 시 요청 URL은 다음과 같이 생성된다.

```text
/admin/product/list?status=ACTIVE
```

하지만 페이지 링크는 다음과 같이 구현되어 있었다.
```jsp
    <a href="${contextPath}/admin/product/list?page=${pageNumber}">
```

페이지 번호만 전달되고 기존 필터 조건 (`status`, `categoryId`, `keyword`)은 전달되지 않았다.

결과적으로 페이지 이동 시 새로운 요청이 발생하면서 필터 조건이 모두 사라지게 되었다.

---

## 해결 과정

### 1. Controller에서 현재 검색 조건을 Model에 저장

```java
    model.addAttribute("selectedCategoryId", categoryId);
    model.addAttribute("selectedStatus", status);
    model.addAttribute("keyword", keyword);
```


### 2. 페이징 링크에 기존 검색 조건 유지

기존 코드

```jsp
<a href="${contextPath}/admin/product/list?page=${pageNumber}">
```

수정 코드

```jsp
<a href="${contextPath}/admin/product/list?page=${pageNumber}
&categoryId=${selectedCategoryId}
&status=${selectedStatus}
&keyword=${keyword}">
```

---

### 3. 이전/다음 버튼도 동일하게 적용

```jsp
<a class="page-link"
   href="${contextPath}/admin/product/list?page=${productPage.page - 1}
   &categoryId=${selectedCategoryId}
   &status=${selectedStatus}
   &keyword=${keyword}">
    이전
</a>
```

```jsp
<a class="page-link"
   href="${contextPath}/admin/product/list?page=${productPage.page + 1}
   &categoryId=${selectedCategoryId}
   &status=${selectedStatus}
   &keyword=${keyword}">
    다음
</a>
```

---

## 결과
- 페이지 이동 시 카테고리 조건 유지
- 페이지 이동 시 판매 상태 조건 유지
- 페이지 이동 시 검색어 유지
- 필터링된 상태 그대로 페이징 가능

---

## 정리

검색, 필터링, 페이징이 함께 존재하는 화면에서는 현재 조회 조건을 페이지 이동 시에도 함께 전달해야한다.

페이지 링크를 구현할 때 페이지 번호만 전달되는 것이 아니라 현재 사용 중인 검색 조건도 함께 유지해야 한다.

---

# MyBatis 동적 검색 조건에서 OR 우선순위로 인해 필터가 정상 동작하지 않는 문제

## 문제 상황

관리자 도서 목록 조회 기능 구현 중 카테고리, 판매 상태, 검색어를 조합하여 조회하는 기능을 구현하였다.

예를 들어 아래와 같은 조건으로 검색을 수행하였다.

```text
카테고리 : IT · 컴퓨터
판매 상태 : 판매중
검색어 : 자바
```

하지만 카테고리와 판매 상태 조건을 선택했음에도 불구하고 전혀 관련 없는 데이터가 함께 조회되는 문제가 발생하였다.

---

## 원인 분석

목록 조회는 다음과 같이 작성되어 있었다.

```xml
<if test="keyword != null">
    AND p.name LIKE CONCAT('%', #{keyword}, '%')
    OR p.author LIKE CONCAT('%', #{keyword}, '%')
</if>
```

실제 SQL로 변환되면 다음과 같은 형태가 된다.

```sql
WHERE p.deleted_at IS NULL
AND p.category_id = 2
AND p.status = 'ACTIVE'
AND p.name LIKE '%자바%'
OR p.author LIKE '%자바%'
```

문제는 SQL에서 `OR` 연산자가 존재할 경우 조건 우선순위가 달라진다는 점이다.

위 쿼리는 다음과 같이 해석될 수 있다.

```sql
(
    p.deleted_at IS NULL
    AND p.category_id = 2
    AND p.status = 'ACTIVE'
    AND p.name LIKE '%자바%'
)
OR p.author LIKE '%자바%'
```

결과적으로 저자명에 "자바"가 포함되어 있다면 카테고리와 판매 상태 조건을 무시하고 조회될 수 있었다.

---

## 해결 방법

검색 조건을 하나의 그룹으로 묶어주었다.

```xml
<if test="keyword != null and keyword != ''">
    AND (
        p.name LIKE CONCAT('%', #{keyword}, '%')
        OR p.author LIKE CONCAT('%', #{keyword}, '%')
    )
</if>
```

실제 SQL은 다음과 같이 동작한다.

```sql
WHERE p.deleted_at IS NULL
AND p.category_id = 2
AND p.status = 'ACTIVE'
AND (
    p.name LIKE '%자바%'
    OR p.author LIKE '%자바%'
)
```
이제 카테고리, 판매 상태 조건을 먼저 만족한 데이터만 검색 대상이 된다.

---

## 결과

### 수정 전

```text
카테고리 조건 무시
판매 상태 조건 무시
관련 없는 데이터 조회
```

### 수정 후

```text
카테고리 조건 정상 적용
판매 상태 조건 정상 적용
검색어 조건 정상 적용
페이징 개수 정상 계산
```

---

## 정리

동적 검색 조건에서 `AND` 와 `OR` 를 함께 사용할 경우 반드시 괄호를 사용하여 조건 그룹을 명확하게 지정해야 한다.

특히 검색 기능 구현 시 다음 패턴을 기억해두자.

```sql
AND (
    컬럼1 LIKE ...
    OR 컬럼2 LIKE ...
)
```

괄호가 없으면 의도하지 않은 데이터가 조회될 수 있으며, 복합 검색 기능이 정상적으로 동작하지 않을 수 있다.