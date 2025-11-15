package com.hcmut.voltrent.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class VietQRConfig {

    private final String baseUrl;

    private final String apiKey;

    private final String clientId;

    private final String generateQREndpoint;

    public VietQRConfig(@Value("${payment.vietqr.base-url}") String baseUrl,
                        @Value("${payment.vietqr.api-key}") String apiKey,
                        @Value("${payment.vietqr.client-id}") String clientId,
                        @Value("${payment.vietqr.api.generate-qr}") String generateQREndpoint) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.clientId = clientId;
        this.generateQREndpoint = baseUrl + generateQREndpoint;
    }

}
