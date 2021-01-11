package com.motaharinia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.motaharinia"})
public class ElasticsearchLogstashKibanaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElasticsearchLogstashKibanaApplication.class, args);
    }

}
