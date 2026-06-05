package hello.bookshop.file;

import hello.bookshop.common.exception.product.FileUploadException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileStore {


    private final String uploadDir = "/Users/ji/bookshop-upload/";


    public UploadFile storeFile(MultipartFile multipartFile) {

        if (multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }


        try {

            String originalFilename = multipartFile.getOriginalFilename();

            String storedFileName = createStoredFileName(originalFilename);

            createUploadDirIfNotExists();

            multipartFile.transferTo(new File(uploadDir + storedFileName));

            return new UploadFile(
                    originalFilename,
                    storedFileName,
                    "/upload/" + storedFileName
            );


        } catch (IOException e) {
            throw new FileUploadException("파일 저장 중 오류가 발생하였습니다.");
        }

    }

    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles) {

        List<UploadFile> uploadFiles = new ArrayList<>();

        if (multipartFiles == null || multipartFiles.isEmpty()) {
            return uploadFiles;
        }

        for (MultipartFile multipartFile : multipartFiles) {

            if (multipartFile == null || multipartFile.isEmpty()) {
                continue;
            }

            UploadFile uploadFile = storeFile(multipartFile);
            uploadFiles.add(uploadFile);
        }

        return uploadFiles;
    }

    private void createUploadDirIfNotExists() {
        File file = new File(uploadDir);

        if (!file.exists()) {
            file.mkdirs();
        }
    }


    private String createStoredFileName(String originalFileName) {

        String ext = extractExt(originalFileName);

        return UUID.randomUUID() + "." + ext;
    }

    private String extractExt(String originalFileName) {

        if (originalFileName == null || originalFileName.isEmpty()) {
            throw new FileUploadException("파일명이 존재하지 않습니다.");
        }

        int pos = originalFileName.indexOf(".");

        if (pos == -1) {
            throw new FileUploadException("확장자가 없는 파일은 업로드할 수 없습니다.");
        }



        return originalFileName.substring(pos + 1);
    }

}
