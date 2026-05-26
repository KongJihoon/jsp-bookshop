package hello.bookshop.common.exception.member;

import hello.bookshop.common.exception.CustomException;

public class PasswordMismatchException extends CustomException {

    public PasswordMismatchException() {
        super("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
    }

    @Override
    public String getErrorCode() {
        return "PASSWORD_MISMATCH";
    }
}
