package br.com.elo.eloapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EloApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(EloApiApplication.class, args);
    }

}
