package com.service.orderService.service.feignClients;

import com.service.orderService.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "userService")
public interface UserClient {
    @GetMapping("/auth/{id}")
    public UserResponseDto getUserByIdHandler(@PathVariable("id") String userId) throws Exception;
}
