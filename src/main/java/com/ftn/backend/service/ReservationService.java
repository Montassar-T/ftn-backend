package com.ftn.backend.service;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.reservation.CreateReservationDto;
import com.ftn.backend.dtos.reservation.ReservationDto;
import com.ftn.backend.dtos.reservation.UpdateReservationDto;
import com.ftn.backend.enums.ReservationStatutEnum;
import com.ftn.backend.enums.TypeReservationEnum;
import com.ftn.backend.exception.business.ConflictException;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.Pool;
import com.ftn.backend.model.Reservation;
import com.ftn.backend.repository.PoolRepository;
import com.ftn.backend.repository.ReservationRepository;
import com.ftn.backend.utils.JpaQueryFilters;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final PoolRepository poolRepository;

    @Transactional(readOnly = true)
    public ReservationDto getById(Long id) {
        Reservation r = reservationRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
        return toDto(r);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<PageDto<ReservationDto>> getAll(Map<String, String> params) {
        JpaQueryFilters<Reservation> filters = new JpaQueryFilters<>(params, Reservation.class);
        Page<Reservation> page = reservationRepository.findAll(filters.getSpecification(), filters.getPageable());
        List<ReservationDto> data = page.stream().map(this::toDto).toList();
        return ResponseEntity.ok(PageDto.<ReservationDto>builder()
                .data(data)
                .total(page.getTotalElements())
                .build());
    }

    @Transactional(readOnly = true)
    public List<ReservationDto> getByPool(Long poolId) {
        return reservationRepository.findByPoolIdAndDeletedAtIsNull(poolId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationDto> getByUser(String email) {
        return reservationRepository.findByReserveeParAndDeletedAtIsNull(email).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public ReservationDto create(CreateReservationDto dto) {
        Pool pool = poolRepository
                .findByIdAndDeletedAtIsNullForUpdate(dto.getPoolId())
                .orElseThrow(() -> new ResourceNotFoundException("Pool not found"));

        // Determine requested lanes: null/empty list = whole pool
        List<Integer> requestedLanes =
                resolveRequestedLanes(dto.getTypeReservation(), dto.getNumeroCouloir(), dto.getNumerosCouloirs());

        // Check conflicts against existing overlapping reservations
        List<Reservation> overlapping = reservationRepository.findOverlapping(
                dto.getPoolId(), dto.getDate(), dto.getHeureDebut(), dto.getHeureFin());

        for (Reservation existing : overlapping) {
            List<Integer> existingLanes = resolveExistingLanes(existing);
            if (lanesConflict(requestedLanes, existingLanes)) {
                throw new ConflictException(conflictMessage(requestedLanes, existingLanes));
            }
        }

        Reservation reservation = Reservation.builder()
                .pool(pool)
                .date(dto.getDate())
                .heureDebut(dto.getHeureDebut())
                .heureFin(dto.getHeureFin())
                .typeReservation(dto.getTypeReservation())
                .nbCouloirs(dto.getNbCouloirs())
                .numeroCouloir(dto.getNumeroCouloir())
                .numerosCouloirs(toCsv(dto.getNumerosCouloirs()))
                .reserveePar(dto.getReserveePar())
                .nomClub(dto.getNomClub())
                .notes(dto.getNotes())
                .build();

        try {
            return toDto(reservationRepository.saveAndFlush(reservation));
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(conflictMessage(requestedLanes, null));
        }
    }

    @Transactional
    public ReservationDto update(Long id, UpdateReservationDto dto) {
        Reservation r = reservationRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        if (dto.getDate() != null) r.setDate(dto.getDate());
        if (dto.getHeureDebut() != null) r.setHeureDebut(dto.getHeureDebut());
        if (dto.getHeureFin() != null) r.setHeureFin(dto.getHeureFin());
        if (dto.getTypeReservation() != null) r.setTypeReservation(dto.getTypeReservation());
        if (dto.getNbCouloirs() != null) r.setNbCouloirs(dto.getNbCouloirs());
        if (dto.getNumeroCouloir() != null) r.setNumeroCouloir(dto.getNumeroCouloir());
        if (dto.getNumerosCouloirs() != null) r.setNumerosCouloirs(toCsv(dto.getNumerosCouloirs()));
        if (dto.getNomClub() != null) r.setNomClub(dto.getNomClub());
        if (dto.getNotes() != null) r.setNotes(dto.getNotes());

        return toDto(reservationRepository.save(r));
    }

    @Transactional
    public ReservationDto approve(Long id) {
        Reservation r = reservationRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        r.setStatut(ReservationStatutEnum.CONFIRMEE);
        return toDto(reservationRepository.save(r));
    }

    @Transactional
    public ReservationDto deny(Long id) {
        Reservation r = reservationRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        r.setStatut(ReservationStatutEnum.ANNULEE);
        return toDto(reservationRepository.save(r));
    }

    @Transactional
    public void delete(Long id) {
        Reservation r = reservationRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
        r.setDeletedAt(LocalDateTime.now());
        reservationRepository.save(r);
    }

    // ---- Helpers ----

    /** Returns null to mean "whole pool", otherwise the list of requested lane numbers. */
    private List<Integer> resolveRequestedLanes(
            TypeReservationEnum type, Integer numeroCouloir, List<Integer> numerosCouloirs) {
        if (type == TypeReservationEnum.CLUB) {
            if (numerosCouloirs == null || numerosCouloirs.isEmpty()) {
                return null; // whole pool
            }
            return numerosCouloirs;
        } else {
            // ATHLETE
            if (numeroCouloir == null) {
                return null; // whole pool (rare)
            }
            return List.of(numeroCouloir);
        }
    }

    private List<Integer> resolveExistingLanes(Reservation r) {
        if (r.getNumerosCouloirs() != null && !r.getNumerosCouloirs().isBlank()) {
            return Arrays.stream(r.getNumerosCouloirs().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }
        if (r.getNumeroCouloir() != null) {
            return List.of(r.getNumeroCouloir());
        }
        return null; // whole pool
    }

    /** Conflict if either side is "whole pool" (null), or if lane lists intersect. */
    private boolean lanesConflict(List<Integer> requested, List<Integer> existing) {
        if (requested == null || existing == null) {
            return true; // one of them occupies the whole pool
        }
        for (Integer lane : requested) {
            if (existing.contains(lane)) return true;
        }
        return false;
    }

    private String conflictMessage(List<Integer> requested, List<Integer> existing) {
        if (requested == null) {
            return "Pool is already reserved for this time slot";
        }
        if (existing == null) {
            return "Cannot reserve lane because the pool is already reserved for this time slot";
        }
        return "Lane is already reserved for this time slot";
    }

    private String toCsv(List<Integer> lanes) {
        if (lanes == null || lanes.isEmpty()) return null;
        return lanes.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    private List<Integer> fromCsv(String csv) {
        if (csv == null || csv.isBlank()) return new ArrayList<>();
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    public ReservationDto toDto(Reservation r) {
        return ReservationDto.builder()
                .id(r.getId())
                .poolId(r.getPool().getId())
                .poolNom(r.getPool().getNom())
                .poolVille(r.getPool().getVille())
                .date(r.getDate())
                .heureDebut(r.getHeureDebut())
                .heureFin(r.getHeureFin())
                .typeReservation(r.getTypeReservation())
                .nbCouloirs(r.getNbCouloirs())
                .numeroCouloir(r.getNumeroCouloir())
                .numerosCouloirs(fromCsv(r.getNumerosCouloirs()))
                .reserveePar(r.getReserveePar())
                .nomClub(r.getNomClub())
                .statut(r.getStatut())
                .notes(r.getNotes())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
