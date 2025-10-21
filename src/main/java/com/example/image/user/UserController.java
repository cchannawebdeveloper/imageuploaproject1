package com.example.image.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {

    // 🔹 This method adds "countries" to all model data
    @ModelAttribute("countries")
    public List<String> countriesList() {
        return List.of("Cambodia", "Thailand", "Vietnam", "Malaysia");
    }


    // 🔹 Show form page
    @GetMapping("/register")
    public String showForm(
            Model model
    ) {

        System.out.println("Register Work!!");
        model.addAttribute("user", new User());  // Add empty user for form
        return "register";  // return template name
    }


    // 🔹 Handle form submission
    @PostMapping("/register2")
    public String submitForm(@ModelAttribute User user,
                             Model model
    ) {

        System.out.println("Register Work!!"+user.toString());
        model.addAttribute("message", "User registered successfully!");
        model.addAttribute("user", user);
        return "result";
    }

    // Handle form submission and redirect
    @PostMapping("/register")
    public String handleSubmit(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        // You could save the user here to DB

        // Add message to redirect (Flash = temporary)
        redirectAttributes.addFlashAttribute("message", "User registered successfully!");
        redirectAttributes.addFlashAttribute("user", user);

        // Redirect to another page
        return "redirect:/result";
    }


    // Show result page
    @GetMapping("/result")
    public String showResultPage(Model model) {
        // Flash attributes are available here automatically
        return "result";
    }

    @PostMapping("/endpoint")
    @ResponseBody // <-- ensures response is serialized as JSON
    public ResponseEntity<Map<String, String>> saveUserEndPoint(
            @ModelAttribute User user
    ) {
        Map<String, String> resp = new HashMap<>();

        // simple server-side validation
        if(user.getName() == null && user.getName().trim().isEmpty()) {
            resp.put("status", "Error");
            resp.put("message", "Please enter your name");
            return ResponseEntity.badRequest().body(resp);  // HTTP 400
        }

        // pretend to save user here (e.g., repo.save(user))
        resp.put("status", "success");
        resp.put("message", "Saved user: " + user.getName() + " (" + (user.getEmail() == null ? "" : user.getEmail()) + ")");
        return ResponseEntity.ok(resp); // HTTP 200
    }


}
