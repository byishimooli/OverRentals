package com.erealestate.houserent_sell;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = { "com.erealestate.houserent_sell.model" })
@EnableJpaRepositories(basePackages = { "com.erealestate.houserent_sell.repository" })
public class OverRentals {
    public static void main(String[] args) {
        SpringApplication.run(OverRentals.class, args);
    }
}