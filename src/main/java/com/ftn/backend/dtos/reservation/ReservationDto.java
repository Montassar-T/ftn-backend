package com.ftn.backend.dtos.reservation;

import com.ftn.backend.enums.ReservationStatutEnum;
import com.ftn.backend.enums.TypeReservationEnum;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDto {

    private Long id;
    private Long poolId;
    private String poolNom;
    private String poolVille;
    private LocalDate date;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private TypeReservationEnum typeReservation;
    private Integer nbCouloirs;
    private Integer numeroCouloir;
    private List<Integer> numerosCouloirs;
    private String reserveePar;
    private String nomClub;
    private ReservationStatutEnum statut;
    private String notes;
    private LocalDateTime createdAt;
}
