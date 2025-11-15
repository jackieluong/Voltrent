package com.hcmut.voltrent.service.qr;

import com.hcmut.voltrent.dtos.request.CreateQrRequest;
import com.hcmut.voltrent.dtos.response.CreateQrResponse;

public interface IQRService {

    CreateQrResponse generateQr(CreateQrRequest request);
}
