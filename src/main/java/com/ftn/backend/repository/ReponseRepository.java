package com.ftn.backend.repository;

import com.ftn.backend.model.Reponse;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReponseRepository extends JpaRepository<Reponse, Long>, JpaSpecificationExecutor<Reponse> {

    Optional<Reponse> findByIdAndDeletedAtIsNull(Long id);

    List<Reponse> findBySujet_IdAndDeletedAtIsNull(Long sujetId);
}
