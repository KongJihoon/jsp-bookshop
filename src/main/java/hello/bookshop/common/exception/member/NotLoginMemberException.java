package hello.bookshop.common.exception.member;

import hello.bookshop.common.exception.CustomException;

public class NotLoginMemberException extends CustomException {

    public NotLoginMemberException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return "NOT LOGIN";
    }
}
