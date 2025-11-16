package com.hcmut.voltrent.service.booking;

import com.hcmut.voltrent.annotations.Caffeine;
import com.hcmut.voltrent.constant.BookingStatus;
import com.hcmut.voltrent.constant.CacheKey;
import com.hcmut.voltrent.constant.PaymentGateway;
import com.hcmut.voltrent.constant.VehicleStatus;
import com.hcmut.voltrent.dtos.model.CacheExpiredEvent;
import com.hcmut.voltrent.dtos.request.CreateBookingRequest;
import com.hcmut.voltrent.dtos.response.CreateBookingResponse;
import com.hcmut.voltrent.dtos.response.RentedVehicleDto;
import com.hcmut.voltrent.entity.Booking;
import com.hcmut.voltrent.entity.Vehicle;
import com.hcmut.voltrent.exception.ConflictException;
import com.hcmut.voltrent.repository.BookingRepository;
import com.hcmut.voltrent.repository.VehicleRepository;
import com.hcmut.voltrent.security.SecurityUtil;
import com.hcmut.voltrent.service.cache.CacheExpirationListener;
import com.hcmut.voltrent.service.cache.CaffeineCacheService;
import com.hcmut.voltrent.service.cache.ICacheService;
import com.hcmut.voltrent.utils.DateUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.hcmut.voltrent.constant.CacheKey.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookingService implements IBookingService, CacheExpirationListener<Booking> {

    @Value("${cache.booking.expiration}")
    private long bookingExpireSeconds;

    private final BookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;
    private final ICacheService cacheService;


    public BookingService(BookingRepository bookingRepository,
                          VehicleRepository vehicleRepository,
                          @Caffeine ICacheService cacheService) {
        this.bookingRepository = bookingRepository;
        this.vehicleRepository = vehicleRepository;
        this.cacheService = cacheService;
    }

    @PostConstruct
    public void init() {
        if (cacheService instanceof CaffeineCacheService caffeineCacheService) {
            caffeineCacheService.addListener(this);
        }
    }

    @Override
    public Set<String> patterns() {
        return Set.of(CacheKey.Booking.CACHE_KEY_REGEX);
    }

    @Override
    public void onCacheExpired(CacheExpiredEvent<Booking> event) {
        Booking booking = event.getValue();

        String originalStatus = booking.getStatus();
        if (BookingStatus.PENDING_PAYMENT.getValue().equals(originalStatus)) {
            booking.setStatus(BookingStatus.CANCELLED.getValue());
        }

        try {
            bookingRepository.save(booking);
            log.info("Update booking id {}, status from {} to {}", booking.getId(), originalStatus, booking.getStatus());
        } catch (Exception e) {
            log.error("Error saving booking {}", booking, e);
            throw new RuntimeException("Error updating booking status");
        }
    }

    @Override
    public CreateBookingResponse createBooking(CreateBookingRequest request) {

        if (!isVehicleAvailableInTimeRange(request.getVehicleId(), request.getStartTime(), request.getEndTime())) {
            log.warn("Vehicle with id {} is not available from {} to {}", request.getVehicleId(),
                    request.getStartTime(), request.getEndTime());
            throw new ConflictException("Vehicle with id " + request.getVehicleId() +
                    " is not available at the given time from " + request.getStartTime() + " to " + request.getEndTime());
        }

        String userId = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new IllegalStateException("No logged in user"));

        Vehicle vehicle = vehicleRepository.findById(Long.valueOf(request.getVehicleId()))
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        Booking newBooking = Booking.builder()
                .userId(userId)
                .vehicle(vehicle)
                .startTime(LocalDate.parse(request.getStartTime()))
                .endTime(LocalDate.parse(request.getEndTime()))
                .status(BookingStatus.PENDING_PAYMENT.getValue())
                .totalAmount(request.getTotalAmount())
                .build();

        try {
            Booking saved = bookingRepository.save(newBooking);
            CreateBookingResponse response = CreateBookingResponse.builder()
                    .bookingId(String.valueOf(saved.getId()))
                    .vehicleId(String.valueOf(saved.getVehicle().getId()))
                    .totalAmount(request.getTotalAmount())
                    .status(BookingStatus.PENDING_PAYMENT.getValue())
                    .build();

            // Save to cache
            cacheService.put(String.format(CacheKey.Booking.BOOKING_ID, saved.getId()), saved, bookingExpireSeconds);

            return response;
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

        Vehicle vehicle = vehicleRepository.findById(booking.getVehicle().getId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        vehicle.setPaused(false);

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
                    Vehicle v = b.getVehicle();
                    return RentedVehicleDto.builder()
                            .vehicle(v)
                            .bookingStatus(b.getStatus())
                            .bookingId(b.getId())
                            .startTime(String.valueOf(b.getStartTime()))
                            .endTime(String.valueOf(b.getEndTime()))
                            .build();
                })
                .filter(dto -> dto.getVehicle() != null)
                .collect(Collectors.toList());
    }

    private boolean isVehicleAvailableInTimeRange(String vehicleId, String startTime, String endTime) {
        List<Booking> bookings = bookingRepository.findByVehicleId(Long.valueOf(vehicleId));

        LocalDate bookingStartTime = DateUtils.convertToLocalDateFormat(startTime);
        LocalDate bookingEndTime = DateUtils.convertToLocalDateFormat(endTime);
        boolean existOverlap = bookings.stream().anyMatch(booking ->
                !bookingStartTime.isAfter(booking.getEndTime())
                        && !bookingEndTime.isBefore(booking.getStartTime())
                        && !BookingStatus.CANCELLED.getValue().equals(booking.getStatus())
        );
        return !existOverlap;
    }
}
