package hello.bookshop.common.exception.member;

import hello.bookshop.common.exception.CustomException;

public class DuplicateMemberException extends CustomException {

    private final String field;
    private final String displayName;

    public DuplicateMemberException(String field, String displayName) {
        super(displayName + "은(는) 이미 사용 중입니다.");
        this.field = field;
        this.displayName = displayName;

    }

    public String getField() {
        return this.field;
    }


    @Override
    public String getErrorCode() {
        return "DUPLICATE_" + field.toUpperCase();
    }
}
