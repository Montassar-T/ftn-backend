package com.ftn.backend.controller;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.SingleResultDto;
import com.ftn.backend.dtos.reservation.AssignLanesDto;
import com.ftn.backend.dtos.reservation.CreateRecurringReservationDto;
import com.ftn.backend.dtos.reservation.CreateReservationDto;
import com.ftn.backend.dtos.reservation.ReservationDto;
import com.ftn.backend.dtos.reservation.UpdateReservationDto;
import com.ftn.backend.service.ReservationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservation", description = "Pool reservation management APIs")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<PageDto<ReservationDto>> getAll(@RequestParam Map<String, String> params) {
        return reservationService.getAll(params);
    }

    @GetMapping("/unseen-count")
    public ResponseEntity<Map<String, Long>> getUnseenCount() {
        return ResponseEntity.ok(Map.of("count", reservationService.getUnseenCount()));
    }

    @GetMapping("/pending-count")
    @PreAuthorize("hasAuthority('RESERVATION_APPROVE')")
    public ResponseEntity<Map<String, Long>> getPendingCount() {
        return ResponseEntity.ok(Map.of("count", reservationService.getPendingCount()));
    }

    @PutMapping("/mark-seen")
    public ResponseEntity<Void> markSeen() {
        reservationService.markSeen();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResultDto<ReservationDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(reservationService.getById(id)));
    }

    @GetMapping("/pool/{poolId}")
    public ResponseEntity<List<ReservationDto>> getByPool(@PathVariable Long poolId) {
        return ResponseEntity.ok(reservationService.getByPool(poolId));
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<List<ReservationDto>> getByUser(@PathVariable String email) {
        return ResponseEntity.ok(reservationService.getByUser(email));
    }

    @PostMapping
    public ResponseEntity<SingleResultDto<ReservationDto>> create(@Valid @RequestBody CreateReservationDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(reservationService.create(dto)));
    }

    @PostMapping("/recurring")
    public ResponseEntity<List<ReservationDto>> createRecurring(@Valid @RequestBody CreateRecurringReservationDto dto) {
        return ResponseEntity.ok(reservationService.createRecurring(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SingleResultDto<ReservationDto>> update(
            @PathVariable Long id, @RequestBody UpdateReservationDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(reservationService.update(id, dto)));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('RESERVATION_APPROVE')")
    public ResponseEntity<SingleResultDto<ReservationDto>> approve(
            @PathVariable Long id, @Valid @RequestBody AssignLanesDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(reservationService.approve(id, dto.getLanes())));
    }

    @PutMapping("/{id}/deny")
    @PreAuthorize("hasAuthority('RESERVATION_APPROVE')")
    public ResponseEntity<SingleResultDto<ReservationDto>> deny(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(reservationService.deny(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
