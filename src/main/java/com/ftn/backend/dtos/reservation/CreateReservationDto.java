package com.ftn.backend.dtos.reservation;

import com.ftn.backend.enums.TypeReservationEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReservationDto {

    @NotNull(message = "Pool ID is required")
    private Long poolId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Start time is required")
    private LocalTime heureDebut;

    @NotNull(message = "End time is required")
    private LocalTime heureFin;

    @NotNull(message = "Type de réservation is required")
    private TypeReservationEnum typeReservation;

    @NotNull(message = "Nombre de couloirs is required")
    @Min(value = 1, message = "Nombre de couloirs must be at least 1")
    private Integer nbCouloirs;

    // Athlete mode: single lane, or null = whole pool (rare for athlete)
    private Integer numeroCouloir;

    // Club mode: list of lanes, empty/null = whole pool
    private List<Integer> numerosCouloirs;

    @NotNull(message = "Reservee par is required")
    private String reserveePar;

    private String nomClub;
    private String notes;
}
