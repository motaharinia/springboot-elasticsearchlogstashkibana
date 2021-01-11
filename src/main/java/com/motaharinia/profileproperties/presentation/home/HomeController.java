package com.motaharinia.profileproperties.presentation.home;



import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Level;


@RestController
@Slf4j
public class HomeController {

    @Value("${spring.application.name}")
    private String springApplicationName;

//    @Autowired
//    RestTemplate restTemplete;
//
//    @Bean
//    RestTemplate restTemplate() {
//        return new RestTemplate();
//    }

    @RequestMapping("/")
    public String getUrl() {
        return springApplicationName;
    }


    @RequestMapping(value = "/elk")
    public String helloWorld() {
        String response = "Welcome to JavaInUse" + new Date();
        log.info(response);

        return response;
    }

    @RequestMapping(value = "/exception")
    public String exception() {
        String response = "";
        try {
            throw new Exception("Exception has occured....");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(String.valueOf(e));

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stackTrace = sw.toString();
            log.error("Exception - " + stackTrace);
            response = stackTrace;
        }

        return response;
    }

}
