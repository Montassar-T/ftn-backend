package com.ftn.backend.repository;

import com.ftn.backend.enums.CategorieActuEnum;
import com.ftn.backend.model.Actualite;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ActualiteRepository extends JpaRepository<Actualite, Long>, JpaSpecificationExecutor<Actualite> {

    Optional<Actualite> findByIdAndDeletedAtIsNull(Long id);

    List<Actualite> findByPublieTrueAndDeletedAtIsNull();

    List<Actualite> findByCategorieAndDeletedAtIsNull(CategorieActuEnum categorie);

    List<Actualite> findByTitreContainingIgnoreCaseAndDeletedAtIsNull(String titre);

    long countByDeletedAtIsNull();
}
