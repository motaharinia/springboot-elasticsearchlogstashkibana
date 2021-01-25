package com.motaharinia.elasticsarchlogstashkibana.member.presentation;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;


@RestController
@Slf4j
@RequestMapping(value = "/member")
public class MemberController {

    @GetMapping(value = "/info")
    public String info() {
        String response = "Welcome to JavaInUse" + new Date();
        log.info(response);
        return response;
    }

    @GetMapping(value = "/error")
    public String error() throws Exception {
        String response = "";
        if (response.isEmpty()) {
            throw new Exception("Exception has occured....");
        }
        return response;
    }

}
