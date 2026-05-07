package com.ftn.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

@Entity
@Table(name = "clubs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Club extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nom;

    @Column(length = 255)
    private String ville;

    @Column(length = 255)
    private String region;

    @Column(length = 500)
    private String logo;

    @Column(name = "date_affiliation")
    private LocalDate dateAffiliation;

    @Column(nullable = false)
    @Builder.Default
    private Boolean actif = true;
}
