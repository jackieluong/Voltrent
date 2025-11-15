package com.hcmut.voltrent.service.qr;

import com.hcmut.voltrent.config.VietQRConfig;
import com.hcmut.voltrent.dtos.request.CreateQrRequest;
import com.hcmut.voltrent.dtos.request.VietQRRequest;
import com.hcmut.voltrent.dtos.response.CreateQrResponse;
import com.hcmut.voltrent.dtos.response.VietQRResponse;
import com.hcmut.voltrent.lib.client.AsyncHttpClient;
import com.hcmut.voltrent.lib.client.HttpRequest;
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class VietQRService implements IQRService {

    private final AsyncHttpClient asyncHttpClient;
    private final VietQRConfig vietQRConfig;

    public VietQRService(AsyncHttpClient asyncHttpClient, VietQRConfig vietQRConfig) {
        this.asyncHttpClient = asyncHttpClient;
        this.vietQRConfig = vietQRConfig;
    }

    @Override
    public CreateQrResponse generateQr(CreateQrRequest request) {
        HttpRequest httpRequest = new HttpRequest();

        Map<String, String> headers = new HashMap<>();
        VietQRRequest requestBody = new VietQRRequest();
        headers.put("Content-Type", "application/json");
        headers.put("x-client-id", vietQRConfig.getClientId());
        headers.put("x-api-key", vietQRConfig.getApiKey());
        requestBody.setAccountNo(request.getAccountNumber());
        requestBody.setAccountName(request.getAccountName());
        requestBody.setAcqId(request.getBankCode());

        httpRequest.setBody(requestBody);
        httpRequest.setHeaders(headers);
        httpRequest.setHttpMethod(HttpMethod.POST);

        asyncHttpClient.executeAsync(httpRequest, VietQRResponse.class, response -> {
            if (response.isSuccess()) {
                CreateQrResponse createQrResponse = new CreateQrResponse();
                createQrResponse.setQrUrl(response.getData().getQrUrl());
                return createQrResponse;
            }
            return null;
        });

    }

}
