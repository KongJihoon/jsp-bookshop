package hello.bookshop.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {



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
