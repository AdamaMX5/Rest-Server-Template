package de.freeschool.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class FreeSchoolApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FreeSchoolApiApplication.class, args);
    }

}
