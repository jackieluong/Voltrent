package com.hcmut.voltrent.service.booking;

import com.hcmut.voltrent.constant.BookingStatus;
import com.hcmut.voltrent.dtos.request.CreateBookingRequest;
import com.hcmut.voltrent.dtos.response.CreateBookingResponse;
import com.hcmut.voltrent.entity.Booking;
import com.hcmut.voltrent.exception.ConflictException;
import com.hcmut.voltrent.repository.BookingRepository;
import com.hcmut.voltrent.security.JwtUtil;
import com.hcmut.voltrent.security.SecurityUtil;
import com.hcmut.voltrent.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BookingService implements IBookingService{

    private final BookingRepository bookingRepository;


    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public CreateBookingResponse createBooking(CreateBookingRequest request) {

        if(!isVehicleAvailable(request.getVehicleId(), request.getStartTime(), request.getEndTime())){
            log.warn("Vehicle with id {} is not available from {} to {}", request.getVehicleId(), request.getStartTime(), request.getEndTime());
            throw new ConflictException("Vehicle with id " + request.getVehicleId() + " is not available at the given time");
        }

        String userId = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new IllegalStateException("No logged in user"));

        Booking newBooking = Booking.builder()
                .userId(userId)
                .vehicleId(request.getVehicleId())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        try{
            Booking saved = bookingRepository.save(newBooking);
            return CreateBookingResponse.builder()
                    .bookingId(String.valueOf(saved.getId()))
                    .vehicleId(String.valueOf(saved.getVehicleId()))
                    .totalAmount(10000L)
                    .status(BookingStatus.PENDING_PAYMENT.getValue())
                    .build();
        } catch (Exception e) {
            log.error("Error saving booking {}", newBooking, e);
            throw new RuntimeException("Error creating new booking");
        }

    }

    private boolean isVehicleAvailable(String vehicleId, String startTime, String endTime) {
        return true;
    }
}
