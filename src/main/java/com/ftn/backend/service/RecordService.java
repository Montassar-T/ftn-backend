package com.ftn.backend.service;

import com.ftn.backend.dtos.PageDto;
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
import com.ftn.backend.utils.JpaQueryFilters;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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
    public RecordDto getById(Long id) {
        Record record = recordRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found"));
        return toDto(record);
    }

    @Transactional(readOnly = true)
    public List<RecordDto> getAll() {
        return recordRepository.findAllByDeletedAtIsNull().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ResponseEntity<PageDto<RecordDto>> getAll(Map<String, String> params) {
        JpaQueryFilters<Record> filters = new JpaQueryFilters<>(params, Record.class);
        Page<Record> page = recordRepository.findAll(filters.getSpecification(), filters.getPageable());
        List<RecordDto> data = page.stream().map(this::toDto).toList();
        return ResponseEntity.ok(PageDto.<RecordDto>builder()
                .data(data)
                .total(page.getTotalElements())
                .build());
    }

    @Transactional(readOnly = true)
    public List<RecordDto> getByAthlete(Long athleteId) {
        return recordRepository.findByAthlete_IdAndDeletedAtIsNull(athleteId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RecordDto> getByEvent(String swimStyle, String distance) {
        return recordRepository.findBySwimStyleAndDistanceAndDeletedAtIsNull(swimStyle, distance).stream()
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

        Competition competition = null;
        if (dto.getCompetitionId() != null) {
            competition = competitionRepository
                    .findByIdAndDeletedAtIsNull(dto.getCompetitionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Competition not found"));
        }

        Record record = Record.builder()
                .athlete(athlete)
                .event(event)
                .competition(competition)
                .recordType(dto.getType())
                .type(dto.getTypeStr())
                .swimStyle(dto.getSwimStyle())
                .distance(dto.getDistance())
                .tempsMs(dto.getTempsMs())
                .tempsDisplay(dto.getTempsDisplay())
                .time(dto.getTime())
                .recordDate(dto.getRecordDate())
                .date(dto.getDate())
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

    public RecordDto toDto(Record record) {
        return RecordDto.builder()
                .id(record.getId())
                .athleteId(record.getAthlete().getId())
                .athleteNom(record.getAthlete().getNom())
                .athletePrenom(record.getAthlete().getPrenom())
                .eventId(record.getEvent() != null ? record.getEvent().getId() : null)
                .competitionId(
                        record.getCompetition() != null
                                ? record.getCompetition().getId()
                                : null)
                .competitionNom(
                        record.getCompetition() != null
                                ? record.getCompetition().getNom()
                                : null)
                .type(record.getRecordType())
                .typeStr(record.getType())
                .swimStyle(record.getSwimStyle())
                .distance(record.getDistance())
                .tempsMs(record.getTempsMs())
                .tempsDisplay(record.getTempsDisplay())
                .time(record.getTime())
                .recordDate(record.getRecordDate())
                .date(record.getDate())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
