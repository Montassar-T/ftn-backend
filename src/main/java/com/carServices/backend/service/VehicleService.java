package com.carServices.backend.service;

import com.carServices.backend.dtos.*;
import com.carServices.backend.enums.ActivityLogAction;
import com.carServices.backend.exception.business.ResourceNotFoundException;
import com.carServices.backend.model.*;
import com.carServices.backend.repository.*;
import com.carServices.backend.security.aop.TrackActivity;
import com.carServices.backend.utils.JpaQueryFilters;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final ClientRepository clientRepository;
    private final VehicleModelRepository vehicleModelRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public PageDto<VehicleDto> getVehicles(Map<String, String> params) {

        JpaQueryFilters<Vehicle> filters = new JpaQueryFilters<>(params, Vehicle.class);
        Page<Vehicle> page = vehicleRepository.findAll(filters.getSpecification(), filters.getPageable());

        List<VehicleDto> data = page.stream()
                .map(vehicle -> modelMapper.map(vehicle, VehicleDto.class))
                .toList();

        return PageDto.<VehicleDto>builder()
                .data(data)
                .total(page.getTotalElements())
                .build();
    }

    @Transactional(readOnly = true)
    public VehicleDto getVehicleById(Long id) {

        Vehicle vehicle = vehicleRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        return modelMapper.map(vehicle, VehicleDto.class);
    }

    @Transactional
    @TrackActivity(action = ActivityLogAction.VEHICLE_CREATED, entityType = "VEHICLE")
    public VehicleDto createVehicle(NewVehicleDto dto) {
        Vehicle vehicle = modelMapper.map(dto, Vehicle.class);

        buildBaseVehicle(vehicle, dto);

        Vehicle saved = vehicleRepository.save(vehicle);

        return modelMapper.map(saved, VehicleDto.class);
    }

    @Transactional
    @TrackActivity(action = ActivityLogAction.VEHICLE_UPDATED, entityType = "VEHICLE")
    public VehicleDto updateVehicle(Long id, NewVehicleDto dto) {

        Vehicle existing = vehicleRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        modelMapper.map(dto, existing);

        buildBaseVehicle(existing, dto);

        Vehicle updated = vehicleRepository.save(existing);

        return modelMapper.map(updated, VehicleDto.class);
    }

    @Transactional
    @TrackActivity(action = ActivityLogAction.VEHICLE_DELETED, entityType = "VEHICLE")
    public void deleteVehicle(Long id) {

        Vehicle vehicle = vehicleRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        vehicle.setDeletedAt(new Date());

        vehicleRepository.save(vehicle);
    }

    private void buildBaseVehicle(Vehicle vehicle, NewVehicleDto dto) {
        Client client = clientRepository
                .findByIdAndDeletedAtIsNull(dto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        VehicleModel model = vehicleModelRepository
                .findByIdAndDeletedAtIsNull(dto.getModelId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle model not found"));

        vehicle.setClient(client);
        vehicle.setModel(model);
    }
}
