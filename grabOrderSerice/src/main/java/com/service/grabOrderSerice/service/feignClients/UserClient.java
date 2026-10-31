package com.service.grabOrderSerice.service.feignClients;

import com.service.grabOrderSerice.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "userService")
public interface UserClient {
    @GetMapping("/auth/getUser")
    public UserResponseDto getUserHandler(@RequestHeader("Authorization") String authHeader) throws Exception;
    @GetMapping("/auth/{id}")
    public UserResponseDto getUserByIdHandler(@PathVariable("id") String userId) throws Exception;
}
