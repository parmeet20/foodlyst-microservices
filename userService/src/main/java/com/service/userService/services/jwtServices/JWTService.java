package com.service.userService.services.jwtServices;

import com.service.userService.dto.LoginDto;
import com.service.userService.dto.UserResponseDto;
import com.service.userService.entities.User;
import com.service.userService.exception.InvalidTokenException;
import com.service.userService.repo.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

@Service
@Slf4j
public class JWTService {

    private final UserRepository userRepo;

    public JWTService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Value("${jwt.private-key}")
    private String privateKeyStr;
    @Value("${jwt.public-key}")
    private String publicKeyStr;

    public String generateToken(LoginDto dto) {
        Map<String, Object> claims = new HashMap<>();
        User usr = userRepo.findByUsername(dto.getUsername());
        claims.put("username", dto.getUsername());
        claims.put("role", usr.getRole());
        claims.put("email", usr.getEmail());
        claims.put("emailSubscribed", usr.isEmailSubscribed());
        claims.put("userId", usr.getId());
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(dto.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 5))
                .and()
                .signWith(getPrivateKey(), Jwts.SIG.RS256)
                .compact();
    }

    private PrivateKey getPrivateKey() {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(privateKeyStr);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        } catch (Exception e) {
            throw new RuntimeException("Error loading private key", e);
        }
    }

    private PublicKey getPublicKey() {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(publicKeyStr);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (Exception e) {
            throw new RuntimeException("Error loading public key", e);
        }
    }

    public String extractUsernameFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getPublicKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.get("username", String.class);
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid or expired token");
        }
    }

    public UserResponseDto getUserFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getPublicKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return new UserResponseDto(
                    claims.get("username", String.class),
                    claims.get("userId", String.class),
                    claims.get("email", String.class),
                    claims.get("emailSubscribed", Boolean.class),
                    claims.get("role", String.class)
            );
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid or expired token");
        }
    }

    public UserResponseDto getUserById(String userId) {
        Optional<User> u = userRepo.findById(userId);
        if (u.isEmpty()) {
            throw new InvalidTokenException("user not found with id " + userId);
        }
        User user = u.get();
        return new UserResponseDto(user.getUsername(), user.getId(), user.getEmail(), user.isEmailSubscribed(), user.getRole());
    }

    public boolean validateToken(String token) {
        if (token == null || token.isBlank()) {
            throw new InvalidTokenException("Token is missing");
        }

        // Remove Bearer prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getPublicKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // ✅ Check expiration manually
            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                throw new InvalidTokenException("Token has expired");
            }

            return true; // ✅ Token is valid
        } catch (InvalidTokenException e) {
            throw e; // rethrow our custom exception
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            throw new InvalidTokenException("Invalid or malformed token");
        }
    }

}
