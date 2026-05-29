package hello.bookshop.member.controller;

import hello.bookshop.common.exception.member.DuplicateMemberException;
import hello.bookshop.common.exception.member.LoginFailedException;
import hello.bookshop.common.session.SessionConst;
import hello.bookshop.member.domain.Member;
import hello.bookshop.member.dto.MemberLoginRequest;
import hello.bookshop.member.dto.MemberSignUpRequest;
import hello.bookshop.member.service.MemberService;
import hello.bookshop.member.validator.MemberValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberValidator memberValidator;


    /**
     * 회원가입 폼
     */
    @GetMapping("/signup")
    public String signup(Model model) {


        model.addAttribute("member", new MemberSignUpRequest());
        return "member/signup";
    }

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public String signup(
            HttpServletRequest servletRequest,
            @Validated @ModelAttribute("member") MemberSignUpRequest request,
            BindingResult bindingResult,
            Model model
    ) {



        memberValidator.validateMemberInfo(request, bindingResult);

        if (bindingResult.hasErrors()) {

            model.addAttribute("errorMessage",
                    bindingResult.getAllErrors()
                            .get(0)
                            .getDefaultMessage());

            return "member/signup";
        }

        try {
            memberService.signUp(request);
        } catch (DuplicateMemberException e) {

            model.addAttribute("errorMessage", e.getMessage());

            return "member/signup";
        }

        log.info("회원가입 완료 = {}", request.getLoginId());


        return "redirect:/";
    }
    /**
     * 로그인 폼
     */
    @GetMapping("/login")
    public String loingForm(@ModelAttribute("member")MemberLoginRequest request) {
        return "member/loginForm";
    }

    @PostMapping("/login")
    public String loginForm(@Validated @ModelAttribute("member") MemberLoginRequest request
    , BindingResult bindingResult, HttpServletRequest httpRequest, Model model) {

        if (bindingResult.hasErrors()) {
            return "member/loginForm";
        }

        try {
            Member member = memberService.loginMember(request.getLoginId(), request.getPassword());

            HttpSession session = httpRequest.getSession(false);

            session.setAttribute(SessionConst.LOGIN_MEMBER, member);
            return "redirect:/";

        } catch (LoginFailedException e) {

            model.addAttribute("errorMessage", e.getMessage());
            return "member/loginForm";
        }


    }


    /**
     * 아이디 존재 검증
     */
    @GetMapping("/signup/check-login-id")
    @ResponseBody
    public boolean checkLoginId(@RequestParam String loginId) {
        return !memberService.existsByLoginId(loginId);
    }


    /**
     * 이메일 존재 검증
     */
    @GetMapping("/signup/check-email")
    @ResponseBody
    public boolean checkEmail(@RequestParam String email) {
        return !memberService.existsByEmail(email);
    }





}
