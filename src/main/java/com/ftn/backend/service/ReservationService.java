package com.ftn.backend.service;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.reservation.CreateReservationDto;
import com.ftn.backend.dtos.reservation.ReservationDto;
import com.ftn.backend.dtos.reservation.UpdateReservationDto;
import com.ftn.backend.enums.ReservationStatutEnum;
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
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("RESERVATION_APPROVE"));
        if (!isAdmin && !auth.getName().equals(email)) {
            throw new com.ftn.backend.exception.auth.AuthException("You cannot view another user's reservations");
        }
        return reservationRepository.findByReserveeParAndDeletedAtIsNull(email).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public ReservationDto create(CreateReservationDto dto) {
        String currentUserEmail = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        Pool pool = poolRepository
                .findByIdAndDeletedAtIsNullForUpdate(dto.getPoolId())
                .orElseThrow(() -> new ResourceNotFoundException("Pool not found"));

        int totalLanes = pool.getNbCouloirs() != null ? pool.getNbCouloirs() : 0;

        if (dto.getNbCouloirs() > totalLanes) {
            throw new ConflictException("This pool only has " + totalLanes + " lanes");
        }

        // Check remaining capacity against every overlapping, non-cancelled reservation
        List<Reservation> overlapping = reservationRepository.findOverlapping(
                dto.getPoolId(), dto.getDate(), dto.getHeureDebut(), dto.getHeureFin());

        int alreadyReserved = overlapping.stream()
                .mapToInt(r -> r.getNbCouloirs() != null ? r.getNbCouloirs() : 0)
                .sum();

        if (alreadyReserved + dto.getNbCouloirs() > totalLanes) {
            int remaining = Math.max(totalLanes - alreadyReserved, 0);
            throw new ConflictException(
                    "Not enough available lanes for this time slot (only " + remaining + " remaining)");
        }

        Reservation reservation = Reservation.builder()
                .pool(pool)
                .date(dto.getDate())
                .heureDebut(dto.getHeureDebut())
                .heureFin(dto.getHeureFin())
                .typeReservation(dto.getTypeReservation())
                .nbCouloirs(dto.getNbCouloirs())
                .reserveePar(currentUserEmail)
                .nomClub(dto.getNomClub())
                .notes(dto.getNotes())
                .build();

        try {
            return toDto(reservationRepository.saveAndFlush(reservation));
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Not enough available lanes for this time slot");
        }
    }

    @Transactional
    public ReservationDto update(Long id, UpdateReservationDto dto) {
        Reservation r = reservationRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("RESERVATION_APPROVE"));
        boolean isOwner = r.getReserveePar().equals(auth.getName());

        if (!isAdmin && !isOwner) {
            throw new com.ftn.backend.exception.auth.AuthException("You cannot edit this reservation");
        }
        if (!isAdmin && r.getStatut() != ReservationStatutEnum.EN_ATTENTE) {
            throw new ConflictException("Reservation already reviewed, it can no longer be edited");
        }

        if (dto.getDate() != null) r.setDate(dto.getDate());
        if (dto.getHeureDebut() != null) r.setHeureDebut(dto.getHeureDebut());
        if (dto.getHeureFin() != null) r.setHeureFin(dto.getHeureFin());
        if (dto.getTypeReservation() != null) r.setTypeReservation(dto.getTypeReservation());
        if (dto.getNbCouloirs() != null) r.setNbCouloirs(dto.getNbCouloirs());
        if (dto.getNomClub() != null) r.setNomClub(dto.getNomClub());
        if (dto.getNotes() != null) r.setNotes(dto.getNotes());

        return toDto(reservationRepository.save(r));
    }

    @Transactional
    public ReservationDto approve(Long id, List<Integer> lanes) {
        Reservation r = reservationRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        if (lanes == null || lanes.isEmpty()) {
            throw new ConflictException("You must assign at least one lane");
        }
        if (r.getNbCouloirs() != null && lanes.size() != r.getNbCouloirs()) {
            throw new ConflictException(
                    "Number of assigned lanes must match the requested number (" + r.getNbCouloirs() + ")");
        }

        int totalLanes = r.getPool().getNbCouloirs() != null ? r.getPool().getNbCouloirs() : 0;
        for (Integer lane : lanes) {
            if (lane < 1 || lane > totalLanes) {
                throw new ConflictException("Lane " + lane + " does not exist in this pool");
            }
        }

        List<Reservation> overlapping = reservationRepository.findOverlapping(
                r.getPool().getId(), r.getDate(), r.getHeureDebut(), r.getHeureFin());

        for (Reservation other : overlapping) {
            if (other.getId().equals(r.getId())) continue;
            if (other.getStatut() != ReservationStatutEnum.CONFIRMEE) continue;
            List<Integer> otherLanes = fromCsv(other.getNumerosCouloirs());
            for (Integer lane : lanes) {
                if (otherLanes.contains(lane)) {
                    throw new ConflictException(
                            "Lane " + lane + " is already assigned to another confirmed reservation");
                }
            }
        }

        r.setNumerosCouloirs(toCsv(lanes));
        r.setStatut(ReservationStatutEnum.CONFIRMEE);
        r.setSeenByUser(false);
        return toDto(reservationRepository.save(r));
    }

    @Transactional
    public ReservationDto deny(Long id) {
        Reservation r = reservationRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        r.setStatut(ReservationStatutEnum.ANNULEE);
        r.setSeenByUser(false);
        return toDto(reservationRepository.save(r));
    }

    @Transactional(readOnly = true)
    public long getUnseenCount() {
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return reservationRepository.countUnseenForUser(email);
    }

    @Transactional(readOnly = true)
    public long getPendingCount() {
        return reservationRepository.countPending();
    }

    @Transactional
    public void markSeen() {
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        reservationRepository.markSeenForUser(email);
    }

    @Transactional
    public void delete(Long id) {
        Reservation r = reservationRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("RESERVATION_APPROVE"));
        boolean isOwner = r.getReserveePar().equals(auth.getName());

        if (!isAdmin && !isOwner) {
            throw new com.ftn.backend.exception.auth.AuthException("You cannot delete this reservation");
        }

        r.setDeletedAt(LocalDateTime.now());
        reservationRepository.save(r);
    }

    // ---- Helpers ----

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