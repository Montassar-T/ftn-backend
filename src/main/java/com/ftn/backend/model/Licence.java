package com.ftn.backend.model;

import com.ftn.backend.enums.StatutLicenceEnum;
import com.ftn.backend.enums.TypeLicenceEnum;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

@Entity
@Table(name = "licences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Licence extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "athlete_id", nullable = false)
    private Athlete athlete;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @Column(unique = true, length = 100)
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private TypeLicenceEnum type;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_expiration")
    private LocalDate dateExpiration;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    @Builder.Default
    private StatutLicenceEnum statut = StatutLicenceEnum.EN_ATTENTE;
}
