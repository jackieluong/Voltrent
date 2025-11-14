package com.hcmut.voltrent.controller;

import com.hcmut.voltrent.dtos.request.AddVehicleDTO;
import com.hcmut.voltrent.dtos.request.UpdateVehicleDTO;
import com.hcmut.voltrent.entity.Vehicle;
import com.hcmut.voltrent.service.booking.IBookingService;
import com.hcmut.voltrent.service.vehicle.IVehicleService;
import com.hcmut.voltrent.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/vehicles")
@Tag(name = "Vehicles", description = "Quản lý và tìm kiếm phương tiện")
public class VehicleController {

    private final IVehicleService vehicleService;
    private final IBookingService bookingService;
    private final ModelMapper modelMapper;

    public VehicleController(IVehicleService vehicleService, IBookingService bookingService, ModelMapper modelMapper) {
        this.vehicleService = vehicleService;
        this.bookingService = bookingService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('COMPANY')")
    @Operation(summary = "Thêm phương tiện mới", description = "Chỉ COMPANY mới có quyền thêm phương tiện.")
    @ApiResponse(responseCode = "201", description = "Phương tiện được tạo thành công", content = @Content(schema = @Schema(implementation = Vehicle.class)))
    @ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ", content = @Content)
    @ApiResponse(responseCode = "401", description = "Không được phép", content = @Content)
    @ApiResponse(responseCode = "409", description = "Xung đột dữ liệu", content = @Content)
    public ResponseEntity<?> addVehicle(
            @Valid @RequestBody AddVehicleDTO request,
            Authentication auth) {
        try {
            UUID userId = extractUserId(auth);
            String userEmail = extractUserEmail(auth);
            Vehicle vehicle = new Vehicle();
            vehicle.setOwnerId(String.valueOf(userId));
            vehicle.setOwnerEmail(userEmail);
            vehicle.setName(request.getName());
            vehicle.setType(request.getType());
            vehicle.setPricePerHour(request.getPricePerHour());
            vehicle.setImageUrl(request.getImageUrl());

            Vehicle savedVehicle = vehicleService.save(vehicle);
            return buildSuccessResponse(HttpStatus.CREATED, "Xe đã được tạo thành công", savedVehicle);
        } catch (Exception e) {
            System.out.println("DEBUG: Error = " + e.getMessage());
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
    }

    @GetMapping("/rented")
    @PreAuthorize("hasAnyRole('COMPANY', 'USER')")
    @Operation(summary = "Lấy danh sách các xe đã thuê bởi người dùng hiện tại", description = "Người dùng có thể xem các xe mình đã thuê.")
    public ResponseEntity<?> getRentedVehicles(Authentication auth) {
        String userId = extractUserId(auth).toString();
        var rentedVehicles = bookingService.getRentedVehicles(userId);
        String message = rentedVehicles.isEmpty() ? "Bạn chưa thuê xe nào." : "Danh sách xe bạn đã thuê";
        return buildSuccessResponse(HttpStatus.OK, message, rentedVehicles);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('COMPANY')")
    @Operation(summary = "Lấy danh sách phương tiện của người dùng", description = "Yêu cầu phải đăng nhập.")
    public ResponseEntity<?> getMyVehicles(Authentication auth) {
        UUID userId = extractUserId(auth);
        List<Vehicle> vehicles = vehicleService.getMyVehicles(userId.toString());
        String message = vehicles.isEmpty() ? "Không có phương tiện nào được đăng ký" : "Danh sách phương tiện của bạn";
        return buildSuccessResponse(HttpStatus.OK, message, vehicles);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COMPANY')")
    @Operation(summary = "Cập nhật thông tin phương tiện", description = "Chỉ COMPANY và chủ sở hữu phương tiện có quyền cập nhật.")
    public ResponseEntity<?> updateVehicle(@PathVariable Long id, @Valid @RequestBody UpdateVehicleDTO request,
            Authentication auth) {
        UUID userId = extractUserId(auth);
        Vehicle updated = vehicleService.updateVehicle(id, request, userId.toString());
        return buildSuccessResponse(HttpStatus.OK, "Cập nhật phương tiện thành công", updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('COMPANY')")
    @Operation(summary = "Xóa phương tiện", description = "Chỉ COMPANY và chủ sở hữu phương tiện có quyền xóa.")
    public ResponseEntity<?> deleteVehicle(@PathVariable Long id, Authentication auth) {
        UUID userId = extractUserId(auth);
        vehicleService.deleteVehicle(id, userId.toString());
        return buildSuccessResponse(HttpStatus.OK, "Xóa phương tiện thành công", Collections.emptyMap());
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm phương tiện khả dụng", description = "Tìm xe theo loại, giá tối thiểu và tối đa, vị trí, bán kính, thời gian.")
    public ResponseEntity<?> searchVehicles(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Double priceMin,
            @RequestParam(required = false) Double priceMax,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false) Double radius,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end) {
        List<Vehicle> vehicles = vehicleService.searchVehicles(type, priceMin, priceMax, lat, lng, radius, start, end);
        String message = vehicles.isEmpty() ? "Không tìm thấy xe phù hợp" : "Danh sách xe tìm thấy";
        return buildSuccessResponse(HttpStatus.OK, message, vehicles);
    }

    @PutMapping("/{id}/pause")
    @PreAuthorize("hasRole('COMPANY')")
    @Operation(summary = "Tạm dừng xe", description = "Chỉ chủ sở hữu xe có thể tạm dừng.")
    public ResponseEntity<?> pauseVehicle(@PathVariable Long id, Authentication auth) {
        UUID userId = extractUserId(auth);
        Vehicle vehicle = vehicleService.pauseVehicle(id, userId.toString());
        return buildSuccessResponse(HttpStatus.OK, "Tạm dừng xe thành công", vehicle);
    }

    @PutMapping("/{id}/resume")
    @PreAuthorize("hasRole('COMPANY')")
    @Operation(summary = "Tiếp tục xe", description = "Chỉ chủ sở hữu xe có thể tiếp tục.")
    public ResponseEntity<?> resumeVehicle(@PathVariable Long id, Authentication auth) {
        UUID userId = extractUserId(auth);
        Vehicle vehicle = vehicleService.resumeVehicle(id, userId.toString());
        return buildSuccessResponse(HttpStatus.OK, "Tiếp tục xe thành công", vehicle);
    }

    // ======= EXCEPTION HANDLERS =======
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT,
                "Lỗi xung đột dữ liệu: " + ex.getMostSpecificCause().getMessage(), Collections.emptyMap());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", errors);
    }

    // ======= PRIVATE HELPERS =======
    private UUID extractUserId(Authentication auth) {
        String subject = null;
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof Map) {
            Map<String, String> principal = (Map<String, String>) auth.getPrincipal();
            subject = principal.get("userId");
        } else if (auth != null && auth.isAuthenticated() && auth.getPrincipal() != null) {
            subject = auth.getName();
        } else {
            subject = SecurityUtil.getCurrentUserLogin().orElse(null);
        }
        if (subject == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }
        try {
            return UUID.fromString(subject);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user ID format in token");
        }
    }

    private String extractUserEmail(Authentication auth) {
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof Map) {
            Map<String, String> principal = (Map<String, String>) auth.getPrincipal();
            return principal.get("email");
        }
        return null;
    }

    private ResponseEntity<Map<String, Object>> buildSuccessResponse(HttpStatus status, String message, Object data) {
        return ResponseEntity.status(status).body(Map.of(
                "status", "success",
                "message", message,
                "data", data));
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message, Object data) {
        return ResponseEntity.status(status).body(Map.of(
                "status", "error",
                "message", message,
                "data", data == null ? Collections.emptyMap() : data));
    }
}