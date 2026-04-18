package com.carServices.backend.service;

import com.carServices.backend.dtos.*;
import com.carServices.backend.exception.business.ResourceNotFoundException;
import com.carServices.backend.model.Mechanic;
import com.carServices.backend.repository.MechanicRepository;
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
public class MechanicService {

    private final MechanicRepository mechanicRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public ResponseEntity<PageDto<MechanicDto>> getMechanics(Map<String, String> params) {

        JpaQueryFilters<Mechanic> filters = new JpaQueryFilters<>(params, Mechanic.class);
        Page<Mechanic> page = mechanicRepository.findAll(filters.getSpecification(), filters.getPageable());

        List<MechanicDto> data =
                page.stream().map(m -> modelMapper.map(m, MechanicDto.class)).toList();

        return ResponseEntity.ok(PageDto.<MechanicDto>builder()
                .data(data)
                .total(page.getTotalElements())
                .build());
    }

    @Transactional(readOnly = true)
    public ResponseEntity<MechanicDto> getMechanicById(Long id) {

        Mechanic mechanic = mechanicRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mechanic not found"));

        return ResponseEntity.ok(modelMapper.map(mechanic, MechanicDto.class));
    }

    @Transactional
    public ResponseEntity<MechanicDto> createMechanic(NewMechanicDto dto) {

        Mechanic saved = mechanicRepository.save(modelMapper.map(dto, Mechanic.class));

        return ResponseEntity.ok(modelMapper.map(saved, MechanicDto.class));
    }

    @Transactional
    public ResponseEntity<MechanicDto> updateMechanic(Long id, NewMechanicDto dto) {

        Mechanic existing = mechanicRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mechanic not found"));

        modelMapper.map(dto, existing);

        return ResponseEntity.ok(modelMapper.map(mechanicRepository.save(existing), MechanicDto.class));
    }

    @Transactional
    public ResponseEntity<InformativeMessage> deleteMechanic(Long id) {

        Mechanic mechanic = mechanicRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mechanic not found"));

        mechanicRepository.save(mechanic);

        return ResponseEntity.ok(new InformativeMessage("Mechanic deleted successfully"));
    }
}
