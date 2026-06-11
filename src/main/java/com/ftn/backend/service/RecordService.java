package com.ftn.backend.service;

import com.ftn.backend.dtos.record.CreateRecordDto;
import com.ftn.backend.dtos.record.RecordDto;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.Athlete;
import com.ftn.backend.model.Competition;
import com.ftn.backend.model.Event;
import com.ftn.backend.model.Record;
import com.ftn.backend.repository.AthleteRepository;
import com.ftn.backend.repository.CompetitionRepository;
import com.ftn.backend.repository.EventRepository;
import com.ftn.backend.repository.RecordRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository recordRepository;
    private final AthleteRepository athleteRepository;
    private final EventRepository eventRepository;
    private final CompetitionRepository competitionRepository;

    @Transactional(readOnly = true)
    public List<RecordDto> getAll() {
        return recordRepository.findAllByDeletedAtIsNull().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RecordDto> getByAthlete(Long athleteId) {
        return recordRepository.findByAthlete_IdAndDeletedAtIsNull(athleteId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public RecordDto create(CreateRecordDto dto) {
        Athlete athlete = athleteRepository
                .findByIdAndDeletedAtIsNull(dto.getAthleteId())
                .orElseThrow(() -> new ResourceNotFoundException("Athlete not found"));
        Event event = dto.getEventId() != null
                ? eventRepository.findByIdAndDeletedAtIsNull(dto.getEventId()).orElse(null)
                : null;
        Competition competition = dto.getCompetitionId() != null
                ? competitionRepository
                        .findByIdAndDeletedAtIsNull(dto.getCompetitionId())
                        .orElse(null)
                : null;
        Record record = Record.builder()
                .athlete(athlete)
                .event(event)
                .competition(competition)
                .tempsMs(dto.getTempsMs())
                .tempsDisplay(dto.getTempsDisplay())
                .recordDate(dto.getRecordDate())
                .type(dto.getType())
                .build();
        return toDto(recordRepository.save(record));
    }

    @Transactional
    public void delete(Long id) {
        Record record = recordRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found"));
        record.setDeletedAt(LocalDateTime.now());
        recordRepository.save(record);
    }

    public RecordDto toDto(Record r) {
        return RecordDto.builder()
                .id(r.getId())
                .athleteId(r.getAthlete().getId())
                .athleteNom(r.getAthlete().getNom())
                .athletePrenom(r.getAthlete().getPrenom())
                .eventId(r.getEvent() != null ? r.getEvent().getId() : null)
                .competitionId(r.getCompetition() != null ? r.getCompetition().getId() : null)
                .competitionNom(r.getCompetition() != null ? r.getCompetition().getNom() : null)
                .tempsMs(r.getTempsMs())
                .tempsDisplay(r.getTempsDisplay())
                .recordDate(r.getRecordDate())
                .type(r.getType())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
