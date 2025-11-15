package com.hcmut.voltrent.controller;

import com.hcmut.voltrent.dtos.model.RestResponse;
import com.hcmut.voltrent.dtos.request.CreateBookingRequest;
import com.hcmut.voltrent.dtos.response.CreateBookingResponse;
import com.hcmut.voltrent.exception.ErrorDetails;
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

import java.util.Date;

@RestController
@RequestMapping("/api/bookings")
@Slf4j
public class BookingController {

    private final IBookingService bookingService;

    public BookingController(IBookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<?> createNewBooking(@Valid @RequestBody CreateBookingRequest request) {
        String startTime = request.getStartTime();
        String endTime = request.getEndTime();
        try {
            if (DateUtils.isDateBeforeNow(startTime) || DateUtils.isTimeRangeInvalid(startTime, endTime)) {
                log.error("Invalid Time Range: {} and {}", startTime, endTime);
                return ResponseEntity.badRequest().body(
                        new ErrorDetails(new Date(), "Invalid Time Range", ""));
            }
            request.setStartTime(String.valueOf(DateUtils.convertToLocalDateFormatWithEx(startTime)));
            request.setEndTime(String.valueOf(DateUtils.convertToLocalDateFormatWithEx(endTime)));
        } catch (Exception e) {
            log.error("Error converting {} and {} to {} format", request.getStartTime(), request.getEndTime(), DateUtils.DATE_FORMAT, e);
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
