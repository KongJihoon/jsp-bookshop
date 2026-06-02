package hello.bookshop.member.controller;

import hello.bookshop.common.session.SessionConst;
import hello.bookshop.member.dto.MemberInfoResponse;
import hello.bookshop.member.dto.SessionMemberDto;
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
     * 회원가입 처리
     */
    @PostMapping("/signup")
    public String signup(
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


        memberService.signUp(request);

        log.info("회원가입 완료 = {}", request.getLoginId());


        return "redirect:/";
    }
    /**
     * 로그인 폼
     */
    @GetMapping("/login")
    public String loginForm(Model model) {

        model.addAttribute("member", new MemberLoginRequest());

        return "member/loginForm";
    }

    /**
     * 로그인 처리
     */
    @PostMapping("/login")
    public String loginForm(@Validated @ModelAttribute("member") MemberLoginRequest request
    , BindingResult bindingResult, HttpServletRequest httpRequest, Model model) {

        if (bindingResult.hasErrors()) {

            model.addAttribute("errorMessage", bindingResult.getAllErrors()
                    .get(0).getDefaultMessage());

            return "member/loginForm";
        }


        SessionMemberDto sessionMemberDto = memberService.loginMember(request.getLoginId(), request.getPassword());

        System.out.println(sessionMemberDto.getMemberId());


        HttpSession session = httpRequest.getSession(true);

        session.setAttribute(SessionConst.LOGIN_MEMBER, sessionMemberDto);


        return "redirect:/";

    }

    /**
     * 로그아웃
     */

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return "redirect:/";
    }

    @GetMapping("/mypage")
    public String myPage() {

        return "member/mypage";

    }


    /**
     * 회원 정보 조회 (마이 페이지)
     */
    @GetMapping("/info")
    public String getMemberDetails(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) SessionMemberDto loginMember,
                                   Model model) {


        MemberInfoResponse memberDetails = memberService.getMemberDetails(loginMember.getMemberId());

        model.addAttribute("member", memberDetails);


        return "member/info";
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
