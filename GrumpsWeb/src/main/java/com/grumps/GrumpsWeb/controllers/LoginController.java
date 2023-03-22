package com.grumps.GrumpsWeb.controllers;

import com.grumps.GrumpsWeb.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Controller
public class LoginController {
    @GetMapping("/login")
    public String loadLoginPage(Model model){
        return "login.html";
    }

//    @PostMapping("/login")
//    public ResponseEntity<String> login(User user){
//        System.out.println(user.getUsername());
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
}
