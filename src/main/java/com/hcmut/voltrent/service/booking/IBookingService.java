package com.hcmut.voltrent.service.booking;

import com.hcmut.voltrent.dtos.request.CreateBookingRequest;
import com.hcmut.voltrent.dtos.response.CreateBookingResponse;

public interface IBookingService {

    CreateBookingResponse createBooking(CreateBookingRequest request);
}
