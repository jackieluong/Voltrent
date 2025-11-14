package com.hcmut.voltrent.repository;

import com.hcmut.voltrent.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByUserId(String userId);

    List<Booking> findByStatus(String status);
}
