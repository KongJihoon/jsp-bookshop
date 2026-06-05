package hello.bookshop.common.exception.product;

import hello.bookshop.common.exception.CustomException;

public class FileUploadException extends CustomException {


    public FileUploadException(String message) {

        super(message);
    }


    @Override
    public String getErrorCode() {
        return "";
    }
}
