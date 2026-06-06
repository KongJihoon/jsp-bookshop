package hello.bookshop.member.dto;


import hello.bookshop.member.domain.Member;
import hello.bookshop.member.type.MemberType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor // 프레임워크 역직렬화를 위한 기본 생성자
public class SessionMemberDto implements Serializable {

    // 클래스 구조 변경 시 세션 끊김을 방지하기 위해 1L로 고정
    private static final long serialVersionUID = 1L;

    private Long memberId;

    private String loginId;

    private String name;

    private MemberType memberType;

    public SessionMemberDto(Member member) {
        this.memberId = member.getMemberId();
        this.loginId = member.getLoginId();
        this.name = member.getName();
        this.memberType = member.getMemberType();
    }





}
