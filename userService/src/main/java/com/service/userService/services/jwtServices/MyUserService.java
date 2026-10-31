package com.service.userService.services.jwtServices;

import com.service.userService.dto.RegisterUserDto;
import com.service.userService.dto.UserResponseDto;
import com.service.userService.repo.UserRepository;
import com.service.userService.entities.User;
import com.service.userService.exception.UserAlreadyExistsException;
import com.service.userService.services.kafka.kafkaProducerService.CreateCorrespondingBalanceAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MyUserService implements UserDetailsService {

    private final CreateCorrespondingBalanceAccount createBalanceAccountKafkaService;
    private final UserRepository repo;
    private final JWTService jwtService;
    private final PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new UserPrincipal(user);
    }

    public UserResponseDto subscribeEmail(String userId) {
        Optional<User> user = repo.findById(userId);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        boolean isEmailSubscribed = user.get().isEmailSubscribed();
        user.get().setEmailSubscribed(!isEmailSubscribed);
        return new UserResponseDto(user.get().getUsername(), user.get().getId(), user.get().getEmail(), user.get().isEmailSubscribed(), user.get().getRole());
    }

    public User createUser(RegisterUserDto dto) {
        User exists = repo.findByUsername(dto.getUsername());
        if (exists != null) {
            throw new UserAlreadyExistsException("User already exists with username: " + dto.getUsername());
        }

        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(encoder.encode(dto.getPassword()))
                .phoneNo(dto.getPhoneNo())
                .address(dto.getAddress())
                .role(dto.getRole())
                .build();

        User savedUser = repo.save(user);
        createBalanceAccountKafkaService.createBalanceAccountProducer(savedUser.getId());
        return savedUser;
    }

    public void deleteUser(String userId, String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            UserResponseDto dto = jwtService.getUserFromToken(token);
            repo.deleteById(userId);
        }
    }

}
