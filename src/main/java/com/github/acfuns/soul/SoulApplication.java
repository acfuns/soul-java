package com.github.acfuns.soul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SoulApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoulApplication.class, args);
    }

}
