package com.taverule.rag.domain.rag.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomepageController {

    @GetMapping("/home")
    public String home(){
        return "homepage";
    }

    @GetMapping("/")
    public String rootPage(){
        return "homepage";
    }

}
