package hello.bookshop.common.exception.cart;


import hello.bookshop.common.exception.CustomException;

public class CartItemNotFoundException extends CustomException {

    public CartItemNotFoundException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return "CART_ITEM_NOT_FOUND";
    }
}
