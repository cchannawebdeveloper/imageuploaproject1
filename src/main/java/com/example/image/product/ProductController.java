package com.example.image.product;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api")
public class ProductController {


    @GetMapping("/product")
    public String product(

    ) {

        return "product";
    }


    @PostMapping("/saveProduct")
    @ResponseBody
    public Map<String, String> saveUser(@ModelAttribute Product product) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Product saved: country=" + product.getCountry() +", name=" + product.getName() + " (" + product.getEmail() + ")");
        return response;
    }
}
