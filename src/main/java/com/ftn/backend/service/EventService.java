package com.ftn.backend.service;

import com.ftn.backend.dtos.event.CreateEventDto;
import com.ftn.backend.dtos.event.EventDto;
import com.ftn.backend.dtos.event.UpdateEventDto;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.Competition;
import com.ftn.backend.model.Event;
import com.ftn.backend.repository.CompetitionRepository;
import com.ftn.backend.repository.EventRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final CompetitionRepository competitionRepository;

    @Transactional(readOnly = true)
    public List<EventDto> getByCompetition(Long competitionId) {
        return eventRepository.findByCompetition_IdAndDeletedAtIsNull(competitionId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public EventDto getById(Long id) {
        return toDto(eventRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found")));
    }

    @Transactional
    public EventDto create(CreateEventDto dto) {
        Competition competition = competitionRepository
                .findByIdAndDeletedAtIsNull(dto.getCompetitionId())
                .orElseThrow(() -> new ResourceNotFoundException("Competition not found"));
        Event event = Event.builder()
                .competition(competition)
                .swimStyle(dto.getSwimStyle())
                .distance(dto.getDistance())
                .gender(dto.getGender())
                .ageCategory(dto.getAgeCategory())
                .round(dto.getRound())
                .scheduledDate(dto.getScheduledDate())
                .build();
        return toDto(eventRepository.save(event));
    }

    @Transactional
    public EventDto update(Long id, UpdateEventDto dto) {
        Event event = eventRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        if (dto.getSwimStyle() != null) event.setSwimStyle(dto.getSwimStyle());
        if (dto.getDistance() != null) event.setDistance(dto.getDistance());
        if (dto.getGender() != null) event.setGender(dto.getGender());
        if (dto.getAgeCategory() != null) event.setAgeCategory(dto.getAgeCategory());
        if (dto.getRound() != null) event.setRound(dto.getRound());
        if (dto.getScheduledDate() != null) event.setScheduledDate(dto.getScheduledDate());
        if (dto.getStatus() != null) event.setStatus(dto.getStatus());
        return toDto(eventRepository.save(event));
    }

    @Transactional
    public void delete(Long id) {
        Event event = eventRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        event.setDeletedAt(LocalDateTime.now());
        eventRepository.save(event);
    }

    public EventDto toDto(Event e) {
        return EventDto.builder()
                .id(e.getId())
                .competitionId(e.getCompetition().getId())
                .competitionNom(e.getCompetition().getName())
                .swimStyle(e.getSwimStyle())
                .distance(e.getDistance())
                .gender(e.getGender())
                .ageCategory(e.getAgeCategory())
                .round(e.getRound())
                .scheduledDate(e.getScheduledDate())
                .status(e.getStatus())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
