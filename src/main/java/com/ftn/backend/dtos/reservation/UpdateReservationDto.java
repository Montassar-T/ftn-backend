package com.ftn.backend.dtos.reservation;

import com.ftn.backend.enums.ReservationStatutEnum;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateReservationDto {

    private LocalDate date;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private Integer numeroCouloir;
    private String nomClub;
    private String notes;
    private ReservationStatutEnum statut;
}