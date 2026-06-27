package hello.bookshop.common.advice;

import hello.bookshop.common.session.SessionConst;
import hello.bookshop.member.dto.response.MyPageHomeResponse;
import hello.bookshop.member.dto.response.SessionMemberDto;
import hello.bookshop.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttribute;

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

        if (loginMember == null) {
            return null;
        }

        if (!isMyPageView(request)) {
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
