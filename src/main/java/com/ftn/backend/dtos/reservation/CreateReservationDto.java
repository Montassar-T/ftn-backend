package com.ftn.backend.dtos.reservation;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

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

    private Integer numeroCouloir; // null = full pool reservation

    @NotNull(message = "Reservee par is required")
    private String reserveePar;

    private String nomClub;
    private String notes;
}