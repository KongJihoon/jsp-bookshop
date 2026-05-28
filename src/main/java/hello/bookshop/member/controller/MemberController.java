package hello.bookshop.member.controller;

import hello.bookshop.common.exception.member.DuplicateMemberException;
import hello.bookshop.member.dto.MemberSignUpRequest;
import hello.bookshop.member.service.MemberService;
import hello.bookshop.member.validator.MemberValidator;
import jakarta.servlet.http.HttpServletRequest;
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


    @GetMapping("/signup")
    public String signup(Model model) {


        model.addAttribute("member", new MemberSignUpRequest());
        return "member/signup";
    }

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

    @GetMapping("/signup/check-login-id")
    @ResponseBody
    public boolean checkLoginId(@RequestParam String loginId) {
        return !memberService.existsByLoginId(loginId);
    }

    @GetMapping("/signup/check-email")
    @ResponseBody
    public boolean checkEmail(@RequestParam String email) {
        return !memberService.existsByEmail(email);
    }





}
