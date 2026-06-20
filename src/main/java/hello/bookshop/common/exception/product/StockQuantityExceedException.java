package hello.bookshop.common.exception.product;

import hello.bookshop.common.exception.CustomException;

public class StockQuantityExceedException extends CustomException {

    public StockQuantityExceedException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return "EXCEED QUANTITY";
    }
}
