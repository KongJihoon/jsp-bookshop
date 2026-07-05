package hello.bookshop.common.exception.order;

import hello.bookshop.common.exception.CustomException;

public class OrderInfoException extends CustomException {

    public OrderInfoException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return "ORDER_INFO_NOT_VALID";
    }
}
