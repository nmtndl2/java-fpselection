package com.task.controller.creadential;

import com.task.dto.credentialDto.UserRequestDTO;
import com.task.entities.credential.Users;
import com.task.service.creadential.impl.AuthServiceImpl;
import com.task.utility.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<String>  Register(@Valid @RequestBody UserRequestDTO userDto) {
        Users registeredUser = authService.register(userDto);
        return ResponseEntity.ok("User registered successfully with email : " + registeredUser.getEmail() ) ;
    }

    @PostMapping("/login")
    public String Login(@RequestBody Users users) {
        authService.login(users);
        return  jwtUtil.generateToken(users.getEmail());
    }
}
