package com.carServices.backend.service;

import com.carServices.backend.dtos.*;
import com.carServices.backend.enums.ActivityLogAction;
import com.carServices.backend.exception.business.ConflictException;
import com.carServices.backend.exception.business.ResourceNotFoundException;
import com.carServices.backend.model.VehicleMake;
import com.carServices.backend.model.VehicleModel;
import com.carServices.backend.repository.VehicleMakeRepository;
import com.carServices.backend.repository.VehicleModelRepository;
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
public class VehicleModelService {

    private final VehicleModelRepository vehicleModelRepository;
    private final VehicleMakeRepository vehicleMakeRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public PageDto<VehicleModelDto> getAllModels(Map<String, String> params) {
        JpaQueryFilters<VehicleModel> filters = new JpaQueryFilters<>(params, VehicleModel.class);
        Page<VehicleModel> page = vehicleModelRepository.findAll(filters.getSpecification(), filters.getPageable());

        List<VehicleModelDto> data = page.stream()
                .map(vehicle -> modelMapper.map(vehicle, VehicleModelDto.class))
                .toList();

        return PageDto.<VehicleModelDto>builder()
                .data(data)
                .total(page.getTotalElements())
                .build();
    }

    @Transactional(readOnly = true)
    public VehicleModelDto getModelById(Long id) {

        VehicleModel model = vehicleModelRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Model not found"));

        return modelMapper.map(model, VehicleModelDto.class);
    }

    @Transactional(readOnly = true)
    public List<VehicleModelLiteDto> getModelsByMake(Long makeId) {

        List<VehicleModel> models = vehicleModelRepository.findByMakeIdAndDeletedAtIsNull(makeId);

        return models.stream()
                .map(m -> modelMapper.map(m, VehicleModelLiteDto.class))
                .toList();
    }

    @Transactional
    @TrackActivity(action = ActivityLogAction.VEHICLE_MODEL_CREATED, entityType = "VEHICLE_MODEL")
    public VehicleModelDto createModel(NewVehicleModelDto dto) {

        VehicleMake make = vehicleMakeRepository
                .findByIdAndDeletedAtIsNull(dto.getMakeId())
                .orElseThrow(() -> new ResourceNotFoundException("Make not found"));

        VehicleModel model =
                VehicleModel.builder().name(dto.getName()).make(make).build();

        VehicleModel saved = vehicleModelRepository.save(model);

        return modelMapper.map(saved, VehicleModelDto.class);
    }

    @Transactional
    @TrackActivity(action = ActivityLogAction.VEHICLE_MODEL_UPDATED, entityType = "VEHICLE_MODEL")
    public VehicleModelDto updateModel(Long id, NewVehicleModelDto dto) {

        VehicleModel model = vehicleModelRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Model not found"));

        VehicleMake make = vehicleMakeRepository
                .findByIdAndDeletedAtIsNull(dto.getMakeId())
                .orElseThrow(() -> new ResourceNotFoundException("Make not found"));

        model.setName(dto.getName());
        model.setMake(make);

        return modelMapper.map(vehicleModelRepository.save(model), VehicleModelDto.class);
    }

    @Transactional
    @TrackActivity(action = ActivityLogAction.VEHICLE_MODEL_DELETED, entityType = "VEHICLE_MODEL")
    public void deleteModel(Long id) {

        VehicleModel model = vehicleModelRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Model not found"));

        if (Boolean.TRUE.equals(model.getSystemAttribute())) {
            throw new ConflictException("System models cannot be deleted");
        }

        model.setDeletedAt(new Date());

        vehicleModelRepository.save(model);
    }
}
