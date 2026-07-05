package hello.bookshop.common.exception.product;

import hello.bookshop.common.exception.CustomException;

public class AdminProductNotFoundException extends CustomException {

    public AdminProductNotFoundException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return "ADMIN_PRODUCT_NOT_FOUND";
    }
}
