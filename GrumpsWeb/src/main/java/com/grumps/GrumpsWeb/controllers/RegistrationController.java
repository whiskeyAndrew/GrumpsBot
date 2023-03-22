package com.grumps.GrumpsWeb.controllers;

import com.grumps.GrumpsWeb.entity.User;
import com.grumps.GrumpsWeb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.Instant;

@Controller
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;
    @GetMapping("/registration")
    //ModelАддАтрибут пихает в форму сразу объект
    public String loadRegistrationPage(){
        //model.addAttribute("userForm",new User());
        return "registration.html";
    }

    @PostMapping("/registration")
    public String addUser(@ModelAttribute("userForm")  User user){
        user.setRegistrationDate(Instant.now());
        user.setRoles("ADMIN");
        userService.createUser(user);
        return "redirect:/";
    }
}
