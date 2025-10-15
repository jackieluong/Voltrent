package com.hcmut.voltrent.service.booking;

import com.hcmut.voltrent.constant.PaymentGateway;
import com.hcmut.voltrent.dtos.request.CreateBookingRequest;
import com.hcmut.voltrent.dtos.response.CreateBookingResponse;
import com.hcmut.voltrent.dtos.response.RentedVehicleDto;

import java.util.List;

public interface IBookingService {

    CreateBookingResponse createBooking(CreateBookingRequest request);

    void markBookingAsPaid(Long bookingId, PaymentGateway paymentMethod);

    List<RentedVehicleDto> getRentedVehicles(String userId);
}
