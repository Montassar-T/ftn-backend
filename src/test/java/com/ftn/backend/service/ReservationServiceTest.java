package com.ftn.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ftn.backend.dtos.reservation.CreateReservationDto;
import com.ftn.backend.dtos.reservation.ReservationDto;
import com.ftn.backend.enums.ReservationStatutEnum;
import com.ftn.backend.enums.TypeReservationEnum;
import com.ftn.backend.exception.business.ConflictException;
import com.ftn.backend.model.Pool;
import com.ftn.backend.model.Reservation;
import com.ftn.backend.repository.PoolRepository;
import com.ftn.backend.repository.ReservationRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    private static final Long POOL_ID = 1L;
    private static final Integer POOL_LANES = 6;
    private static final LocalDate DATE = LocalDate.of(2026, 6, 12);
    private static final LocalTime START = LocalTime.of(10, 0);
    private static final LocalTime END = LocalTime.of(11, 0);

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private PoolRepository poolRepository;

    @InjectMocks
    private ReservationService reservationService;

    private Pool pool;

    @BeforeEach
    void setUp() {
        pool = Pool.builder().id(POOL_ID).nom("Main pool").ville("Tunis").nbCouloirs(POOL_LANES).build();
        lenient().when(poolRepository.findByIdAndDeletedAtIsNullForUpdate(POOL_ID)).thenReturn(Optional.of(pool));
    }

    // ---- create() ----

    @Test
    void createRejectsWhenRequestExceedsPoolTotalLanes() {
        CreateReservationDto request = reservationRequest(POOL_LANES + 1);

        ConflictException exception = assertThrows(ConflictException.class, () -> reservationService.create(request));

        assertEquals("This pool only has " + POOL_LANES + " lanes", exception.getMessage());
        verify(reservationRepository, never()).saveAndFlush(any());
    }

    @Test
    void createRejectsWhenNotEnoughRemainingCapacity() {
        CreateReservationDto request = reservationRequest(2);
        Reservation existing = existingReservation(5, ReservationStatutEnum.EN_ATTENTE);
        when(reservationRepository.findOverlapping(POOL_ID, DATE, START, END)).thenReturn(List.of(existing));

        ConflictException exception = assertThrows(ConflictException.class, () -> reservationService.create(request));

        assertTrue(exception.getMessage().contains("Not enough available lanes"));
        verify(reservationRepository, never()).saveAndFlush(any());
    }

    @Test
    void createAllowsReservationWhenCapacityIsAvailable() {
        CreateReservationDto request = reservationRequest(2);
        Reservation existing = existingReservation(3, ReservationStatutEnum.EN_ATTENTE);
        when(reservationRepository.findOverlapping(POOL_ID, DATE, START, END)).thenReturn(List.of(existing));
        when(reservationRepository.saveAndFlush(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        reservationService.create(request);

        verify(reservationRepository).saveAndFlush(any(Reservation.class));
    }

    @Test
    void createChecksForOverlappingReservationsInSamePoolAndTimeSlot() {
        CreateReservationDto request = reservationRequest(2);
        when(reservationRepository.findOverlapping(POOL_ID, DATE, START, END)).thenReturn(List.of());
        when(reservationRepository.saveAndFlush(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        reservationService.create(request);

        verify(reservationRepository).findOverlapping(eq(POOL_ID), eq(DATE), eq(START), eq(END));
    }

    // ---- approve() ----

    @Test
    void approveAssignsLanesAndConfirmsReservation() {
        Reservation existing = existingReservation(2, ReservationStatutEnum.EN_ATTENTE);
        existing.setId(20L);
        when(reservationRepository.findByIdAndDeletedAtIsNull(20L)).thenReturn(Optional.of(existing));
        when(reservationRepository.findOverlapping(POOL_ID, DATE, START, END)).thenReturn(List.of());
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReservationDto result = reservationService.approve(20L, List.of(3, 4));

        assertEquals(ReservationStatutEnum.CONFIRMEE, result.getStatut());
        assertEquals(List.of(3, 4), result.getNumerosCouloirs());
        verify(reservationRepository).save(existing);
    }

    @Test
    void approveRejectsWhenLaneCountDoesNotMatchRequestedCount() {
        Reservation existing = existingReservation(2, ReservationStatutEnum.EN_ATTENTE);
        existing.setId(20L);
        when(reservationRepository.findByIdAndDeletedAtIsNull(20L)).thenReturn(Optional.of(existing));

        ConflictException exception =
                assertThrows(ConflictException.class, () -> reservationService.approve(20L, List.of(3)));

        assertEquals("Number of assigned lanes must match the requested number (2)", exception.getMessage());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void approveRejectsWhenLaneIsOutOfRange() {
        Reservation existing = existingReservation(1, ReservationStatutEnum.EN_ATTENTE);
        existing.setId(20L);
        when(reservationRepository.findByIdAndDeletedAtIsNull(20L)).thenReturn(Optional.of(existing));

        ConflictException exception =
                assertThrows(ConflictException.class, () -> reservationService.approve(20L, List.of(99)));

        assertEquals("Lane 99 does not exist in this pool", exception.getMessage());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void approveRejectsWhenLaneAlreadyAssignedToConfirmedOverlap() {
        Reservation existing = existingReservation(1, ReservationStatutEnum.EN_ATTENTE);
        existing.setId(20L);
        Reservation otherConfirmed = existingReservation(1, ReservationStatutEnum.CONFIRMEE);
        otherConfirmed.setId(21L);
        otherConfirmed.setNumerosCouloirs("3");
        when(reservationRepository.findByIdAndDeletedAtIsNull(20L)).thenReturn(Optional.of(existing));
        when(reservationRepository.findOverlapping(POOL_ID, DATE, START, END)).thenReturn(List.of(otherConfirmed));

        ConflictException exception =
                assertThrows(ConflictException.class, () -> reservationService.approve(20L, List.of(3)));

        assertEquals("Lane 3 is already assigned to another confirmed reservation", exception.getMessage());
        verify(reservationRepository, never()).save(any());
    }

    // ---- deny() ----

    @Test
    void denyCancelsPendingReservation() {
        Reservation existing = existingReservation(2, ReservationStatutEnum.EN_ATTENTE);
        existing.setId(21L);
        when(reservationRepository.findByIdAndDeletedAtIsNull(21L)).thenReturn(Optional.of(existing));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReservationDto result = reservationService.deny(21L);

        assertEquals(ReservationStatutEnum.ANNULEE, result.getStatut());
        verify(reservationRepository).save(existing);
    }

    // ---- helpers ----

    private CreateReservationDto reservationRequest(Integer nbCouloirs) {
        return CreateReservationDto.builder()
                .poolId(POOL_ID)
                .date(DATE)
                .heureDebut(START)
                .heureFin(END)
                .typeReservation(TypeReservationEnum.ATHLETE)
                .nbCouloirs(nbCouloirs)
                .reserveePar("swimmer@example.com")
                .build();
    }

    private Reservation existingReservation(Integer nbCouloirs, ReservationStatutEnum statut) {
        return Reservation.builder()
                .pool(pool)
                .date(DATE)
                .heureDebut(START)
                .heureFin(END)
                .typeReservation(TypeReservationEnum.ATHLETE)
                .nbCouloirs(nbCouloirs)
                .statut(statut)
                .reserveePar("existing@example.com")
                .build();
    }
}