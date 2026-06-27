# 마이페이지 공통 요약 정보 미출력 트러블 슈팅

## 문제 상황

마이페이지 홈에서는 최근 주문 수와 장바구니 상품 수를 정상적으로 출력하고 있었다.

하지만 주문 내역, 주문 상세, 회원정보 조회/수정 화면 공통 상단 UI에서는 요약 정보가 표시되지 않았다.

해당 화면들은 모두 동일하게 `mypage-menu.jsp`를 include하고 있었다.

```text
<jsp:include page="../common/mypage-menu.jsp"/>
```

`mypage-menu.jsp`에서는 다음과 같이 `myPageHome`데이터를 사용하고 있다.

```text
${myPageHome.recentOrderCount}
${myPageHome.cartItemCount}
```

즉 화면 조각은 공통으로 재사용하고 있었지만, 해당 조각이 필요로 하는 Model 데이터는 일부 컨트롤러에서만 제공되고 있었다.

---

## 원인 분석

처음에는 `/member/mypage`요청에서만 `myPageHome`을 Model에 담고 있었다.

```java
@GetMapping("/mypage")
public String myPage(
        @SessionAttribute(SessionConst.LOGIN_MEMBER) SessionMemberDto loginMember,
        Model model
) {
    MyPageHomeResponse myPageHome =
            memberService.getMyPageHome(loginMember.getMemberId());

    model.addAttribute("myPageHome", myPageHome);

    return "member/mypage";
}
```

이 방식은 마이페이지 홈에서는 정상 동작하지만, 다른 마이페이지 계열 화면에서는 문제가 생긴다.

---

## 해결 방법

### 방법 1. 각 컨트롤러마다 myPageHome 추가

```java
MyPageHomeResponse myPageHome =
        memberService.getMyPageHome(loginMember.getMemberId());

model.addAttribute("myPageHome", myPageHome);
```

#### 장점
- 구현이 단순하다.
- 어떤 화면에서 요약정보를 사용하는지 컨트롤러에 명확하게 드러난다.

#### 단점
- 여러 컨트롤러에서 같은 코드가 반복된다.
- 공통 UI가 필요로 하는 데이터를 개별 컨트롤러가 매번 책임지게 된다.

### 방법 2. `@ControllerAdvice` + `@ModelAttribute`

```java
@ControllerAdvice
@RequiredArgsConstructor
public class MyPageModelAttributeAdvice {

    private final MemberService memberService;

    @ModelAttribute("myPageHome")
    public MyPageHomeResponse myPageHome(
            HttpServletRequest request,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false)
            SessionMemberDto loginMember
    ) {
        if (!isMyPageView(request)) {
            return null;
        }

        if (loginMember == null) {
            return null;
        }

        return memberService.getMyPageHome(loginMember.getMemberId());
    }

    private boolean isMyPageView(HttpServletRequest request) {
        String path = request.getRequestURI()
                .substring(request.getContextPath().length());

        return path.equals("/member/mypage")
                || path.equals("/member/info")
                || path.equals("/member/edit")
                || path.equals("/orders")
                || path.matches("^/orders/\\d+$");
    }
}
```


#### 장점
- 마이페이지 공통 요약 데이터를 한 곳에서 관리
- 개별 컨트롤러의 중복 코드가 사라진다.
- 새로운 마이페이지 화면이 추가될 때 공통 조건만 추가하면 된다.

#### 단점
- `@ControllerAdvice`는 범위가 넓기 때문에 조건 없이 사용하면 모든 요청에서 DB조회가 발생할 수 있다.
- 특정 URL에서만 실행되도록 요청 조건을 관리해야 한다.


---

## 선택한 해결 방법

마이페이지 상단 요약 정보는 여러 화면에서 반복적으로 사용되는 공통 UI 데이터이므로, 개별 컨트롤러마다 추가하지 않고 `@ControllerAdvice`와 `@ModelAttribute`를 사용해 공통으로 주입했다.
다만 모든 요청에서 요약 정보 조회가 실행되면 불필요한 DB 조회가 발생할 수 있으므로, 마이페이지 계열 화면에서만 동작하도록 요청 경로를 제한했다.


---

## 개선 후 흐름
```text
/member/mypage
/member/info
/member/edit
/orders
/orders/{orderId}
```

위 경로로 요청이 들어오면 다음 흐름으로 공통 요약 정보가 추가된다.

```text
1. MyPageModelAttributeAdvice 실행
2. 요청 경로가 마이페이지 계열인지 확인
3. 로그인 회원 정보 확인
4. memberService.getMyPageHome(memberId) 호출
5. myPageHome을 Model에 추가
6. mypage-menu.jsp에서 최근 주문 수와 장바구니 수 출력
```

이후 각 컨트롤러는 자신의 화면에 필요한 핵심 데이터만 Model에 담으면 된다.

```text
/member/info
-> member 정보만 Model에 추가

/orders
-> orders 목록만 Model에 추가

/orders/{orderId}
-> order 상세 정보만 Model에 추가
```

---

## 결과
변경 후 다음 화면에서 모두 동일하게 최근 주문 수와 장바구니 상품 수가 표시되었다.

```text
마이페이지 홈
회원정보 조회
회원정보 수정
주문 내역
주문 상세
```

