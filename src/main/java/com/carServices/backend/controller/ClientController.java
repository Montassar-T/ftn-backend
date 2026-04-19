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
    public ResponseEntity<PageDto<ClientDto>> getClients(@RequestParam Map<String, String> params) {

        return ResponseEntity.ok(clientService.getClients(params));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResultDto<ClientDto>> getClientById(@PathVariable Long id) {

        ClientDto result = clientService.getClientById(id);

        return ResponseEntity.ok(new SingleResultDto<>(result));
    }

    @PostMapping
    public ResponseEntity<SingleResultDto<ClientDto>> createClient(@RequestBody NewClientDto clientDto) {

        ClientDto result = clientService.createClient(clientDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(new SingleResultDto<>(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SingleResultDto<ClientDto>> updateClient(
            @PathVariable Long id, @RequestBody NewClientDto clientDto) {

        ClientDto result = clientService.updateClient(id, clientDto);

        return ResponseEntity.ok(new SingleResultDto<>(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<InformativeMessage> deleteClient(@PathVariable Long id) {

        clientService.deleteClient(id);

        return ResponseEntity.ok(new InformativeMessage("Client deleted successfully"));
    }
}
