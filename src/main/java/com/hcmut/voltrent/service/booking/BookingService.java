package com.hcmut.voltrent.service.booking;

import com.hcmut.voltrent.annotations.Caffeine;
import com.hcmut.voltrent.constant.*;
import com.hcmut.voltrent.dtos.model.BankInfo;
import com.hcmut.voltrent.dtos.model.CacheExpiredEvent;
import com.hcmut.voltrent.dtos.request.BaseTransactionRequest;
import com.hcmut.voltrent.dtos.request.ConfirmPaymentRequest;
import com.hcmut.voltrent.dtos.request.CreateBookingRequest;
import com.hcmut.voltrent.dtos.response.*;
import com.hcmut.voltrent.entity.BankAccount;
import com.hcmut.voltrent.entity.Booking;
import com.hcmut.voltrent.entity.Vehicle;
import com.hcmut.voltrent.exception.ConflictException;
import com.hcmut.voltrent.repository.BookingRepository;
import com.hcmut.voltrent.repository.TransactionRepository;
import com.hcmut.voltrent.repository.VehicleRepository;
import com.hcmut.voltrent.security.SecurityUtil;
import com.hcmut.voltrent.service.bank_account.IBankAccountService;
import com.hcmut.voltrent.service.cache.CacheExpirationListener;
import com.hcmut.voltrent.service.cache.CaffeineCacheService;
import com.hcmut.voltrent.service.cache.ICacheService;
import com.hcmut.voltrent.service.transaction.ITransactionService;
import com.hcmut.voltrent.utils.DateUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    private final IBankAccountService bankAccountService;
    private final ITransactionService transactionService;


    public BookingService(BookingRepository bookingRepository,
                          VehicleRepository vehicleRepository,
                          @Caffeine ICacheService cacheService,
                          IBankAccountService bankAccountService, ITransactionService transactionService) {
        this.bookingRepository = bookingRepository;
        this.vehicleRepository = vehicleRepository;
        this.cacheService = cacheService;
        this.bankAccountService = bankAccountService;
        this.transactionService = transactionService;
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

        Booking newBooking = Booking.builder()
                .userId(userId)
                .vehicleId(Long.valueOf(request.getVehicleId()))
                .startTime(LocalDate.parse(request.getStartTime()))
                .endTime(LocalDate.parse(request.getEndTime()))
                .status(BookingStatus.PENDING_PAYMENT.getValue())
                .totalAmount(request.getTotalAmount())
                .build();

        try {
            Booking saved = bookingRepository.save(newBooking);
            CreateBookingResponse response = CreateBookingResponse.builder()
                    .bookingId(String.valueOf(saved.getId()))
                    .vehicleId(String.valueOf(saved.getVehicleId()))
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

        Vehicle vehicle = vehicleRepository.findById(booking.getVehicleId())
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
                    Vehicle v = vehicleRepository.findById(b.getVehicleId())
                            .orElse(null);
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

    @Override
    public GetBookingQRInfo getQRInfo(String bookingId) {
        Booking booking = bookingRepository.findById(Long.valueOf(bookingId))
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        String userId = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new IllegalStateException("No logged in user"));

        BaseBankAccountResponse bankAccountResponse = bankAccountService.getBankAccount(userId);
        BankInfo bankInfo = new BankInfo(bankAccountResponse.getAccountName(), bankAccountResponse.getAccountNumber(),
                bankAccountResponse.getBankCode(), bankAccountResponse.getBankName());

        GetBookingQRInfo bookingQRInfo = new GetBookingQRInfo();
        bookingQRInfo.setBookingId(bookingId);
        bookingQRInfo.setAmount(booking.getTotalAmount());
        bookingQRInfo.setBankInfo(bankInfo);
        bookingQRInfo.setTransferContent("XM" + bookingId + " " + booking.getTotalAmount());
        bookingQRInfo.setTemplate("compact");
        bookingQRInfo.setNote("Vui lòng nhập chính xác nội dung chuyển khoản để được xác nhận nhanh nhất");
        return bookingQRInfo;
    }

    @Override
    public ConfirmTransferResponse confirmTransfer(String bookingId) {
        Booking booking = bookingRepository.findById(Long.valueOf(bookingId))
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getStatus().equals(BookingStatus.PENDING_PAYMENT.getValue())) {
            log.warn("Booking id {} is not in PENDING_PAYMENT status", bookingId);
            throw new ConflictException("Booking id " + bookingId + " is not in PENDING_PAYMENT status");
        }

        log.info("Confirm transfer for booking id {}, set status from {} to {}", bookingId, booking.getStatus(), BookingStatus.PENDING_CONFIRMATION.getValue());
        booking.setStatus(BookingStatus.PENDING_CONFIRMATION.getValue());
        try {
            Booking saved = bookingRepository.save(booking);
            return ConfirmTransferResponse.builder()
                    .bookingId(bookingId)
                    .message("Đang chờ chủ xe xác nhận tiền về")
                    .status(saved.getStatus())
                    .build();
        } catch (Exception e) {
            log.error("Error saving booking {}", booking, e);
            throw new RuntimeException("Error confirming transfer");
        }
    }

    @Override
    public ConfirmPaymentResponse confirmPayment(String bookingId, ConfirmPaymentRequest request) {
        Booking booking = bookingRepository.findById(Long.valueOf(bookingId))
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        TransferAction transferAction = TransferAction.fromString(request.getAction());
        if (transferAction == null) {
            log.warn("Transfer action {} is not supported", request.getAction());
            throw new RuntimeException("Transfer action " + request.getAction() + " is not supported");
        }

        if (transferAction.equals(TransferAction.CONFIRM)) {
            log.warn("Transfer action is CONFIRM, process to save new transaction");
            BaseTransactionRequest baseTransactionRequest = new BaseTransactionRequest();
            baseTransactionRequest.setBookingId(booking.getId());
            baseTransactionRequest.setTotalAmount(booking.getTotalAmount());
            BaseTransactionResponse transactionResponse = transactionService.createTransaction(baseTransactionRequest);
            log.info("Confirm payment for booking id {}, set status from {} to {}", bookingId, booking.getStatus(), BookingStatus.CONFIRMED.getValue());
            booking.setStatus(BookingStatus.CONFIRMED.getValue());
            return ConfirmPaymentResponse.builder()
                    .bookingId(bookingId)
                    .status(BookingStatus.CONFIRMED.getValue())
                    .paymentStatus(PaymentStatus.SUCCESS.getDescription())
                    .transactionId(transactionResponse.getTransactionId())
                    .updatedAt(LocalDateTime.now().toString())
                    .build();
        } else {
            log.warn("Transfer action is {}, not saving new transaction", request.getAction());
            log.info("Cancel payment for booking id {}, set status from {} to {}", bookingId, booking.getStatus(), BookingStatus.CANCELLED.getValue());
            booking.setStatus(BookingStatus.CANCELLED.getValue());
            return ConfirmPaymentResponse.builder()
                    .bookingId(bookingId)
                    .status(BookingStatus.CANCELLED.getValue())
                    .paymentStatus(PaymentStatus.PENDING.getDescription())
                    .transactionId("")
                    .updatedAt(LocalDateTime.now().toString())
                    .build();
        }
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
