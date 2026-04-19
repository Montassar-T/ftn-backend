package com.carServices.backend.service;

import com.carServices.backend.dtos.ClientDto;
import com.carServices.backend.dtos.NewClientDto;
import com.carServices.backend.dtos.PageDto;
import com.carServices.backend.enums.ActivityLogAction;
import com.carServices.backend.exception.business.ResourceNotFoundException;
import com.carServices.backend.model.Client;
import com.carServices.backend.repository.ClientRepository;
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
public class ClientService {

    private final ClientRepository clientRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public PageDto<ClientDto> getClients(Map<String, String> params) {
        JpaQueryFilters<Client> filters = new JpaQueryFilters<>(params, Client.class);
        Page<Client> page = clientRepository.findAll(filters.getSpecification(), filters.getPageable());

        List<ClientDto> filteredClients = page.stream()
                .map(clients -> this.modelMapper.map(clients, ClientDto.class))
                .toList();

        return PageDto.<ClientDto>builder()
                .data(filteredClients)
                .total(page.getTotalElements())
                .build();
    }

    @Transactional(readOnly = true)
    public ClientDto getClientById(Long id) {
        Client client = clientRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        return modelMapper.map(client, ClientDto.class);
    }

    @Transactional
    @TrackActivity(action = ActivityLogAction.CLIENT_CREATED, entityType = "CLIENT")
    public ClientDto createClient(NewClientDto dto) {

        Client client = modelMapper.map(dto, Client.class);

        Client saved = clientRepository.save(client);

        return modelMapper.map(saved, ClientDto.class);
    }

    @Transactional
    @TrackActivity(action = ActivityLogAction.CLIENT_UPDATED, entityType = "CLIENT")
    public ClientDto updateClient(Long id, NewClientDto dto) {

        Client existing = clientRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        modelMapper.map(dto, existing);

        Client updated = clientRepository.save(existing);

        return modelMapper.map(updated, ClientDto.class);
    }

    @Transactional
    @TrackActivity(action = ActivityLogAction.CLIENT_DELETED, entityType = "CLIENT")
    public void deleteClient(Long id) {

        Client client =
                clientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        client.setDeletedAt(new Date());

        clientRepository.save(client);
    }
}
