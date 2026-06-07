package hello.bookshop.common.exception.category;


import hello.bookshop.common.exception.CustomException;

public class NotFoundCategoryException extends CustomException {


    public NotFoundCategoryException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return "";
    }
}
