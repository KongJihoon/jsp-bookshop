package hello.bookshop.common.interceptor;

import hello.bookshop.common.session.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":\"NOT_LOGIN\",\"message\":\"로그인 후 사용 가능합니다.\",\"redirectUrl\":\""
                        + request.getContextPath() + "/member/login\"}");
                return false;
            }

            response.sendRedirect(request.getContextPath() + "/member/login");
            return false;
        }

        return true;
    }
}
