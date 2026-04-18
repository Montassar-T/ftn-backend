package com.carServices.backend.controller;

import com.carServices.backend.dtos.*;
import com.carServices.backend.service.ClientService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/clients")
@Tag(name = "Client", description = "Client APIs")
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public ResponseEntity<SingleResultDto<PageDto<ClientDto>>> getClients(@RequestParam Map<String, String> params) {

        PageDto<ClientDto> result = clientService.getClients(params).getBody();

        return ResponseEntity.ok(new SingleResultDto<>(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResultDto<ClientDto>> getClientById(@PathVariable Long id) {

        ClientDto result = clientService.getClientById(id).getBody();

        return ResponseEntity.ok(new SingleResultDto<>(result));
    }

    @PostMapping
    public ResponseEntity<SingleResultDto<ClientDto>> createClient(@RequestBody NewClientDto clientDto) {

        ClientDto result = clientService.createClient(clientDto).getBody();

        return ResponseEntity.status(HttpStatus.CREATED).body(new SingleResultDto<>(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SingleResultDto<ClientDto>> updateClient(
            @PathVariable Long id, @RequestBody NewClientDto clientDto) {

        ClientDto result = clientService.updateClient(id, clientDto).getBody();

        return ResponseEntity.ok(new SingleResultDto<>(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SingleResultDto<InformativeMessage>> deleteClient(@PathVariable Long id) {

        InformativeMessage result = clientService.deleteClient(id).getBody();

        return ResponseEntity.ok(new SingleResultDto<>(result));
    }
}
