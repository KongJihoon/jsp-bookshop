package hello.bookshop.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateRequest {

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다. 예: 010-1234-5678")
    @NotBlank(message = "전화번호를 입력해주세요.")
    private String phone;


    @NotBlank(message = "우편번호를 입력해주세요.")
    private String zipcode;

    @NotBlank(message = "주소를 입력해주세요.")
    private String address;

    @NotBlank(message = "상세주소를 입력해주세요.")
    private String addressDetail;

}
