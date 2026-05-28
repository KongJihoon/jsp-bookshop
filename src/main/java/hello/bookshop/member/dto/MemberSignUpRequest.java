package hello.bookshop.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// 테스트용 빌더
@Builder
public class MemberSignUpRequest {

    @NotBlank(message = "로그인 아이디는 필수입니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{2,20}$",
            message = "로그인 아이디는 2~20자의 영문과 숫자 조합이어야 합니다."
    )
    private String loginId;


    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$", message = "비밀번호는 최소 8자 이상이어야 하며, 영문자, 숫자, 특수문자를 포함해야 합니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$", message = "비밀번호 확인은 최소 8자 이상이어야 하며, 영문자, 숫자, 특수문자를 포함해야 합니다.")
    private String checkPassword;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다. 예: 010-1234-5678")
    private String phone;

    @NotBlank(message = "우편번호는 필수입니다.")
    private String zipcode;

    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    @NotBlank(message = "상세 주소는 필수입니다.")
    private String addressDetail;
}