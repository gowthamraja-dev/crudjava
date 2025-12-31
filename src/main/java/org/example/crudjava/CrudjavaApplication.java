package org.example.crudjava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CrudjavaApplication {

    public static void main(String[] args) {
        System.out.println("starting CrudjavaApplication");
        SpringApplication.run(CrudjavaApplication.class, args);
        System.out.println("CrudjavaApplication started");
    }

}
