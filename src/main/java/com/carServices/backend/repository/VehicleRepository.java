package com.carServices.backend.repository;

import com.carServices.backend.model.Vehicle;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VehicleRepository extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {
    Optional<Vehicle> findByIdAndDeletedAtIsNull(Long id);

    @Query(
            """
SELECT v FROM Vehicle v
JOIN v.client c
JOIN v.model m
WHERE v.deletedAt IS NULL
AND (
	LOWER(v.registration) = LOWER(:q)
	OR LOWER(v.vin) = LOWER(:q)

	OR LOWER(v.registration) LIKE LOWER(CONCAT(:q, '%'))
	OR LOWER(v.vin) LIKE LOWER(CONCAT(:q, '%'))

	OR LOWER(m.name) LIKE LOWER(CONCAT(:q, '%'))

	OR LOWER(c.name) LIKE LOWER(CONCAT(:q, '%'))
	OR c.phoneNumber LIKE CONCAT(:q, '%')
)
""")
    List<Vehicle> search(@Param("q") String q);
}
