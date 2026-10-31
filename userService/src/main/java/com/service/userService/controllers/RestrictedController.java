package com.service.userService.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("demo")
public class RestrictedController {
    @GetMapping
    public String demoHandler() {
        return "Hello this route is working";
    }
}
