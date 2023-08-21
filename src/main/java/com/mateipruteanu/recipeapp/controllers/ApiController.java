package com.mateipruteanu.recipeapp.controllers;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController {
    @GetMapping("/")
    public String getInfo() {
        return "Restful API for a recipe app";
    }

    @GetMapping("/secure")
    public String getSecureInfo() {
        return "This is a secure endpoint";
    }

    @GetMapping("/auth/notSecure")
    public String getNotSecureInfo() {
        return "This is not a secure endpoint";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String getAdminInfo() {
        return "This is an admin endpoint";
    }


    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String getUserInfo() {
        return "This is a user endpoint";
    }
}
