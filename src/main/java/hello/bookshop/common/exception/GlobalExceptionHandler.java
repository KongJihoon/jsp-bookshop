package hello.bookshop.common.exception;

import hello.bookshop.common.exception.member.DuplicateMemberException;
import hello.bookshop.common.exception.member.PasswordMismatchException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final View error;

    public GlobalExceptionHandler(View error) {
        this.error = error;
    }

    /** 중복 회원 예외 처리 */
    @ExceptionHandler(DuplicateMemberException.class)
    public ModelAndView handleDuplicateException(
            DuplicateMemberException e
    ) {

        log.warn("Duplicate Member Exception: {}", e.getMessage());

        ModelAndView mv = new ModelAndView("member/signup");

        mv.addObject("errorMessage", e.getMessage());
        mv.addObject("errorField", e.getField());
        mv.addObject("errorCode", e.getErrorCode());


        return mv;
    }
    /** 비밀번호 불일치 예외 처리 */
    @ExceptionHandler(PasswordMismatchException.class)
    public ModelAndView handlePasswordNotMatchException(
            PasswordMismatchException e
    ) {

        log.warn("Password NotMatch Exception: {}", e.getMessage());

        ModelAndView mv = new ModelAndView("member/signup");

        mv.addObject("errorMessage", e.getMessage());
        mv.addObject("errorCode", e.getErrorCode());

        return mv;
    }


    /** Bean Validation 예외 처리 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ModelAndView handleMethodNotValidException(
            MethodArgumentNotValidException e
    ) {

        log.warn("Validation Exception: {}", e.getMessage());

        Map<String, String> fieldErrors = new ConcurrentHashMap<>();

        e.getBindingResult().getFieldErrors().forEach(error ->
            fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        ModelAndView mv = new ModelAndView("member/signup");

        mv.addObject("fieldErrors", fieldErrors);

        return mv;

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
