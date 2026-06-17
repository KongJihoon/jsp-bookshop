# 상품 상세 조회 시 이미지가 출력되지 않는 문제

## 문제 상황

상품 상세 조회 기능 구현 후 DB에서 이미지 경로를 정상적으로 조회했지만 화면에서는 이미지가 출력되지 않았다.

```html
<img src="${contextPath}${image.imagePath}">
```

브라우저에서는 깨진 이미지 아이콘이 표시되었다.

## 원인 분석

DB에 저장된 이미지 경로는 정상적이었다.

```text
/upload/8e3f2d91-xxxx-xxxx.jpg
```

하지만 실제 파일은 서버 로컬 경로에 저장되어있었다.

```text
/Users/username/bookshop-upload/
```

브라우저는

```text
GET /upload/xxxx.jpg
```
요청을 보냈지만 SpringMVC는 `/upload/**`요청을 어디서 찾아야 하는지 알지 못했다.
결과적으로 404 오류가 발생한 것이다.

---

## 해결 방법

WebConfig에 ResourceHandler를 추가하였다.
```java
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {

    registry.addResourceHandler("/upload/**")
            .addResourceLocations("file:/Users/ji/bookshop-upload/");
}
```

---

## 결과

브라우저 요청(`/upload/파일명.jpg`) -> 실제 저장 위치(`/Users/username/bookshop-upload/파일명.jpg`)
로 매핑되어 이미지가 정상 출력되었다.

---

## 배운점

파일 업로드 기능을 구현하더라도

파일저장 -> DB저장 -> 정적 리소스 매핑 이 세단계가 모두 연결되어야 실제 화면에서 이미지를 확인할 수 있다.

