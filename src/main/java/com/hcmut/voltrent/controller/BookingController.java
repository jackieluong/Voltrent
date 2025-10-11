package com.hcmut.voltrent.controller;

import com.hcmut.voltrent.dtos.RestResponse;
import com.hcmut.voltrent.dtos.UserDto;
import com.hcmut.voltrent.dtos.request.CreateBookingRequest;
import com.hcmut.voltrent.dtos.request.LoginRequest;
import com.hcmut.voltrent.dtos.response.CreateBookingResponse;
import com.hcmut.voltrent.dtos.response.LoginResponse;
import com.hcmut.voltrent.service.booking.IBookingService;
import com.hcmut.voltrent.utils.DateUtils;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
@Slf4j
public class BookingController {

    private final IBookingService bookingService;

    public BookingController(IBookingService bookingService) {
        this.bookingService = bookingService;
    }


    @PostMapping
    public ResponseEntity<?> createNewBooking(@Valid @RequestBody CreateBookingRequest request){


        String startTime = request.getStartTime();
        String endTime = request.getEndTime();
        try{
            request.setStartTime(DateUtils.convertToLocalDateTimeFormat(startTime));
            request.setEndTime(DateUtils.convertToLocalDateTimeFormat(endTime));
        } catch (Exception e) {
            log.error("Error converting {} and {} to {} format", request.getStartTime(), request.getEndTime(), DateUtils.DATE_FORMAT ,e);
        }

        CreateBookingResponse response = bookingService.createBooking(request);

        RestResponse restResponse = RestResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Create booking successfully")
                .data(response)
                .build();

        return ResponseEntity.ok(restResponse);

    }

}
