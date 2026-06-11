package com.ftn.backend.model;

import com.ftn.backend.enums.ReservationStatutEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pool_id", nullable = false)
    private Pool pool;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "heure_debut", nullable = false)
    private LocalTime heureDebut;

    @Column(name = "heure_fin", nullable = false)
    private LocalTime heureFin;

    // null means full pool, a number means that specific lane
    @Column(name = "numero_couloir")
    private Integer numeroCouloir;

    @Column(name = "reservee_par", nullable = false, length = 255)
    private String reserveePar; // email of the user

    @Column(name = "nom_club", length = 255)
    private String nomClub; // optional club name

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private ReservationStatutEnum statut = ReservationStatutEnum.EN_ATTENTE;

    @Column(length = 500)
    private String notes;
}