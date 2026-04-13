package com.carServices.backend.model;

import com.carServices.backend.enums.MechanicStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mechanics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mechanic extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String phoneNumber;

    @Column
    private String specialty;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MechanicStatus status;
}
