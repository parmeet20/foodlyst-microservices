package com.service.userService.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private String username;
    private String userId;
    private String email;
    private boolean isEmailSubscribed;
    private String role;
}
