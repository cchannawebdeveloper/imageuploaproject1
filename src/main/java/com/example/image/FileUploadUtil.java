package com.example.image;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class FileUploadUtil {

    //keep old image
    public static void saveFileV2(
            String uploadDir
            , String oldFileName
            , MultipartFile file
    ) throws IOException {

        // ✅ Create upload folder if not exists
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        // ✅ Generate unique file name (keep old files)
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int randomPart = ThreadLocalRandom.current().nextInt(1000, 10000); // 4-digit random

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = "";

        if (StringUtils.hasText(originalFilename) && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String newFileName = String.format("%s%d%s", timestamp, randomPart, extension);

        // ✅ Save file (do NOT replace)
        Path targetPath = uploadPath.resolve(newFileName);

        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

    }

    //replace image:
    /*
    public void saveFile(
            String uploadDir
            , String oldFileName
            , MultipartFile file
    ) throws IOException {

        // Delete old file if exist
        if (oldFileName != null && !oldFileName.isEmpty()) {
            Path oldFilePath = Paths.get(uploadDir, oldFileName);
            Files.deleteIfExists(oldFilePath);
        }

        // Generate a unique file name for upload
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int randomPart = ThreadLocalRandom.current().nextInt(1000, 10000); // 4-digit random

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = "";

        if (StringUtils.hasText(originalFilename) && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String newFileName = String.format("%s%d%s", timestamp, randomPart, extension);

        // Select folder by type
       // String subFolder = getSubFolder(extension);

        Path uploadPath = Paths.get(uploadDir);

        //create directories
        if(!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }


        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = uploadPath.resolve(newFileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

            // Build file URL (works with static resource mapping)
           // String fileUrl = "/" + subFolder + "/" + newFileName;

        } catch (IOException ex) {
            throw new IOException("Cound not save file "+ newFileName, ex);
        }

    }                  */

    private static String getSubFolder(String extension) {
        if (extension.matches("(?i)\\.(jpg|jpeg|png|gif|bmp|webp)$")) {
            return "images";
        } else if (extension.matches("(?i)\\.(mp4|avi|mov|mkv|webm)$")) {
            return "videos";
        } else if (extension.matches("(?i)\\.(pdf)$")) {
            return "pdfs";
        } else {
            return "others";
        }
    }

    public static void removeDir(String catDir) {
        clearDir(catDir);
        try {
            Files.delete(Paths.get(catDir));

        } catch (IOException e) {
            e.printStackTrace();
            //logger.error("Could not remove directory: "+catDir);
        }

    }

    public static void clearDir(String dir) {
        Path dirPath = Paths.get(dir);
        try {

            Files.list(dirPath).forEach(file -> {
                if(!Files.isDirectory(file)) {
                    try {
                        Files.delete(file);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        //logger.error("Could not delete file "+file);
                    }
                }
            });
        }
        catch (IOException ex) {
            ex.printStackTrace();
           // logger.error("Could not delete file: "+dir);
        }



    }
}
