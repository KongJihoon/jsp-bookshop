package hello.bookshop.common.exception.admin;

import hello.bookshop.common.exception.CustomException;

public class AdminLoginFailedException extends CustomException {

    public AdminLoginFailedException(String message) {

        super(message);
    }

    @Override
    public String getErrorCode() {
        return "LOGIN_FAILED";
    }
}
