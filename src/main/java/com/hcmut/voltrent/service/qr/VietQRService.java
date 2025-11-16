package com.hcmut.voltrent.service.qr;

import com.hcmut.voltrent.config.VietQRConfig;
import com.hcmut.voltrent.dtos.request.CreateQrRequest;
import com.hcmut.voltrent.dtos.request.VietQRRequest;
import com.hcmut.voltrent.dtos.response.CreateQrResponse;
import com.hcmut.voltrent.dtos.response.VietQRResponse;
import com.hcmut.voltrent.lib.client.ReactiveHttpClient;
import com.hcmut.voltrent.lib.client.HttpRequest;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class VietQRService implements IQRService {

    private final ReactiveHttpClient reactiveHttpClient;
    private final VietQRConfig vietQRConfig;

    public VietQRService(ReactiveHttpClient reactiveHttpClient, VietQRConfig vietQRConfig) {
        this.reactiveHttpClient = reactiveHttpClient;
        this.vietQRConfig = vietQRConfig;
    }

    @Override
    public CreateQrResponse generateQr(CreateQrRequest request) {
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setUrl(vietQRConfig.getGenerateQREndpoint());
        httpRequest.setHttpMethod(HttpMethod.POST);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("x-client-id", vietQRConfig.getClientId());
        headers.put("x-api-key", vietQRConfig.getApiKey());
        httpRequest.setHeaders(headers);

        VietQRRequest requestBody = VietQRRequest.builder()
                .accountNo(request.getAccountNumber())
                .accountName(request.getAccountName())
                .acqId(request.getBankCode())
                .format("text")
                .template("compact")
                .build();
        httpRequest.setBody(requestBody);

        return reactiveHttpClient.execute(httpRequest, VietQRResponse.class)
                .map(response -> {
                    if (Objects.equals(response.getCode(), "00")) {
                        CreateQrResponse createQrResponse = new CreateQrResponse();
                        createQrResponse.setQrDataUrl(response.getData().getQrDataURL());
                        createQrResponse.setAccountName(request.getAccountName());
                        createQrResponse.setAccountNumber(request.getAccountNumber());
                        return createQrResponse;
                    } else {
                        throw new RuntimeException("Failed to generate VietQR code: " + response.getCode() + " - " + response.getDesc());
                    }

                })
                .block(); // Blocking call
    }
}
