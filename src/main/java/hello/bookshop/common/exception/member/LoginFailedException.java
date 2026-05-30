package hello.bookshop.common.exception.member;

import hello.bookshop.common.exception.CustomException;

public class LoginFailedException extends CustomException {

    public LoginFailedException() {

        super("아이디 또는 비밀번호가 올바르지 않습니다.");
    }

    @Override
    public String getErrorCode() {
        return "LOGIN_FAILED";
    }
}
