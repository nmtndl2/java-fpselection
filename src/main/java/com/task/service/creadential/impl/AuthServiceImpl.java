package com.task.service.creadential.impl;

import com.task.dto.credential.dto.UserRequestDTO;
import com.task.entities.credential.Role;
import com.task.entities.credential.Users;
import com.task.exception.UserAlreadyExistsException;
import com.task.repository.credential.RoleRepository;
import com.task.repository.credential.UsersRepository;
import com.task.service.creadential.AuthService;
import com.task.utility.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;

    private final UsersRepository usersRepository;

    private final RoleRepository roleRepository;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtService;

    @Override
    public Users register(UserRequestDTO usersDto) {
        if (usersRepository.findByEmail(usersDto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User already exists with email: " + usersDto.getEmail());
        }

        Users users = new Users();
        users.setEmail(usersDto.getEmail());
        users.setPassword(passwordEncoder.encode(usersDto.getPassword()));

        Set<Role> roles = usersDto.getRoles().stream()
                .map(roleName -> roleRepository.findByRole("ROLE_" + roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                .collect(Collectors.toSet());

        users.setRoles(roles);

        return usersRepository.save(users);
    }

    @Override
    public String login(Users users) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(users.getEmail(), users.getPassword()));

        if (authentication.isAuthenticated()) {
            return "Jwt Token : "+ jwtService.generateToken(users.getEmail());
        }
        return "Fail";
    }
}
