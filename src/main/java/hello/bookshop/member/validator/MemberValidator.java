package hello.bookshop.member.validator;

import hello.bookshop.member.dto.MemberSignUpRequest;
import hello.bookshop.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
@RequiredArgsConstructor
public class MemberValidator {



    public void validateMemberInfo(MemberSignUpRequest request, BindingResult bindingResult) {


        if (request.getPassword() != null && request.getCheckPassword() != null) {

            if (!request.getPassword().equals(request.getCheckPassword())) {

                bindingResult.rejectValue("checkPassword", "passwordMismatch", "비밀번호와 비밀번호 확인이 일치하지 않습니다.");
            }

        }


    }




}
