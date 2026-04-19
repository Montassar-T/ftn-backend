package com.carServices.backend.service;

import com.carServices.backend.dtos.*;
import com.carServices.backend.enums.ActivityLogAction;
import com.carServices.backend.exception.business.ResourceNotFoundException;
import com.carServices.backend.model.Repair;
import com.carServices.backend.repository.RepairRepository;
import com.carServices.backend.security.aop.TrackActivity;
import com.carServices.backend.utils.JpaQueryFilters;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RepairService {

    private final RepairRepository repairRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public PageDto<RepairDto> getRepairs(Map<String, String> params) {

        JpaQueryFilters<Repair> filters = new JpaQueryFilters<>(params, Repair.class);

        Page<Repair> page = repairRepository.findAll(filters.getSpecification(), filters.getPageable());

        List<RepairDto> data =
                page.stream().map(r -> modelMapper.map(r, RepairDto.class)).toList();

        return PageDto.<RepairDto>builder()
                .data(data)
                .total(page.getTotalElements())
                .build();
    }

    @Transactional(readOnly = true)
    public RepairDto getRepairById(Long id) {

        Repair repair = repairRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repair not found"));

        return modelMapper.map(repair, RepairDto.class);
    }

    @Transactional
    @TrackActivity(action = ActivityLogAction.REPAIR_CREATED, entityType = "REPAIR")
    public RepairDto createRepair(NewRepairDto dto) {

        Repair repair = modelMapper.map(dto, Repair.class);

        Repair saved = repairRepository.save(repair);

        return modelMapper.map(saved, RepairDto.class);
    }

    @Transactional
    @TrackActivity(action = ActivityLogAction.REPAIR_UPDATED, entityType = "REPAIR")
    public RepairDto updateRepair(Long id, NewRepairDto dto) {

        Repair existing = repairRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repair not found"));

        modelMapper.map(dto, existing);

        Repair updated = repairRepository.save(existing);

        return modelMapper.map(updated, RepairDto.class);
    }

    @Transactional
    @TrackActivity(action = ActivityLogAction.REPAIR_DELETED, entityType = "REPAIR")
    public void deleteRepair(Long id) {

        Repair repair = repairRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repair not found"));

        repair.setDeletedAt(new Date());

        repairRepository.save(repair);
    }
}
