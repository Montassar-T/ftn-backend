package com.ftn.backend.model;

import com.ftn.backend.enums.ReservationStatutEnum;
import com.ftn.backend.enums.TypeReservationEnum;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.*;

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

    @Column(name = "nb_couloirs")
    private Integer nbCouloirs;

    // Kept for backward compatibility (single lane, athlete mode)
    @Column(name = "numero_couloir")
    private Integer numeroCouloir;

    // Comma-separated lane numbers for club mode, e.g. "1,2,3". Null/empty = whole pool.
    @Column(name = "numeros_couloirs", length = 100)
    private String numerosCouloirs;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_reservation", nullable = false, length = 20)
    @Builder.Default
    private TypeReservationEnum typeReservation = TypeReservationEnum.ATHLETE;

    @Column(name = "reservee_par", nullable = false, length = 255)
    private String reserveePar;

    @Column(name = "nom_club", length = 255)
    private String nomClub;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private ReservationStatutEnum statut = ReservationStatutEnum.EN_ATTENTE;

    @Column(name = "seen_by_user", nullable = false)
    @Builder.Default
    private Boolean seenByUser = true;

    @Column(length = 500)
    private String notes;
}
