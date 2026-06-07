package hello.bookshop.common.interceptor;

import hello.bookshop.common.session.SessionConst;
import hello.bookshop.member.dto.response.SessionMemberDto;
import hello.bookshop.member.type.MemberType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class AdminCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession(false);


        if (session == null) {
            response.sendRedirect("/admin/login");
            return false;
        }

        SessionMemberDto loginMember = (SessionMemberDto) session.getAttribute(SessionConst.LOGIN_MEMBER);


        if (loginMember == null) {
            response.sendRedirect("/admin/login");
            return false;
        }

        if (loginMember.getMemberType() != MemberType.ADMIN) {
            response.sendRedirect("/");
            return false;
        }

        return true;


    }
}
