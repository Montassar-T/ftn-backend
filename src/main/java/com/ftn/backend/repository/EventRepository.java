package com.ftn.backend.repository;

import com.ftn.backend.model.Event;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    Optional<Event> findByIdAndDeletedAtIsNull(Long id);

    List<Event> findByCompetition_IdAndDeletedAtIsNull(Long competitionId);
}
