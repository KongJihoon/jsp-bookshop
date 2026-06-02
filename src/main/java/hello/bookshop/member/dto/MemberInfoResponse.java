package hello.bookshop.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberInfoResponse {

    private String loginId;

    private String name;

    private String email;

    private String phone;

    private String zipcode;

    private String address;

    private String addressDetail;


}
