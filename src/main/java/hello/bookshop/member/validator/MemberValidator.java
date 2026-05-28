package hello.bookshop.member.validator;

import hello.bookshop.common.exception.CustomException;
import hello.bookshop.common.exception.member.PasswordMismatchException;
import hello.bookshop.member.dto.MemberSignUpRequest;
import hello.bookshop.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
@RequiredArgsConstructor
public class MemberValidator {


    private final MemberService memberService;

    public void validateMemberInfo(MemberSignUpRequest request, BindingResult bindingResult) {


        if (request.getPassword() != null && request.getCheckPassword() != null) {

            if (!request.getPassword().equals(request.getCheckPassword())) {

                throw new PasswordMismatchException();
            }

        }


    }

}
