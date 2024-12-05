package com.sounganization.botanify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BotanifyApplication {

    public static void main(String[] args) {
        SpringApplication.run(BotanifyApplication.class, args);
    }

}
