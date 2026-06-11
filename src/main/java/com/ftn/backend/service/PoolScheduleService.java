package com.ftn.backend.service;

import com.ftn.backend.dtos.schedule.CreatePoolScheduleDto;
import com.ftn.backend.dtos.schedule.PoolScheduleDto;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.Pool;
import com.ftn.backend.model.PoolSchedule;
import com.ftn.backend.repository.PoolRepository;
import com.ftn.backend.repository.PoolScheduleRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PoolScheduleService {

    private final PoolScheduleRepository scheduleRepository;
    private final PoolRepository poolRepository;

    @Transactional(readOnly = true)
    public List<PoolScheduleDto> getByPool(Long poolId) {
        return scheduleRepository.findByPool_IdAndDeletedAtIsNull(poolId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public PoolScheduleDto create(CreatePoolScheduleDto dto) {
        Pool pool = poolRepository
                .findByIdAndDeletedAtIsNull(dto.getPoolId())
                .orElseThrow(() -> new ResourceNotFoundException("Pool not found"));
        PoolSchedule schedule = PoolSchedule.builder()
                .pool(pool)
                .purpose(dto.getPurpose())
                .startDateTime(dto.getStartDateTime())
                .endDateTime(dto.getEndDateTime())
                .build();
        return toDto(scheduleRepository.save(schedule));
    }

    @Transactional
    public void delete(Long id) {
        PoolSchedule schedule = scheduleRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));
        schedule.setDeletedAt(LocalDateTime.now());
        scheduleRepository.save(schedule);
    }

    public PoolScheduleDto toDto(PoolSchedule s) {
        return PoolScheduleDto.builder()
                .id(s.getId())
                .poolId(s.getPool().getId())
                .poolNom(s.getPool().getNom())
                .purpose(s.getPurpose())
                .startDateTime(s.getStartDateTime())
                .endDateTime(s.getEndDateTime())
                .status(s.getStatus())
                .createdAt(s.getCreatedAt())
                .build();
    }
}
