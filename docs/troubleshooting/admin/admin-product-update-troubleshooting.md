# 관리자 도서 수정 기능 트러블 슈팅

## 1. 도서명을 입력해도 필수값 검증에 실패한 문제

### 문제 상황

도서 수정 화면에서 도서명을 입력하고 저장했지만 다음 검증 오류가 계속 발생하였다.

```text
도서명은 필수입니다.
```

검증에 실패한 후 수정 화면으로 리다이렉드 후 입력했더 도서명도 사라지는 상황이 발생하였다.

---

### 원인 분석

도서명 `input`에 `id`는 있었지만 `name` 속성이 빠져있었다.

```jsp
    <input type="text" id="name" value="${formProduct.name}".....>
```

HTML Form 전송에서 서버로 전달되는 파라미터 이름은 `id`가 아니라 `name`으로 결정된다.

따라서 요청에 `name` 파라미터가 포함되지 않았고, SpringMVC는 `ProductUpdateRequest.name`을 `null`로 바인딩했다.

```java
    @NotBlank(message = "도서명은 필수입니다.")
    private String name;
```

결과적으로 `@NotBlank`검증에 실패했고, 바인딩된 값도 없어서 화면에서 입력값이 사라졌다.

---

### 해결 방법

도서명 입력 요소에 `name`속성을 추가하였다.

```jsp
    <input id="name" name="name" value="${formProduct.name}" class="form-control">
```

---
### 결과
```text
브라우저 Form
-> name 파라미터 전달
-> ProductUpdateRequest.name 바인딩
-> Bean Validation 검증
-> 정상 수정
```

HTML의 `id` 속성과 `name`속성은 역할이 다르다는 점을 확인했다.

- `id` 속성은 `label, CSS, Javascript`에서 요소를 식별하는 역할을 한다.
- `name` 속성은 Form 데이터의 파라미터 이름으로 사용된다.

---

## 2. 최초 조회값과 검증 실패 입력값을 구분해야 했던 문제

### 문제 상황

수정 화면에는 두 종류의 데이터를 처리해야 했다.

```text
최초 GET 요청
-> DB에 저장된 기존 상품 정보

검증에 실패한 POST 요청
-> 사용자가 수정해서 입력한 정보
```

JSP가 항상 조회 객체인 `${product}`를 출력하면 검증 실패 후 사용자의 입력값이 기존 DB값으로 덮어씌워지는 문제가 발생한다.

---

### 원인 분석

컨트롤러에서 서로 다른 두 모델 객체를 사용하고 있었다.

```java
model.addAttribute("product", product);
model.addAttribute("productUpdateRequest", request);
```

각 객체의 역할은 다음과 같다.

- `product` : 기존 상품 정보와 이미지 조회
- `productUpdateRequest` : 사용자가 전송한 수정 데이터

검증 실패 시 Spring MVC는 바인딩된 `productUpdateQuest`를 모델에 유지하지만, JSP에서 `${product}`를 사용하엽 해당 value를 사용할 수 없다.

---

### 해결 방번

JSP에서 요청 방식에 따라 폼에 출력할 객체를 선택했다.

```jsp
<c:set var="formProduct" value="${pageContext.request.method eq 'POST' ? ${productUpdateRequest : ${product}}}"/>
```

입력 요소는 모두 `formProduct`를 사용하도록 통일했다.

기존 이미지는 수정 요청 DTO에 포함되지 않으므로 조회 객체를 사용했다.

```jsp
<c:forEach var="image" items="${product.image}">
    ...
</c:forEach>
```

---

### 결과

```text
최초 GET
→ product 값 출력

검증 실패 POST
→ productUpdateRequest 값 출력

기존 이미지
→ product.images 출력
```

수정 폼에서는 단순히 DB값을 보여주는 것뿐 아니라 검증 실패 후 사용자가 입력한 값을 보존해야 한다는 점을 확인했다.

---

## 3. JSP EL과 Javascript 템플릿 문자열 충돌

### 문제 상황

상세 이미지 미리보기 코드가 실행되지 않았다.

```javascript
image.alt = '새 상세 이미지 ${index + 1}';
```

---

### 원인 분석

JSP 와 Javascript 모두 `${...}` 문법을 사용한다.

JSP 엔진이 Javascript 실행 전에 `${index + 1}`을 EL 표현식으로 처리하면서 브라우저에 전달되는 Javascript가 의도와 다르게 변경될 수 있다.

---

### 해결 방법
JSP EL과 충돌하지 않도록 문자열 연결 방식을 사용했다.

```javascript
image.alt = "새 상세 이미지 " + (index + 1);
```

---

## 배운점
- HTML Form 바인딩에는 `name` 속성이 반드시 필요하다.
- 수정 화면은 최초 조회값과 검증 실패 입력값을 구분해야 한다.
- 조회 DTO와 수정 요청 DTO는 역할이 다르다.
- JSP와 JavaScript를 함께 사용할 때 `${...}` 문법 충돌을 고려해야 한다.
- 클라이언트 미리보기 검증과 서버 검증은 별도로 수행해야 한다.





