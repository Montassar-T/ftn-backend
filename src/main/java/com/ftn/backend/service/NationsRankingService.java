package com.ftn.backend.service;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.ranking.CreateNationsRankingDto;
import com.ftn.backend.dtos.ranking.NationsRankingDto;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.Athlete;
import com.ftn.backend.model.Event;
import com.ftn.backend.model.NationsRanking;
import com.ftn.backend.repository.AthleteRepository;
import com.ftn.backend.repository.EventRepository;
import com.ftn.backend.repository.NationsRankingRepository;
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
public class NationsRankingService {

    private final NationsRankingRepository rankingRepository;
    private final AthleteRepository athleteRepository;
    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    public ResponseEntity<PageDto<NationsRankingDto>> getAll(Map<String, String> params) {
        JpaQueryFilters<NationsRanking> filters = new JpaQueryFilters<>(params, NationsRanking.class);
        Page<NationsRanking> page = rankingRepository.findAll(filters.getSpecification(), filters.getPageable());
        List<NationsRankingDto> data = page.stream().map(this::toDto).toList();
        return ResponseEntity.ok(PageDto.<NationsRankingDto>builder()
                .data(data)
                .total(page.getTotalElements())
                .build());
    }

    @Transactional
    public NationsRankingDto create(CreateNationsRankingDto dto) {
        Athlete athlete = athleteRepository
                .findByIdAndDeletedAtIsNull(dto.getAthleteId())
                .orElseThrow(() -> new ResourceNotFoundException("Athlete not found"));
        Event event = dto.getEventId() != null
                ? eventRepository.findByIdAndDeletedAtIsNull(dto.getEventId()).orElse(null)
                : null;
        NationsRanking ranking = NationsRanking.builder()
                .athlete(athlete)
                .event(event)
                .bestTimeMs(dto.getBestTimeMs())
                .pointsFina(dto.getPointsFina())
                .rankPosition(dto.getRankPosition())
                .season(dto.getSeason())
                .build();
        return toDto(rankingRepository.save(ranking));
    }

    @Transactional
    public void delete(Long id) {
        NationsRanking ranking = rankingRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ranking not found"));
        ranking.setDeletedAt(LocalDateTime.now());
        rankingRepository.save(ranking);
    }

    private String formatTime(Long ms) {
        if (ms == null) return null;
        long minutes = ms / 60000;
        long seconds = (ms % 60000) / 1000;
        long millis = ms % 1000;
        return String.format("%d:%02d.%03d", minutes, seconds, millis);
    }

    public NationsRankingDto toDto(NationsRanking r) {
        return NationsRankingDto.builder()
                .id(r.getId())
                .athleteId(r.getAthlete().getId())
                .athleteNom(r.getAthlete().getNom())
                .athletePrenom(r.getAthlete().getPrenom())
                .eventId(r.getEvent() != null ? r.getEvent().getId() : null)
                .bestTimeMs(r.getBestTimeMs())
                .bestTimeDisplay(formatTime(r.getBestTimeMs()))
                .pointsFina(r.getPointsFina())
                .rankPosition(r.getRankPosition())
                .season(r.getSeason())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
