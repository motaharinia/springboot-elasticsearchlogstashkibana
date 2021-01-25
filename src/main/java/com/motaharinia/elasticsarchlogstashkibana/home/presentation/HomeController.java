package com.motaharinia.elasticsarchlogstashkibana.home.presentation;



import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
public class HomeController {

    @Value("${spring.application.name}")
    private String springApplicationName;


    @GetMapping("/")
    public String getUrl() {
        return springApplicationName;
    }

}
