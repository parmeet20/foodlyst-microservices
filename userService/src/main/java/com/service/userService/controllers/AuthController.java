package com.service.userService.controllers;

import com.service.userService.dto.LoginDto;
import com.service.userService.dto.RegisterUserDto;
import com.service.userService.dto.UserResponseDto;
import com.service.userService.entities.User;
import com.service.userService.services.jwtServices.JWTService;
import com.service.userService.services.jwtServices.MyUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final MyUserService userService;
    private final JWTService jwtService;


    @PostMapping("/login")
    public String login(@RequestBody LoginDto request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        if (auth.isAuthenticated()) return jwtService.generateToken(request);
        return "Not Authenticated";
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterUserDto dto) throws Exception {
        User savedUser = userService.createUser(dto);
        return ResponseEntity.ok("User registered successfully with username: " + savedUser.getUsername());
    }

    @PostMapping("/{id}/subscribe")
    public ResponseEntity<?> register(@PathVariable("id") String userId, @RequestHeader("Authorization") String authHeader) throws Exception {
        UserResponseDto savedUser = userService.subscribeEmail(userId);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/getUser")
    public UserResponseDto getUserHandler(@RequestHeader("Authorization") String authHeader) throws Exception {
        // Ensure header exists and starts with Bearer
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Remove "Bearer "
            return jwtService.getUserFromToken(token);
        } else {
            return null;
        }
    }

    @GetMapping("/{id}")
    public UserResponseDto getUserByIdHandler(@PathVariable("id") String userId) throws Exception {
        return ResponseEntity.ok(jwtService.getUserById(userId)).getBody();
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable("id") String userId, @RequestHeader("Authorization") String authHeader) throws Exception {
        String token = authHeader.substring(7);
        UserResponseDto usr = jwtService.getUserFromToken(token);
        userService.deleteUser(userId, authHeader);
        return "User deleted successfully with id" + usr.getUserId();
    }

}
