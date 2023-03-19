package com.grumps.GrumpsWeb.service;

import com.grumps.GrumpsWeb.entity.User;
import com.grumps.GrumpsWeb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public void createUser(User user){
        User userFromDB = userRepository.findUserByUsername(user.getUsername());
        if (userFromDB!=null){
            //Кидать эксепшн или возвращать "ВСЕХУЕВО"
            //либо сделать свой эксепшн который возвращает "ВСЕХУЕВО"
        }

        userFromDB = userRepository.findUserByEmail(user.getEmail());
        if (userFromDB!=null){

        }

        userRepository.save(user);
    }
}
