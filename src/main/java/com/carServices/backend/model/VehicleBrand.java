package com.carServices.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicle_brands")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleBrand extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
}
