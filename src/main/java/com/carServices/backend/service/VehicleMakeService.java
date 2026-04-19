package com.carServices.backend.service;

import com.carServices.backend.dtos.*;
import com.carServices.backend.enums.ActivityLogAction;
import com.carServices.backend.exception.business.*;
import com.carServices.backend.model.VehicleMake;
import com.carServices.backend.repository.VehicleMakeRepository;
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
public class VehicleMakeService {

    private final VehicleMakeRepository vehicleMakeRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public PageDto<VehicleMakeDto> getAllMakes(Map<String, String> params) {
        JpaQueryFilters<VehicleMake> filters = new JpaQueryFilters<>(params, VehicleMake.class);
        Page<VehicleMake> page = vehicleMakeRepository.findAll(filters.getSpecification(), filters.getPageable());

        List<VehicleMakeDto> data = page.stream()
                .map(vehicle -> modelMapper.map(vehicle, VehicleMakeDto.class))
                .toList();

        return PageDto.<VehicleMakeDto>builder()
                .data(data)
                .total(page.getTotalElements())
                .build();
    }

    @Transactional(readOnly = true)
    public VehicleMakeDto getMakeById(Long id) {

        VehicleMake make =
                vehicleMakeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Make not found"));

        return modelMapper.map(make, VehicleMakeDto.class);
    }

    @Transactional
    @TrackActivity(action = ActivityLogAction.VEHICLE_MAKE_CREATED, entityType = "VEHICLE_MAKE")
    public VehicleMakeDto createMake(NewVehicleMakeDto dto) {

        VehicleMake make = VehicleMake.builder().name(dto.getName()).build();

        VehicleMake saved = vehicleMakeRepository.save(make);

        return modelMapper.map(saved, VehicleMakeDto.class);
    }

    @Transactional
    @TrackActivity(action = ActivityLogAction.VEHICLE_MAKE_UPDATED, entityType = "VEHICLE_MAKE")
    public VehicleMakeDto updateMake(Long id, NewVehicleMakeDto dto) {

        VehicleMake make =
                vehicleMakeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Make not found"));

        make.setName(dto.getName());

        return modelMapper.map(vehicleMakeRepository.save(make), VehicleMakeDto.class);
    }

    @Transactional
    @TrackActivity(action = ActivityLogAction.VEHICLE_MAKE_DELETED, entityType = "VEHICLE_MAKE")
    public void deleteMake(Long id) {

        VehicleMake make =
                vehicleMakeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Make not found"));

        if (Boolean.TRUE.equals(make.getSystemAttribute())) {
            throw new ConflictException("System models cannot be deleted");
        }

        make.setDeletedAt(new Date());

        vehicleMakeRepository.save(make);
    }
}
