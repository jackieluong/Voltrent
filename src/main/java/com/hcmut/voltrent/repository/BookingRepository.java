package com.hcmut.voltrent.repository;

import com.hcmut.voltrent.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
