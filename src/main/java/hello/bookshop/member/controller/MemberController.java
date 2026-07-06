package hello.bookshop.member.controller;

import hello.bookshop.common.exception.CustomException;
import hello.bookshop.common.exception.member.DuplicateMemberException;
import hello.bookshop.common.session.SessionConst;
import hello.bookshop.member.dto.request.MemberLoginRequest;
import hello.bookshop.member.dto.request.MemberSignUpRequest;
import hello.bookshop.member.dto.request.MemberUpdateRequest;
import hello.bookshop.member.dto.request.MemberWithdrawRequest;
import hello.bookshop.member.dto.response.MemberInfoResponse;
import hello.bookshop.member.dto.response.MyPageHomeResponse;
import hello.bookshop.member.dto.response.SessionMemberDto;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
            Model model,
            RedirectAttributes redirectAttributes
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

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "회원가입이 완료되었습니다. 로그인해주세요."
        );

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
    , BindingResult bindingResult, HttpServletRequest httpRequest, Model model, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            model.addAttribute("errorMessage", bindingResult.getAllErrors()
                    .get(0).getDefaultMessage());

            return "member/loginForm";
        }


        SessionMemberDto sessionMemberDto = memberService.loginMember(request.getLoginId(), request.getPassword());

        HttpSession session = httpRequest.getSession(true);

        session.setAttribute(SessionConst.LOGIN_MEMBER, sessionMemberDto);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                sessionMemberDto.getName() + "님 환영합니다."
        );

        return "redirect:/";

    }

    /**
     * 로그아웃
     */

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "로그아웃에 성공하였습니다."
        );

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
    public String getMemberDetails(@SessionAttribute(name = SessionConst.LOGIN_MEMBER) SessionMemberDto loginMember,
                                   Model model) {


        MemberInfoResponse memberDetails = memberService.getMemberDetails(loginMember.getMemberId());

        model.addAttribute("member", memberDetails);


        return "member/info";
    }

    /**
     * 회원 정보 수정 폼 (마이 페이지)
     */
    @GetMapping("/edit")
    public String editForm(@SessionAttribute(name = SessionConst.LOGIN_MEMBER) SessionMemberDto loginMember,
                           Model model) {

        MemberInfoResponse member = memberService.getMemberDetails(loginMember.getMemberId());

        model.addAttribute("member", member);

        return "member/edit";

    }

    /**
     * 회원 정보 수정 기능 (마이 페이지)
     */
    @PostMapping("/edit")
    public String edit(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) SessionMemberDto loginMember,
            @Validated @ModelAttribute("member") MemberUpdateRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", bindingResult.getAllErrors().get(0).getDefaultMessage());
            MemberInfoResponse memberDetails =
                    memberService.getMemberDetails(loginMember.getMemberId());

            model.addAttribute("member", memberDetails);
            return "member/edit";
        }

        try {

            memberService.updateMemberInfo(loginMember.getMemberId(), request);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "회원정보가 수정되었습니다."
            );

            return "redirect:/member/info";

        } catch (DuplicateMemberException e) {

            log.warn("회원정보 수정 이메일 중복 발생 = {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());

            MemberInfoResponse memberDetails = memberService.getMemberDetails(loginMember.getMemberId());

            model.addAttribute("member", memberDetails);

            return "member/edit";

        }

    }

    /**
     * 회원탈퇴
     */
    @PostMapping("/withdraw")
    public String withdraw(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) SessionMemberDto loginMember,
            @Validated @ModelAttribute MemberWithdrawRequest request,
            BindingResult bindingResult,
            HttpServletRequest httpRequest,
            RedirectAttributes redirectAttributes
            ) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", bindingResult.getAllErrors().get(0).getDefaultMessage()
            );

            return "redirect:/member/edit";

        }

        try {
            memberService.withdrawMember(loginMember.getMemberId(), request.getPassword());

            HttpSession session = httpRequest.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "회원탈퇴가 완료되었습니다."
            );

            return "redirect:/";

        } catch (CustomException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

            return "redirect:/member/edit";
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
