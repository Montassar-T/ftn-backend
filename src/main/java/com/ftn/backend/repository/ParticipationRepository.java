package com.ftn.backend.repository;

import com.ftn.backend.enums.ParticipationStatusEnum;
import com.ftn.backend.model.Participation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    Optional<Participation> findByIdAndDeletedAtIsNull(Long id);

    List<Participation> findByEvenement_IdAndDeletedAtIsNullOrderByCreatedAtAsc(Long evenementId);

    List<Participation> findByEvenement_IdAndDeletedAtIsNull(Long evenementId);

    List<Participation> findByUser_IdAndDeletedAtIsNull(Long userId);

    boolean existsByEvenement_IdAndUser_IdAndDeletedAtIsNull(Long evenementId, Long userId);

    long countByEvenement_IdAndStatusAndDeletedAtIsNull(Long evenementId, ParticipationStatusEnum status);

    Optional<Participation> findByEvenement_IdAndUser_IdAndDeletedAtIsNull(Long evenementId, Long userId);
}
