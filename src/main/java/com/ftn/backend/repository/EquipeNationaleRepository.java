package com.ftn.backend.repository;

import com.ftn.backend.model.EquipeNationale;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EquipeNationaleRepository
        extends JpaRepository<EquipeNationale, Long>, JpaSpecificationExecutor<EquipeNationale> {

    Optional<EquipeNationale> findByIdAndDeletedAtIsNull(Long id);
}
