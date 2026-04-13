package com.carServices.backend.service;

import com.carServices.backend.dtos.InformativeMessage;
import com.carServices.backend.dtos.NewVehicleDto;
import com.carServices.backend.dtos.PageDto;
import com.carServices.backend.dtos.VehicleDto;
import com.carServices.backend.exception.ResourceNotFoundException;
import com.carServices.backend.model.Client;
import com.carServices.backend.model.Vehicle;
import com.carServices.backend.model.VehicleModel;
import com.carServices.backend.repository.ClientRepository;
import com.carServices.backend.repository.VehicleModelRepository;
import com.carServices.backend.repository.VehicleRepository;
import com.carServices.backend.utils.JpaQueryFilters;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<PageDto<VehicleDto>> getVehicles(Map<String, String> params) {

        JpaQueryFilters<Vehicle> filters = new JpaQueryFilters<>(params, Vehicle.class);
        Page<Vehicle> page = vehicleRepository.findAll(filters.getSpecification(), filters.getPageable());

        List<VehicleDto> data = page.stream()
                .map(vehicle -> modelMapper.map(vehicle, VehicleDto.class))
                .toList();

        return ResponseEntity.ok(PageDto.<VehicleDto>builder()
                .data(data)
                .total(page.getTotalElements())
                .build());
    }

    @Transactional(readOnly = true)
    public ResponseEntity<VehicleDto> getVehicleById(Long id) {

        Vehicle vehicle = vehicleRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        return ResponseEntity.ok(modelMapper.map(vehicle, VehicleDto.class));
    }

    @Transactional
    public ResponseEntity<VehicleDto> createVehicle(NewVehicleDto dto) {
        Vehicle vehicle = modelMapper.map(dto, Vehicle.class);

        buildBaseVehicle(vehicle, dto);

        Vehicle saved = vehicleRepository.save(vehicle);

        return ResponseEntity.ok(modelMapper.map(saved, VehicleDto.class));
    }

    @Transactional
    public ResponseEntity<VehicleDto> updateVehicle(Long id, NewVehicleDto dto) {

        Vehicle existing = vehicleRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        modelMapper.map(dto, existing);

        buildBaseVehicle(existing, dto);

        Vehicle updated = vehicleRepository.save(existing);

        return ResponseEntity.ok(modelMapper.map(updated, VehicleDto.class));
    }

    @Transactional
    public ResponseEntity<InformativeMessage> deleteVehicle(Long id) {

        Vehicle vehicle = vehicleRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        vehicleRepository.delete(vehicle);

        return ResponseEntity.ok(new InformativeMessage("Vehicle deleted successfully"));
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
