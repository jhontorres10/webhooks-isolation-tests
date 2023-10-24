package com.example.demo.controller;

import com.example.demo.service.ProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Service
@RestController
@RequiredArgsConstructor
public class InboundController {
    private final ProviderService providerService;

    @GetMapping("/inbound")
    public String inbound() {
        providerService.callWebHook();
        return "webhookCalled";
    }
}
