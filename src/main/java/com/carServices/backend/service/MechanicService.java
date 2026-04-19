package com.carServices.backend.service;

import com.carServices.backend.dtos.*;
import com.carServices.backend.enums.ActivityLogAction;
import com.carServices.backend.exception.business.ResourceNotFoundException;
import com.carServices.backend.model.Mechanic;
import com.carServices.backend.repository.MechanicRepository;
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
public class MechanicService {

    private final MechanicRepository mechanicRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public PageDto<MechanicDto> getMechanics(Map<String, String> params) {

        JpaQueryFilters<Mechanic> filters = new JpaQueryFilters<>(params, Mechanic.class);
        Page<Mechanic> page = mechanicRepository.findAll(filters.getSpecification(), filters.getPageable());

        List<MechanicDto> data =
                page.stream().map(m -> modelMapper.map(m, MechanicDto.class)).toList();

        return PageDto.<MechanicDto>builder()
                .data(data)
                .total(page.getTotalElements())
                .build();
    }

    @Transactional(readOnly = true)
    public MechanicDto getMechanicById(Long id) {

        Mechanic mechanic = mechanicRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mechanic not found"));

        return modelMapper.map(mechanic, MechanicDto.class);
    }

    @Transactional
    @TrackActivity(action = ActivityLogAction.MECHANIC_CREATED, entityType = "MECHANIC")
    public MechanicDto createMechanic(NewMechanicDto dto) {

        Mechanic saved = mechanicRepository.save(modelMapper.map(dto, Mechanic.class));

        return modelMapper.map(saved, MechanicDto.class);
    }

    @Transactional
    @TrackActivity(action = ActivityLogAction.MECHANIC_UPDATED, entityType = "MECHANIC")
    public MechanicDto updateMechanic(Long id, NewMechanicDto dto) {

        Mechanic existing = mechanicRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mechanic not found"));

        modelMapper.map(dto, existing);

        return modelMapper.map(mechanicRepository.save(existing), MechanicDto.class);
    }

    @Transactional
    @TrackActivity(action = ActivityLogAction.MECHANIC_DELETED, entityType = "MECHANIC")
    public void deleteMechanic(Long id) {

        Mechanic mechanic = mechanicRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mechanic not found"));

        mechanic.setDeletedAt(new Date());

        mechanicRepository.save(mechanic);
    }
}
