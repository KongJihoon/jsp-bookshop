package hello.bookshop.file;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UploadFile {

    private final String originalFileName;

    private final String storedFileName;

    private final String filePath;

}
