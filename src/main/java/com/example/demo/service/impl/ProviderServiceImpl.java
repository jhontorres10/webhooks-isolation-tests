package com.example.demo.service.impl;

import com.example.demo.client.ProviderClient;
import com.example.demo.service.ProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProviderServiceImpl implements ProviderService {
    private final ProviderClient providerClient;

    @Override
    public void callWebHook() {
        providerClient.getWeekHook();
    }
}
