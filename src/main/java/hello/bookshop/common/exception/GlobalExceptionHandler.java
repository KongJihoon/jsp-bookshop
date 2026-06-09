package hello.bookshop.common.exception;

import hello.bookshop.common.exception.admin.AdminLoginFailedException;
import hello.bookshop.common.exception.category.NotFoundCategoryException;
import hello.bookshop.common.exception.member.DuplicateMemberException;
import hello.bookshop.common.exception.member.MemberLoginFailedException;
import hello.bookshop.common.exception.member.MemberNotFoundException;
import hello.bookshop.common.exception.product.ProductNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    @ExceptionHandler(MemberLoginFailedException.class)
    public String handleLoginFailedException(MemberLoginFailedException e, Model model) {

        log.warn("로그인 실패 발생 = {}", e.getMessage());

        model.addAttribute("errorMessage", e.getMessage());

        return "member/loginForm";
    }

    /**
     * 관리자 로그인 실패 예외 처리
     */

    @ExceptionHandler(AdminLoginFailedException.class)
    public String handleAdminLoginFailedException(AdminLoginFailedException e, Model model) {

        log.warn("관리자 로그인 실패 발생 = {}", e.getMessage());

        model.addAttribute("errorMessage", e.getMessage());

        return "admin/login";

    }

    /** 사용자 조회 실패 예외 처리 */
    @ExceptionHandler(MemberNotFoundException.class)
    public String handleMemberNotFoundException(MemberNotFoundException e, Model model) {

        log.warn("사용자 조회 실패 발생 = {}", e.getMessage());

        model.addAttribute("errorMessage", e.getMessage());

        return "common/error/notFound";
    }

    /** 이미지 용량 초과 예외 처리 */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException e,
            RedirectAttributes redirectAttributes
    ) {

        redirectAttributes.addFlashAttribute(
                "errorMessage",
                "파일 크기가 너무 큽니다."
        );

        return "redirect:/admin/product/add";
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public String handleProductNotFoundException(ProductNotFoundException e, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());


        return "redirect:/admin/product/detail";
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
