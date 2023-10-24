package com.example.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
public class OutboundController {

    @PostMapping("/outbound")
    public String outbound(@RequestBody Map<String, Object> body) {
        log.info("I was called");
        return body.toString();
    }

}
