package hello.bookshop.common.exception.member;

import hello.bookshop.common.exception.CustomException;

public class MemberWithdrawNotAllowedException extends CustomException {

    public MemberWithdrawNotAllowedException() {
        super("진행 중인 주문이 존재하여 현재 회원탈퇴가 불가합니다.");
    }

    @Override
    public String getErrorCode() {
        return "MEMBER_WITHDRAW_NOT_ALLOWED";
    }
}
