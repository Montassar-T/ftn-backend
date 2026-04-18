package com.carServices.backend.model;

import jakarta.persistence.*;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "vehicle_models")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleModel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "make_id")
    private VehicleMake make;

    @Column(nullable = false)
    private Boolean systemAttribute;

    @OneToMany(mappedBy = "model", fetch = FetchType.LAZY)
    private List<Vehicle> vehicles;
}
