package com.chat;

import com.chat.netty.WSServer;
import com.chat.utils.IdWorker;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
@MapperScan("com.chat.mapper")
public class ChatApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(ChatApplication.class,args);

    }

    @Bean
    public IdWorker getId() {
        return new IdWorker(1,1);
    }

    @Override
    public void run(String... args) throws Exception {
        new WSServer().start();
    }
}

