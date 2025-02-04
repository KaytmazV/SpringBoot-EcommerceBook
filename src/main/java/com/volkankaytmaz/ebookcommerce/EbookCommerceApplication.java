package com.volkankaytmaz.ebookcommerce;

import com.volkankaytmaz.ebookcommerce.service.CSVLoaderService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EbookCommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbookCommerceApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(CSVLoaderService csvLoaderService) {
        return args -> {
            System.out.println("Veritabanının doldurulması gerekip gerekmediği kontrol ediliyor...");
            csvLoaderService.loadCSVIfDatabaseEmpty();
            System.out.println("Veritabanı kontrolü ve doldurma işlemi tamamlandı.");
        };
    }
}

