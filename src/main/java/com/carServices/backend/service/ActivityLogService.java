package com.carServices.backend.service;

import com.carServices.backend.dtos.*;
import com.carServices.backend.model.*;
import com.carServices.backend.repository.ActivityLogRepository;
import com.carServices.backend.utils.JpaQueryFilters;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public PageDto<ActivityLogDto> getLogs(Map<String, String> params) {
        JpaQueryFilters<ActivityLog> filters = new JpaQueryFilters<>(params, ActivityLog.class);
        Page<ActivityLog> page = activityLogRepository.findAll(filters.getSpecification(), filters.getPageable());

        List<ActivityLogDto> filteredLogs = page.stream()
                .map(logs -> this.modelMapper.map(logs, ActivityLogDto.class))
                .toList();

        return PageDto.<ActivityLogDto>builder()
                .data(filteredLogs)
                .total(page.getTotalElements())
                .build();
    }
}
