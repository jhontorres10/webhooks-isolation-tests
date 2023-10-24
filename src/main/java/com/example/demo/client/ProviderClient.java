package com.example.demo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "provider", url = "${faker.webhook}")
public interface ProviderClient {
    @GetMapping(value = "/provider")
    void getWeekHook();
}