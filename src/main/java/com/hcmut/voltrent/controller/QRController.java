package com.hcmut.voltrent.controller;

import com.hcmut.voltrent.dtos.model.RestResponse;

import com.hcmut.voltrent.dtos.request.CreateQrRequest;
import com.hcmut.voltrent.dtos.response.CreateQrResponse;
import com.hcmut.voltrent.service.qr.IQRService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/qr")
@Slf4j
public class QRController {

    private final IQRService qrService;

    public QRController(IQRService qrService) {
        this.qrService = qrService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateQR(@Valid @RequestBody CreateQrRequest request) {
        CreateQrResponse response = qrService.generateQr(request);
        return RestResponse.successResponse("Create QR successfully", response);
    }
}