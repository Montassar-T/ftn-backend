package com.ftn.backend.repository;

import com.ftn.backend.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {

    Optional<Reservation> findByIdAndDeletedAtIsNull(Long id);

    List<Reservation> findByPoolIdAndDeletedAtIsNull(Long poolId);

    List<Reservation> findByReserveeParAndDeletedAtIsNull(String email);

    // Conflict check: same pool, same date, overlapping time, same couloir (or full pool)
    @Query("""
        SELECT COUNT(r) > 0 FROM Reservation r
        WHERE r.pool.id = :poolId
        AND r.date = :date
        AND r.deletedAt IS NULL
        AND r.statut <> com.ftn.backend.enums.ReservationStatutEnum.ANNULEE
        AND (r.numeroCouloir = :numeroCouloir OR r.numeroCouloir IS NULL OR :numeroCouloir IS NULL)
        AND r.heureDebut < :heureFin
        AND r.heureFin > :heureDebut
    """)
    boolean existsConflict(
            @Param("poolId") Long poolId,
            @Param("date") LocalDate date,
            @Param("heureDebut") LocalTime heureDebut,
            @Param("heureFin") LocalTime heureFin,
            @Param("numeroCouloir") Integer numeroCouloir
    );
}