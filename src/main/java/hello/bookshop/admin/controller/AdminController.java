package hello.bookshop.admin.controller;

import hello.bookshop.admin.dto.AdminLoginRequest;
import hello.bookshop.admin.service.AdminService;
import hello.bookshop.common.session.SessionConst;
import hello.bookshop.member.dto.response.SessionMemberDto;
import hello.bookshop.member.type.MemberType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/login")
    public String loginForm(HttpServletRequest request,Model model) {


        HttpSession session = request.getSession(false);

        if (session != null) {
            SessionMemberDto loginMember = (SessionMemberDto) session.getAttribute(SessionConst.LOGIN_MEMBER);

            if (loginMember != null && loginMember.getMemberType() == MemberType.ADMIN) {
                return "redirect:/admin/dashboard";
            }
        }

        model.addAttribute("admin", new AdminLoginRequest());

        return "admin/login";
    }


    @PostMapping("/login")
    public String login(
            @Validated @ModelAttribute("admin") AdminLoginRequest adminRequest,
            BindingResult bindingResult,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {

            model.addAttribute("errorMessage", bindingResult.getAllErrors()
                    .get(0).getDefaultMessage());

            return "admin/login";
        }

        SessionMemberDto loginAdmin = adminService.loginAdmin(adminRequest);
        HttpSession session = request.getSession();

        session.setAttribute(SessionConst.LOGIN_MEMBER, loginAdmin);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                loginAdmin.getName() + "님 환영합니다."
        );

        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

}
