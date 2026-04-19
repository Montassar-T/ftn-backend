package com.carServices.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicle_makes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleMake extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Builder.Default
    @Column(nullable = false)
    private Boolean systemAttribute = false;
}
