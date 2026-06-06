package hello.bookshop.member.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberType {

    USER("일반 회원"),
    ADMIN("관리자");

    private final String description;
}
