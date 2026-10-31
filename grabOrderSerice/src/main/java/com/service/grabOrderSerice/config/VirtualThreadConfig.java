package com.service.grabOrderSerice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class VirtualThreadConfig {
    @Bean
    public Executor virtualThreadExec(){
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
