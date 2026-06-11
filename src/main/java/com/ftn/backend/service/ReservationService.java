package com.ftn.backend.service;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.reservation.CreateReservationDto;
import com.ftn.backend.dtos.reservation.ReservationDto;
import com.ftn.backend.dtos.reservation.UpdateReservationDto;
import com.ftn.backend.exception.business.ConflictException;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.Pool;
import com.ftn.backend.model.Reservation;
import com.ftn.backend.repository.PoolRepository;
import com.ftn.backend.repository.ReservationRepository;
import com.ftn.backend.utils.JpaQueryFilters;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
        return reservationRepository.findByPoolIdAndDeletedAtIsNull(poolId)
                .stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationDto> getByUser(String email) {
        return reservationRepository.findByReserveeParAndDeletedAtIsNull(email)
                .stream().map(this::toDto).toList();
    }

    @Transactional
    public ReservationDto create(CreateReservationDto dto) {
        Pool pool = poolRepository
                .findByIdAndDeletedAtIsNull(dto.getPoolId())
                .orElseThrow(() -> new ResourceNotFoundException("Pool not found"));

        boolean conflict = reservationRepository.existsConflict(
                dto.getPoolId(), dto.getDate(),
                dto.getHeureDebut(), dto.getHeureFin(),
                dto.getNumeroCouloir()
        );
        if (conflict) {
            throw new ConflictException("Ce créneau est déjà réservé pour cette piscine");
        }

        Reservation reservation = Reservation.builder()
                .pool(pool)
                .date(dto.getDate())
                .heureDebut(dto.getHeureDebut())
                .heureFin(dto.getHeureFin())
                .numeroCouloir(dto.getNumeroCouloir())
                .reserveePar(dto.getReserveePar())
                .nomClub(dto.getNomClub())
                .notes(dto.getNotes())
                .build();

        return toDto(reservationRepository.save(reservation));
    }

    @Transactional
    public ReservationDto update(Long id, UpdateReservationDto dto) {
        Reservation r = reservationRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        if (dto.getDate() != null) r.setDate(dto.getDate());
        if (dto.getHeureDebut() != null) r.setHeureDebut(dto.getHeureDebut());
        if (dto.getHeureFin() != null) r.setHeureFin(dto.getHeureFin());
        if (dto.getNumeroCouloir() != null) r.setNumeroCouloir(dto.getNumeroCouloir());
        if (dto.getNomClub() != null) r.setNomClub(dto.getNomClub());
        if (dto.getNotes() != null) r.setNotes(dto.getNotes());
        if (dto.getStatut() != null) r.setStatut(dto.getStatut());

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

    public ReservationDto toDto(Reservation r) {
        return ReservationDto.builder()
                .id(r.getId())
                .poolId(r.getPool().getId())
                .poolNom(r.getPool().getNom())
                .poolVille(r.getPool().getVille())
                .date(r.getDate())
                .heureDebut(r.getHeureDebut())
                .heureFin(r.getHeureFin())
                .numeroCouloir(r.getNumeroCouloir())
                .reserveePar(r.getReserveePar())
                .nomClub(r.getNomClub())
                .statut(r.getStatut())
                .notes(r.getNotes())
                .createdAt(r.getCreatedAt())
                .build();
    }
}