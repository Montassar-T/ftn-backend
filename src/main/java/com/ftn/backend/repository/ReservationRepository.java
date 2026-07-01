package com.ftn.backend.repository;

import com.ftn.backend.model.Reservation;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {

    Optional<Reservation> findByIdAndDeletedAtIsNull(Long id);

    List<Reservation> findByPoolIdAndDeletedAtIsNull(Long poolId);

    List<Reservation> findByReserveeParAndDeletedAtIsNull(String email);

    // Returns all non-cancelled reservations for a pool on a given date with overlapping time,
    // so the service layer can check lane conflicts in Java (lanes stored as comma-separated strings).
    @Query(
            """
		SELECT r FROM Reservation r
		WHERE r.pool.id = :poolId
		AND r.date = :date
		AND r.deletedAt IS NULL
		AND r.statut <> com.ftn.backend.enums.ReservationStatutEnum.ANNULEE
		AND r.heureDebut < :heureFin
		AND r.heureFin > :heureDebut
	""")
    List<Reservation> findOverlapping(
            @Param("poolId") Long poolId,
            @Param("date") LocalDate date,
            @Param("heureDebut") LocalTime heureDebut,
            @Param("heureFin") LocalTime heureFin);
	@Query("""
    SELECT COUNT(r) FROM Reservation r
    WHERE r.reserveePar = :email
    AND r.statut <> com.ftn.backend.enums.ReservationStatutEnum.EN_ATTENTE
    AND r.seenByUser = false
    AND r.deletedAt IS NULL
""")
	long countUnseenForUser(@Param("email") String email);

	@Query("""
    SELECT COUNT(r) FROM Reservation r
    WHERE r.statut = com.ftn.backend.enums.ReservationStatutEnum.EN_ATTENTE
    AND r.deletedAt IS NULL
""")
	long countPending();

	@Modifying
	@Query("""
    UPDATE Reservation r SET r.seenByUser = true
    WHERE r.reserveePar = :email
    AND r.statut <> com.ftn.backend.enums.ReservationStatutEnum.EN_ATTENTE
    AND r.deletedAt IS NULL
""")
	int markSeenForUser(@Param("email") String email);
}
