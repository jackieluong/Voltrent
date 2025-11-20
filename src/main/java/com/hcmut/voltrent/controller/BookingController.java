package com.hcmut.voltrent.controller;

import com.hcmut.voltrent.dtos.model.RestResponse;
import com.hcmut.voltrent.dtos.request.ConfirmPaymentRequest;
import com.hcmut.voltrent.dtos.request.CreateBookingRequest;
import com.hcmut.voltrent.dtos.response.*;
import com.hcmut.voltrent.exception.ErrorDetails;
import com.hcmut.voltrent.service.booking.IBookingService;
import com.hcmut.voltrent.utils.DateUtils;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        BaseBookingResponse response = bookingService.createBooking(request);
        return RestResponse.successResponse("Create booking successfully", response);
    }

    @GetMapping("/{id}/qr-info")
    public ResponseEntity<?> getBookingQRInfo(@PathVariable(name = "id") String bookingId) {
        GetBookingQRInfo response = bookingService.getQRInfo(bookingId);
        return RestResponse.successResponse("Get booking QR info successfully", response);
    }

    @PostMapping("/{id}/confirm-transfer")
    public ResponseEntity<?> confirmBookingTransfer(@PathVariable(name = "id") String bookingId) {
        ConfirmTransferResponse response = bookingService.confirmTransfer(bookingId);

        return RestResponse.successResponse("Confirm transfer successfully", response);
    }

    @PostMapping("/{id}/confirm-payment")
    public ResponseEntity<?> confirmPaymentTransfer(@PathVariable(name = "id") String bookingId,
                                                    @Valid @RequestBody ConfirmPaymentRequest request) {
        ConfirmPaymentResponse response = bookingService.confirmPayment(bookingId, request);
        return RestResponse.successResponse("Confirm payment successfully", response);
    }

    @GetMapping("/company")
    public ResponseEntity<?> getCompanyBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        PagedResponse<BookingDetailResponse> response =
                bookingService.getCompanyBookings(page, size, sortBy, sortDirection);

        return RestResponse.successResponse( "Get company bookings successfully", response);
    }
}
