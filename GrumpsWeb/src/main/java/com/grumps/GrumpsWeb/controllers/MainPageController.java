package com.grumps.GrumpsWeb.controllers;

import com.grumps.GrumpsWeb.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class MainPageController {

    @GetMapping("/message")
    public ResponseEntity<String> loadMainPage(){
        return new ResponseEntity("Return",HttpStatus.OK);
    }

    @GetMapping("/admin")
    public String loadAdminPage(Model model){
        return "admin.html";
    }
}
