package com.ftn.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ftn.backend.dtos.reservation.CreateReservationDto;
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
        pool = Pool.builder().id(POOL_ID).nom("Main pool").ville("Tunis").build();
        when(poolRepository.findByIdAndDeletedAtIsNullForUpdate(POOL_ID)).thenReturn(Optional.of(pool));
    }

    @Test
    void createRejectsReservationWhenSameLaneOverlaps() {
        CreateReservationDto request = athleteReservation(2);
        Reservation existing = existingReservation(2, null);
        when(reservationRepository.findOverlapping(POOL_ID, DATE, START, END)).thenReturn(List.of(existing));

        ConflictException exception = assertThrows(ConflictException.class, () -> reservationService.create(request));

        assertEquals("Lane is already reserved for this time slot", exception.getMessage());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void createRejectsWholePoolReservationWhenPoolHasOverlappingReservation() {
        CreateReservationDto request = clubReservation(null);
        Reservation existing = existingReservation(2, null);
        when(reservationRepository.findOverlapping(POOL_ID, DATE, START, END)).thenReturn(List.of(existing));

        ConflictException exception = assertThrows(ConflictException.class, () -> reservationService.create(request));

        assertEquals("Pool is already reserved for this time slot", exception.getMessage());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void createRejectsLaneReservationWhenWholePoolIsAlreadyReserved() {
        CreateReservationDto request = athleteReservation(2);
        Reservation existing = existingReservation(null, null);
        when(reservationRepository.findOverlapping(POOL_ID, DATE, START, END)).thenReturn(List.of(existing));

        ConflictException exception = assertThrows(ConflictException.class, () -> reservationService.create(request));

        assertEquals(
                "Cannot reserve lane because the pool is already reserved for this time slot", exception.getMessage());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void createAllowsDifferentLaneDuringOverlappingTimeSlot() {
        CreateReservationDto request = athleteReservation(3);
        Reservation existing = existingReservation(2, null);
        Reservation saved = existingReservation(3, null);
        saved.setId(10L);
        when(reservationRepository.findOverlapping(POOL_ID, DATE, START, END)).thenReturn(List.of(existing));
        when(reservationRepository.saveAndFlush(any(Reservation.class))).thenReturn(saved);

        reservationService.create(request);

        verify(reservationRepository).saveAndFlush(any(Reservation.class));
    }

    @Test
    void createChecksForOverlappingReservationsInSamePoolAndTimeSlot() {
        CreateReservationDto request = athleteReservation(2);
        when(reservationRepository.findOverlapping(POOL_ID, DATE, START, END)).thenReturn(List.of());
        when(reservationRepository.saveAndFlush(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        reservationService.create(request);

        verify(reservationRepository).findOverlapping(eq(POOL_ID), eq(DATE), eq(START), eq(END));
    }

    private CreateReservationDto athleteReservation(Integer lane) {
        return CreateReservationDto.builder()
                .poolId(POOL_ID)
                .date(DATE)
                .heureDebut(START)
                .heureFin(END)
                .typeReservation(TypeReservationEnum.ATHLETE)
                .nbCouloirs(1)
                .numeroCouloir(lane)
                .reserveePar("swimmer@example.com")
                .build();
    }

    private CreateReservationDto clubReservation(List<Integer> lanes) {
        return CreateReservationDto.builder()
                .poolId(POOL_ID)
                .date(DATE)
                .heureDebut(START)
                .heureFin(END)
                .typeReservation(TypeReservationEnum.CLUB)
                .nbCouloirs(1)
                .numerosCouloirs(lanes)
                .reserveePar("club@example.com")
                .build();
    }

    private Reservation existingReservation(Integer lane, String lanes) {
        return Reservation.builder()
                .pool(pool)
                .date(DATE)
                .heureDebut(START)
                .heureFin(END)
                .typeReservation(TypeReservationEnum.ATHLETE)
                .numeroCouloir(lane)
                .numerosCouloirs(lanes)
                .reserveePar("existing@example.com")
                .build();
    }
}
