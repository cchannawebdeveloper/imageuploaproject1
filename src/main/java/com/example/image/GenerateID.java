package com.example.image;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GenerateID {

    public static void main(String[] args) {


        // Date part
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        long randomPart = (long)(Math.random() * 10000); // 4 digits
        System.out.println("Hello World!"+datePart);
        System.out.println("Hello World!randomPart="+randomPart);
        String id = datePart + randomPart;
        System.out.println("Hello World!id="+id);

    }
}
