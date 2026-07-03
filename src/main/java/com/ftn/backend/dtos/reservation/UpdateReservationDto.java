package com.ftn.backend.dtos.reservation;

import com.ftn.backend.enums.TypeReservationEnum;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateReservationDto {

    private LocalDate date;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private TypeReservationEnum typeReservation;
    private Integer nbCouloirs;
    private Integer numeroCouloir;
    private List<Integer> numerosCouloirs;
    private String nomClub;
    private String notes;
}
