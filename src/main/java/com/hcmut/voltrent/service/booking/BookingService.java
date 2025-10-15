package com.hcmut.voltrent.service.booking;

import com.hcmut.voltrent.constant.BookingStatus;
import com.hcmut.voltrent.constant.PaymentGateway;
import com.hcmut.voltrent.constant.VehicleStatus;
import com.hcmut.voltrent.dtos.request.CreateBookingRequest;
import com.hcmut.voltrent.dtos.response.CreateBookingResponse;
import com.hcmut.voltrent.dtos.response.RentedVehicleDto;
import com.hcmut.voltrent.entity.Booking;
import com.hcmut.voltrent.entity.Vehicle;
import com.hcmut.voltrent.exception.ConflictException;
import com.hcmut.voltrent.repository.BookingRepository;
import com.hcmut.voltrent.repository.VehicleRepository;
import com.hcmut.voltrent.security.JwtUtil;
import com.hcmut.voltrent.security.SecurityUtil;
import com.hcmut.voltrent.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookingService implements IBookingService {

    private final BookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;

    public BookingService(BookingRepository bookingRepository, VehicleRepository vehicleRepository) {
        this.bookingRepository = bookingRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public CreateBookingResponse createBooking(CreateBookingRequest request) {

        if (!isVehicleAvailable(request.getVehicleId(), request.getStartTime(), request.getEndTime())) {
            log.warn("Vehicle with id {} is not available from {} to {}", request.getVehicleId(),
                    request.getStartTime(), request.getEndTime());
            throw new ConflictException(
                    "Vehicle with id " + request.getVehicleId() + " is not available at the given time");
        }

        String userId = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new IllegalStateException("No logged in user"));

        Booking newBooking = Booking.builder()
                .userId(userId)
                .vehicleId(request.getVehicleId())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(BookingStatus.PENDING_PAYMENT.getValue())
                .totalAmount(request.getTotalAmount())
                .build();

        try {
            Booking saved = bookingRepository.save(newBooking);
            return CreateBookingResponse.builder()
                    .bookingId(String.valueOf(saved.getId()))
                    .vehicleId(String.valueOf(saved.getVehicleId()))
                    .totalAmount(request.getTotalAmount())
                    .status(BookingStatus.PENDING_PAYMENT.getValue())
                    .build();
        } catch (Exception e) {
            log.error("Error saving booking {}", newBooking, e);
            throw new RuntimeException("Error creating new booking");
        }

    }

    @Override
    public void markBookingAsPaid(Long bookingId, PaymentGateway paymentMethod) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(BookingStatus.CONFIRMED.getValue());
        booking.setPaymentCompletedTime(LocalDateTime.now());

        Vehicle vehicle = vehicleRepository.findById(Long.valueOf(booking.getVehicleId()))
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        vehicle.setStatus(VehicleStatus.RENTED);

        try {
            bookingRepository.save(booking);
            vehicleRepository.save(vehicle);
            log.info("Saved booking {}", booking);
        } catch (Exception e) {
            log.error("Error updating booking {}", booking, e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<RentedVehicleDto> getRentedVehicles(String userId) {
        List<Booking> bookings = bookingRepository.findAllByUserId(userId);
        // Lấy thông tin xe và trạng thái booking cho từng booking
        return bookings.stream()
                .map(b -> {
                    Vehicle v = vehicleRepository.findById(Long.valueOf(b.getVehicleId()))
                            .orElse(null);
                    return RentedVehicleDto.builder()
                            .vehicle(v)
                            .bookingStatus(b.getStatus())
                            .bookingId(b.getId())
                            .startTime(b.getStartTime())
                            .endTime(b.getEndTime())
                            .build();
                })
                .filter(dto -> dto.getVehicle() != null)
                .collect(Collectors.toList());
    }

    private boolean isVehicleAvailable(String vehicleId, String startTime, String endTime) {
        return true;
    }
}
