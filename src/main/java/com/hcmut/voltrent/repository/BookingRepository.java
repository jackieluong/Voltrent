package com.hcmut.voltrent.repository;

import com.hcmut.voltrent.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByUserId(String userId);

    List<Booking> findByStatus(String status);

    List<Booking> findByVehicleId(Long vehicleId);

    @Query("SELECT b.vehicle.id FROM Booking b WHERE b.startTime < :endTime AND b.endTime > :startTime AND b.status = 'CONFIRMED'")
    List<Long> findBookedVehicleIds(@Param("startTime") Date startTime, @Param("endTime") Date endTime);


    @EntityGraph(attributePaths = {"vehicle", "user"})
    @Query("""
                SELECT b FROM Booking b
                JOIN b.vehicle v
                WHERE v.ownerId = :companyId
            """)
    Page<Booking> findAllByCompanyId(@Param("companyId") String companyId, Pageable pageable);


}
