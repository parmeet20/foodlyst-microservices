package com.service.offerService.service.feignClientServices;

import com.service.offerService.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "userService")
public interface UserClient {
    @GetMapping("/auth/getUser")
    public UserResponseDto getUserHandler(@RequestHeader("Authorization") String authHeader);
}
