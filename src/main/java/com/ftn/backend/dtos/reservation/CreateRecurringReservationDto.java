package com.ftn.backend.dtos.reservation;

import com.ftn.backend.enums.TypeReservationEnum;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRecurringReservationDto {

    @NotNull(message = "Pool ID is required")
    private Long poolId;

    // Date of the FIRST occurrence. Its day-of-week is the day that repeats
    // (e.g. a Tuesday startDate => every following occurrence is also a Tuesday).
    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "Start time is required")
    private LocalTime heureDebut;

    @NotNull(message = "End time is required")
    private LocalTime heureFin;

    @NotNull(message = "Type de réservation is required")
    private TypeReservationEnum typeReservation;

    @NotNull(message = "Nombre de couloirs is required")
    @Min(value = 1, message = "Nombre de couloirs must be at least 1")
    private Integer nbCouloirs;

    // Number of weekly occurrences to generate (e.g. 8 = "for 8 weeks")
    @NotNull(message = "Number of occurrences is required")
    @Min(value = 2, message = "Use the regular booking endpoint for a single occurrence")
    @Max(value = 52, message = "Cannot book more than 52 occurrences at once")
    private Integer occurrences;

    private String nomClub;
    private String notes;
}