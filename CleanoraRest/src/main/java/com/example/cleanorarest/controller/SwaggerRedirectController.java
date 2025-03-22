package com.example.cleanorarest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cleonora/v1")
public class SwaggerRedirectController {

    @GetMapping("/")
    public String redirect() {
        return "redirect:/cleonora/v1/swagger-ui/index.html";
    }
}
