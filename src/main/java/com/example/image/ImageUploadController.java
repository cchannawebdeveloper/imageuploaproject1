package com.example.image;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class ImageUploadController {

    @Value("${upload.path}")
    private String UPLOAD_DIR;//uploadPath

   // private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";



    @GetMapping("/")
    public String index(
            Model model) {

       /* File dir = new File(UPLOAD_DIR);
        File[] files = dir.listFiles();

        List<String> fileNames = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                fileNames.add(file.getName());
            }
        }*/

        // reload image list
        File dir = new File(UPLOAD_DIR);
        String[] files = dir.list();
        model.addAttribute("images", files != null ? Arrays.asList(files) : Collections.emptyList());
        model.addAttribute("pdfs", listFiles("pdfs"));
        return "index";
    }


    private List<String> listFiles(String subFolder) {
        try {
            Path dir = Paths.get(UPLOAD_DIR + subFolder);
            if (Files.exists(dir)) {
                return Files.list(dir)
                        .map(path -> path.getFileName().toString())
                        .toList();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam(value = "file") MultipartFile file,
                             @RequestParam(value = "oldFileName", required = false) String oldFileName,
                             RedirectAttributes rd)

    {
        if(file.isEmpty()) {
            rd.addFlashAttribute("message", "Please select a file to upload!");
            return "redirect:/";
        }

        try {

            FileUploadUtil.saveFileV2(UPLOAD_DIR, oldFileName, file);
            rd.addFlashAttribute("message", "File uploaded/modified successfully!");

        }
        catch (IOException e) {
            e.printStackTrace();
            rd.addFlashAttribute("message", "File upload failed!");
        }

        // reload image list
        File folder = new File(UPLOAD_DIR);
        String[] files = folder.list();
        rd.addFlashAttribute("images", files != null ? Arrays.asList(files) : Collections.emptyList());
        return "redirect:/"; // Redirect to the root URL
    }

    @GetMapping("/files/view/{filename:.+}")
    @ResponseBody
    public ResponseEntity<byte[]> viewImage(@PathVariable String filename) throws IOException {

        System.out.println("view work");
        Path filePath = Paths.get(UPLOAD_DIR).resolve(filename);
        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        byte[] fileContent = Files.readAllBytes(filePath);
        String contentType = Files.probeContentType(filePath);

        System.out.println("contentType=="+contentType);
        System.out.println("fileContent=="+fileContent);

        return ResponseEntity.ok()
                .header("Content-Type", contentType)
                .body(fileContent);
    }

    // ✅ Show image view page
    @GetMapping("/files/show/{filename:.+}")
    public String showImage(@PathVariable String filename, Model model) {
        model.addAttribute("imageName", filename);
        return "image_view";
    }

   /* @PostMapping("/upload3")
    public String uploadFileBackup(@RequestParam(value = "file") MultipartFile file,
                             @RequestParam(value = "oldFileName", required = false) String oldFileName,
                             RedirectAttributes rd)

    {
        if (file.isEmpty()) {
            rd.addFlashAttribute("message", "Please select a file to upload!");
            return "redirect:/";
        }
        try {

            // Delete old file if exists
            if (oldFileName != null && !oldFileName.isEmpty()) {
                Path oldFilePath = Paths.get(UPLOAD_DIR, oldFileName);
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

            File dir = new File(UPLOAD_DIR);


            if (!dir.exists()) dir.mkdirs();

            // ✅ Save file
            Path path = Paths.get(UPLOAD_DIR, newFileName);
            Files.write(path, file.getBytes());

            System.out.println("Update Successfully!!!");
            rd.addFlashAttribute("message", "File uploaded/modified successfully!");

        }
        catch (IOException e) {
            e.printStackTrace();
            rd.addFlashAttribute("message", "File upload failed!");
        }

        // reload image list
        File folder = new File(UPLOAD_DIR);
        String[] files = folder.list();
        rd.addFlashAttribute("images", files != null ? Arrays.asList(files) : Collections.emptyList());
        return "redirect:/"; // Redirect to the root URL


    }*/



    @PostMapping("/upload2")
    public String uploadImage(
            @RequestParam("image") MultipartFile file,
            Model model)
            throws IOException {
        if (file.isEmpty()) {
            model.addAttribute("message", "Please select an image to upload.");
            return "upload";
        }

        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        // ✅ Generate unique file name
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        long randomPart = (long)(Math.random() * 10000); // 4 digits
        String id = datePart + randomPart;

        // ✅ Keep file extension (e.g. .jpg, .png)
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String newFileName = id + extension;

        // ✅ Save file
       // Path path = Paths.get(uploadPath, newFileName);
      //  Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        // Save new file
        Path path = Paths.get(UPLOAD_DIR + newFileName);
        Files.write(path, file.getBytes());

       // Path path = Paths.get(uploadPath + "/" + file.getOriginalFilename());

       // Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        model.addAttribute("message", "Image uploaded successfully with id: " + id);
        model.addAttribute("images", new File(UPLOAD_DIR).listFiles());
        //return "upload";
        return "redirect:/";
    }

    // ✅ Delete image by filename


    // ✅ AJAX delete endpoint
    @DeleteMapping("/delete/{fileName}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteImage(@PathVariable String fileName) {
        Map<String, String> response = new HashMap<>();

        try {
            Path filePath = Paths.get(UPLOAD_DIR, fileName);
            boolean deleted = Files.deleteIfExists(filePath);
            System.out.println(":deleted:=="+deleted);
            if (deleted) {
                response.put("status", "success");//success
                response.put("message", "Deleted image: " + fileName);
            }
            else {
                response.put("status", "error");
                response.put("message", "Error deleting image: " + fileName);
            }
        } catch (IOException e) {
            response.put("status", "error");
            response.put("message", "Error deleting file: " + e.getMessage());
        }

        return ResponseEntity.ok(response);



    }





}
