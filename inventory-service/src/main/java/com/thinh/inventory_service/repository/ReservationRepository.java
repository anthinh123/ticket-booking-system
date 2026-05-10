package com.thinh.inventory_service.repository;

import com.thinh.inventory_service.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE Reservation r SET r.status = :status " +
            "WHERE r.seatId IN :seatIds AND r.status = 'ACTIVE'")
    int updateStatusBySeatIds(@Param("seatIds") List<Long> seatIds, @Param("status") String status);
}
