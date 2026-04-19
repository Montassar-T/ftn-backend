package com.carServices.backend.model;

import com.carServices.backend.enums.FuelLevel;
import com.carServices.backend.enums.RepairStatus;
import com.carServices.backend.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "repairs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Repair extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RepairStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Mechanic mechanic;

    private Integer mileage;

    @Enumerated(EnumType.STRING)
    private FuelLevel fuelLevel;

    private Boolean hasTools;

    private Boolean hasSpareWheel;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtils.TIMESTAMP_FORMAT)
    private LocalDateTime scheduledAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtils.TIMESTAMP_FORMAT)
    private LocalDateTime receivedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtils.TIMESTAMP_FORMAT)
    private LocalDateTime expectedDeliveryAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtils.TIMESTAMP_FORMAT)
    private LocalDateTime startedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtils.TIMESTAMP_FORMAT)
    private LocalDateTime completedAt;
}
