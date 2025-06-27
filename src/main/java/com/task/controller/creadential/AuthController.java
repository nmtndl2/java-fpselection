package com.task.controller.creadential;

import com.task.dto.credential.dto.UserRequestDTO;
import com.task.entities.credential.Users;
import com.task.service.creadential.impl.AuthServiceImpl;
import com.task.utility.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;

    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRequestDTO userDto) {
        Users registeredUser = authService.register(userDto);
        return ResponseEntity.ok("User registered successfully with email : " + registeredUser.getEmail());
    }

    @PostMapping("/login")
    public String authenticateUser(@RequestBody Users users) {
        authService.login(users);
        return jwtUtil.generateToken(users.getEmail());
    }
}
