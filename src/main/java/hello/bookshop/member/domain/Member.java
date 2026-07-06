package hello.bookshop.member.domain;

import hello.bookshop.member.type.MemberType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class Member {

    private Long memberId;

    private String loginId;

    private String password;

    private String name;

    private String email;

    private String phone;

    private String zipcode;

    private String address;

    private String addressDetail;

    private MemberType memberType;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime withdrawnAt;


    @Builder
    private Member(String loginId, String password, String name, String email, String phone, String zipcode, String address, String addressDetail, MemberType memberType) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.zipcode = zipcode;
        this.address = address;
        this.addressDetail = addressDetail;
        this.memberType = memberType;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Member signUp(String loginId, String password, String name, String email, String phone, String zipcode, String address, String addressDetail) {
        return Member.builder()
                .loginId(loginId)
                .password(password)
                .name(name)
                .email(email)
                .phone(phone)
                .zipcode(zipcode)
                .address(address)
                .addressDetail(addressDetail)
                .memberType(MemberType.USER)
                .build();
    }

    public void updateMemberInfo(String email, String phone, String zipcode, String address, String addressDetail) {



        this.email = email;
        this.phone = phone;
        this.zipcode = zipcode;
        this.address = address;
        this.addressDetail = addressDetail;
        this.updatedAt = LocalDateTime.now();

    }

    public void withdraw() {
        this.withdrawnAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }





}
