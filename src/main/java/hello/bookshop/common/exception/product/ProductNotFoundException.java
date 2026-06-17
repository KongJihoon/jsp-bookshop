package hello.bookshop.common.exception.product;

import hello.bookshop.common.exception.CustomException;

public class ProductNotFoundException extends CustomException {


    public ProductNotFoundException(String message) {
        super(message);
    }


    @Override
    public String getErrorCode() {
        return "";
    }
}
