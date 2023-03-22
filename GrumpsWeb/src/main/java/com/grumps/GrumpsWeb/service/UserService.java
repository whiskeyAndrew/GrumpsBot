package com.grumps.GrumpsWeb.service;

import com.grumps.GrumpsWeb.entity.User;
import com.grumps.GrumpsWeb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService{
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public ResponseEntity createUser(User user){
        User userFromDB = userRepository.findUserByUsername(user.getUsername());
        if (userFromDB!=null){
            //Кидать эксепшн или возвращать "ВСЕХУЕВО"
            //либо сделать свой эксепшн который возвращает "ВСЕХУЕВО"
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        userFromDB = userRepository.findUserByEmail(user.getEmail());
        if (userFromDB!=null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("Ok");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username);
    }
}
