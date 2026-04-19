package com.carServices.backend.repository;

import com.carServices.backend.model.Mechanic;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MechanicRepository extends JpaRepository<Mechanic, Long>, JpaSpecificationExecutor<Mechanic> {
    Optional<Mechanic> findByIdAndDeletedAtIsNull(Long id);

    @Query(
            """
SELECT m FROM Mechanic m
WHERE m.deletedAt IS NULL
AND (
	LOWER(m.firstName) LIKE LOWER(CONCAT(:q, '%'))
	OR LOWER(m.lastName) LIKE LOWER(CONCAT(:q, '%'))
	OR m.phoneNumber LIKE CONCAT(:q, '%')
	OR LOWER(m.specialty) LIKE LOWER(CONCAT(:q, '%'))
)
""")
    List<Mechanic> search(@Param("q") String q);
}
