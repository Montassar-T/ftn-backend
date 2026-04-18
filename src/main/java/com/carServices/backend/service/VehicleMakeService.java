package com.carServices.backend.service;

import com.carServices.backend.dtos.InformativeMessage;
import com.carServices.backend.dtos.PageDto;
import com.carServices.backend.dtos.VehicleMakeDto;
import com.carServices.backend.exception.business.ConflictException;
import com.carServices.backend.exception.business.ResourceNotFoundException;
import com.carServices.backend.model.VehicleMake;
import com.carServices.backend.repository.VehicleMakeRepository;
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
public class VehicleMakeService {

    private final VehicleMakeRepository vehicleMakeRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public ResponseEntity<PageDto<VehicleMakeDto>> getAllMakes(Map<String, String> params) {
        JpaQueryFilters<VehicleMake> filters = new JpaQueryFilters<>(params, VehicleMake.class);
        Page<VehicleMake> page = vehicleMakeRepository.findAll(filters.getSpecification(), filters.getPageable());

        List<VehicleMakeDto> data = page.stream()
                .map(vehicle -> modelMapper.map(vehicle, VehicleMakeDto.class))
                .toList();

        return ResponseEntity.ok(PageDto.<VehicleMakeDto>builder()
                .data(data)
                .total(page.getTotalElements())
                .build());
    }

    @Transactional(readOnly = true)
    public ResponseEntity<VehicleMakeDto> getMakeById(Long id) {

        VehicleMake make =
                vehicleMakeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Make not found"));

        return ResponseEntity.ok(modelMapper.map(make, VehicleMakeDto.class));
    }

    @Transactional
    public ResponseEntity<VehicleMakeDto> createMake(VehicleMakeDto dto) {

        VehicleMake make = VehicleMake.builder().name(dto.getName()).build();

        VehicleMake saved = vehicleMakeRepository.save(make);

        return ResponseEntity.ok(modelMapper.map(saved, VehicleMakeDto.class));
    }

    @Transactional
    public ResponseEntity<VehicleMakeDto> updateMake(Long id, VehicleMakeDto dto) {

        VehicleMake make =
                vehicleMakeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Make not found"));

        make.setName(dto.getName());

        return ResponseEntity.ok(modelMapper.map(vehicleMakeRepository.save(make), VehicleMakeDto.class));
    }

    @Transactional
    public ResponseEntity<InformativeMessage> deleteMake(Long id) {

        VehicleMake make =
                vehicleMakeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Make not found"));

        if (Boolean.TRUE.equals(make.getSystemAttribute())) {
            throw new ConflictException("System models cannot be deleted");
        }

        vehicleMakeRepository.delete(make);

        return ResponseEntity.ok(new InformativeMessage("Make deleted successfully"));
    }
}
