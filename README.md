# 📚 JSP BookMall

> JSP + Servlet + MyBatis 기반의 전통적인 서버 사이드 렌더링 구조를 학습하기 위한 도서 쇼핑몰 프로젝트


- JSP / Servlet 기반의 요청 흐름과 Session 기반 인증 방식을 이해하기 위해 진행한 프로젝트입니다.
- 회원가입, 로그인, 상품 관리, 장바구니, 주문 기능 등을 구현하며 전통적인 웹 애플리케이션 구조를 학습하는 것을 목표로 하였습니다.
- 단순 CRUD 구현을 넘어서 관리자 권한 분리, 상품 상태 관리, 주문/재고 처리 흐름 등을 고려하여 구현할 예정입니다.

---

# 🗓️ 개발 기간

- 2026.05 ~ 진행 중 (약 3주 예정)

---

# 1️⃣ ERD

> ERD는 docs 폴더에 정리 예정입니다.

![ERD](./docs/erd.png)

---

# 🏛️ 프로젝트 구조

```text
src/main/java
├── controller
├── service
├── repository
├── mapper
├── domain
├── dto
├── filter
├── util
└── exception

src/main/webapp
├── WEB-INF
│   └── views
├── css
├── js
└── images
```

---

# 🛠 기술 스택

## 👨‍💻 Backend

- Java 17
- JSP / Servlet
- MyBatis
- JDBC

## 🎨 Frontend

- HTML
- CSS
- JavaScript
- JSTL / EL

## ⚙️ Database

- MySQL

## 🧰 Tools

- IntelliJ IDEA
- Git & GitHub
- Maven
- Notion

---

# 🧩 주요 기능

## 👤 회원 (User)

### 회원가입

- [x] 회원가입 폼 구현
- [x] 아이디 중복 검사
- [x] 이메일 중복 검사
- [x] 비밀번호 암호화 저장
- [x] 입력값 검증 처리
- [x] 회원 기본 권한(USER) 부여
- [x] 회원 상태 ACTIVE 저장

### 로그인

- [x] 아이디 / 비밀번호 기반 로그인
- [x] 로그인 성공 시 Session 저장
- [x] 로그인 실패 처리
- [ ] 로그인 사용자 접근 처리

### 로그아웃

- [x] Session 무효화 처리
- [x] 로그아웃 후 메인 페이지 이동

### 마이페이지

- [ ] 회원 정보 조회
- [ ] 회원 정보 수정
- [ ] 비밀번호 변경
- [ ] 주문 내역 조회

### 회원탈퇴

- [ ] 회원 상태 WITHDRAWN 변경
- [ ] Soft Delete 처리

---

# 🔐 인증 / 인가

- [ ] LoginCheckFilter 구현
- [ ] AdminCheckFilter 구현
- [ ] 관리자 / 일반 사용자 권한 분리
- [ ] 비로그인 사용자 접근 제한

---

# 📦 상품 (Product)

## 상품 기능

- [ ] 상품 등록
- [ ] 상품 수정
- [ ] 상품 삭제
- [ ] 상품 목록 조회
- [ ] 상품 상세 조회
- [ ] 카테고리별 조회
- [ ] 검색 및 페이징 처리

## 상품 상태 관리

- [ ] ON_SALE
- [ ] SOLD_OUT
- [ ] DELETED

---

# 🛒 장바구니 (Cart)

- [ ] 장바구니 담기
- [ ] 장바구니 조회
- [ ] 수량 변경
- [ ] 장바구니 상품 삭제

---

# 📄 주문 (Order)

- [ ] 주문 생성
- [ ] 주문 내역 조회
- [ ] 주문 상세 조회
- [ ] 주문 취소
- [ ] 배송 상태 관리
- [ ] 주문 취소 시 재고 복구

---

# 🚚 배송 (Delivery)

- [ ] 배송지 입력
- [ ] 배송 상태 변경
- [ ] 기본 배송지 자동 입력

---

# 🔍 검색 기능

- [ ] 상품명 검색
- [ ] 저자 검색
- [ ] 출판사 검색
- [ ] 검색 결과 페이징 처리
- [ ] 검색 조건 유지

---

# ⭐ 리뷰 기능

- [ ] 리뷰 작성
- [ ] 리뷰 목록 조회
- [ ] 리뷰 수정
- [ ] 리뷰 삭제
- [ ] 평점 등록

---

# 📄 API / 화면 명세

> 추후 docs 폴더에 정리 예정입니다.

```text
docs/api.md
docs/screen/
```

---

# 🎥 시연 화면

## 회원가입

> 추후 GIF 추가 예정

```text
docs/screen/signup.gif
```

## 로그인

> 추후 GIF 추가 예정

```text
docs/screen/login.gif
```

---

# 🔥 핵심 구현 예정 내용

## Session 기반 로그인 처리

- 로그인 성공 시 Session에 사용자 정보 저장
- 인증이 필요한 URL 접근 제한
- Filter 기반 인증 처리

## 상품 상태 관리

- 실제 삭제 대신 상태값(DELETED) 변경 방식 적용
- 판매 중 상품만 사용자 화면 노출

```text
ON_SALE
SOLD_OUT
DELETED
```

## 주문 취소 시 재고 복구

- 주문 생성 시 재고 감소
- 배송 전 상태에서만 주문 취소 가능
- 주문 취소 시 상품 재고 복구 처리

---

# ⚠️ 트러블슈팅

> 자세한 내용은 docs/troubleshooting.md 에 정리 예정입니다.

예정 항목:

- Session 유지 문제
- 검색 조건 유지 문제
- MyBatis parameterType 오류
- JSP EL 표현식 출력 문제
- 파일 업로드 경로 문제
- 한글 인코딩 문제

---

# 📚 회고

프로젝트 완료 후 작성 예정입니다.

- JSP / Servlet 요청 흐름 학습
- Session 기반 인증 처리 경험
- MyBatis SQL 작성 경험
- CRUD 및 검색/페이징 구현 경험
- 관리자 기능 설계 경험
