package com.carServices.backend.service;

import com.carServices.backend.dtos.*;
import com.carServices.backend.repository.*;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ClientRepository clientRepository;
    private final VehicleRepository vehicleRepository;
    private final MechanicRepository mechanicRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public GlobalSearchDto search(String query) {

        String q = query == null ? "" : query.trim();

        if (q.isEmpty()) {
            return GlobalSearchDto.builder()
                    .clients(List.of())
                    .vehicles(List.of())
                    .mechanics(List.of())
                    .build();
        }

        List<ClientDto> clients = clientRepository.search(q).stream()
                .limit(5)
                .map(c -> modelMapper.map(c, ClientDto.class))
                .toList();

        List<VehicleDto> vehicles = vehicleRepository.search(q).stream()
                .limit(5)
                .map(v -> modelMapper.map(v, VehicleDto.class))
                .toList();

        List<MechanicDto> mechanics = mechanicRepository.search(q).stream()
                .limit(5)
                .map(m -> modelMapper.map(m, MechanicDto.class))
                .toList();

        return GlobalSearchDto.builder()
                .clients(clients)
                .vehicles(vehicles)
                .mechanics(mechanics)
                .build();
    }
}
