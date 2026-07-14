package hello.bookshop.common.exception.member;

import hello.bookshop.common.exception.CustomException;

public class MemberFindException extends CustomException {
    public MemberFindException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return "MEMBER_FIND_FAILED";
    }
}
