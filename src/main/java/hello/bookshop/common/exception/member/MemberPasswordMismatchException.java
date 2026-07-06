package hello.bookshop.common.exception.member;

import hello.bookshop.common.exception.CustomException;

public class MemberPasswordMismatchException extends CustomException {

    public MemberPasswordMismatchException() {

        super("비밀번호가 일치하지 않습니다.");
    }

    @Override
    public String getErrorCode() {
        return "PASSWORD_MISMATCH";
    }
}
