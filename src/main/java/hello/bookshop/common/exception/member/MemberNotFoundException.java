package hello.bookshop.common.exception.member;

import hello.bookshop.common.exception.CustomException;

public class MemberNotFoundException extends CustomException {

    public MemberNotFoundException() {
        super("사용자를 찾을 수 없습니다.");
    }

    @Override
    public String getErrorCode() {
        return "USER_NOT_FOUND";
    }
}
