package hello.bookshop.common.exception;

import hello.bookshop.common.exception.member.DuplicateMemberException;
import hello.bookshop.common.exception.member.LoginFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /** 회원가입 중복 예외 처리 */
    @ExceptionHandler(DuplicateMemberException.class)
    public String handleDuplicateException(DuplicateMemberException e, Model model) {

        log.warn("회원가입 중복 발생 = {}", e.getMessage());
        model.addAttribute("errorMessage", e.getMessage());

        return "member/signup"; // 회원가입 폼으로 돌아가며 에러메시지

    }
    /**  로그인 실패 예외 처리 */
    @ExceptionHandler(LoginFailedException.class)
    public String handleLoginFailedException(LoginFailedException e, Model model) {

        log.warn("로그인 실패 발생 = {}", e.getMessage());

        model.addAttribute("errorMessage", e.getMessage());

        return "member/loginForm";
    }



    /**     * 예상치 못한 예외 처리     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGeneralException(Exception e) {
        log.error("Unexpected Exception: ", e);

        ModelAndView mav = new ModelAndView("common/error/systemError");
        mav.addObject("errorMessage", "시스템 오류가 발생했습니다.");
        mav.addObject("errorCode", "SYSTEM_ERROR");

        return mav;
    }

}
