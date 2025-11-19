package com.hcmut.voltrent.service.qr;

import com.hcmut.voltrent.annotations.S3;
import com.hcmut.voltrent.annotations.S3Async;
import com.hcmut.voltrent.config.VietQRConfig;
import com.hcmut.voltrent.dtos.request.CreateQrRequest;
import com.hcmut.voltrent.dtos.request.VietQRRequest;
import com.hcmut.voltrent.dtos.response.CreateQrResponse;
import com.hcmut.voltrent.dtos.response.VietQRResponse;
import com.hcmut.voltrent.entity.CompanyPaymentInfo;
import com.hcmut.voltrent.lib.client.ReactiveHttpClient;
import com.hcmut.voltrent.lib.client.HttpRequest;
import com.hcmut.voltrent.repository.CompanyPaymentInfoRepository;
import com.hcmut.voltrent.security.SecurityUtil;
import com.hcmut.voltrent.service.file.IFileService;
import com.hcmut.voltrent.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class VietQRService implements IQRService {

    private final ReactiveHttpClient reactiveHttpClient;
    private final VietQRConfig vietQRConfig;
    private final IFileService<Mono<String>> fileService;
    private final CompanyPaymentInfoRepository companyInfoRepository;

    public VietQRService(ReactiveHttpClient reactiveHttpClient,
                         VietQRConfig vietQRConfig,
                         @S3Async IFileService fileService,
                         CompanyPaymentInfoRepository companyInfoRepository) {
        this.reactiveHttpClient = reactiveHttpClient;
        this.vietQRConfig = vietQRConfig;
        this.fileService = fileService;
        this.companyInfoRepository = companyInfoRepository;
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

        CreateQrResponse res = reactiveHttpClient.execute(httpRequest, VietQRResponse.class)
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

        uploadQRToS3(res, request);
        return res;
    }

    private void uploadQRToS3(CreateQrResponse res, CreateQrRequest request) {
        // Run S3 upload on different thread (async)
        SecurityContext securityContext = SecurityContextHolder.getContext();
        byte[] fileBytes = FileUtils.decodeQRBase64(res.getQrDataUrl());
        fileService.upload(fileBytes, request.getAccountNumber() + ".png", "qr")
                .doOnSuccess(url -> log.info("QR uploaded to S3 successfully: {}", url))
                .doOnSuccess(url -> {
                    SecurityContextHolder.setContext(securityContext);
                    CompanyPaymentInfo companyPaymentInfo = buildCompanyPaymentInfo(request);
                    companyPaymentInfo.setPaymentQRUrl(url);
                    companyInfoRepository.save(companyPaymentInfo);
                    log.info("Saving QR Url to database successfully");
                    SecurityContextHolder.clearContext();
                })
                .doOnError(err -> log.error("Failed to upload QR to S3 and saving payment QRCode Url", err))
                .subscribe();  // triggers execution
    }

    private CompanyPaymentInfo buildCompanyPaymentInfo(CreateQrRequest request) {
        CompanyPaymentInfo companyPaymentInfo = new CompanyPaymentInfo();
        companyPaymentInfo.setCompanyId(SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new IllegalStateException("No logged in user")));
        companyPaymentInfo.setAccountNumber(request.getAccountNumber());
        companyPaymentInfo.setAccountName(request.getAccountName());
        companyPaymentInfo.setBankCode(request.getBankCode());
        return companyPaymentInfo;
    }
}
