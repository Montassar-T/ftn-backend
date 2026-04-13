package com.carServices.backend.controller;

import com.carServices.backend.dtos.ClientDto;
import com.carServices.backend.dtos.InformativeMessage;
import com.carServices.backend.dtos.NewClientDto;
import com.carServices.backend.dtos.PageDto;
import com.carServices.backend.service.ClientService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/clients")
@Tag(name = "Client", description = "Client APIs")
public class ClientController {
    private final ClientService clientService;

    @GetMapping("")
    public ResponseEntity<PageDto<ClientDto>> getClients(
            @Parameter(
                            name = "params",
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "object", implementation = ClientDto.class),
                            style = ParameterStyle.FORM,
                            explode = Explode.TRUE)
                    @RequestParam
                    Map<String, String> params) {
        return clientService.getClients(params);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> getClientById(@PathVariable Long id) {
        return clientService.getClientById(id);
    }

    @PostMapping
    public ResponseEntity<ClientDto> createClient(@RequestBody NewClientDto clientDto) {
        return clientService.createClient(clientDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientDto> updateClient(@PathVariable Long id, @RequestBody NewClientDto clientDto) {
        return clientService.updateClient(id, clientDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<InformativeMessage> deleteClient(@PathVariable Long id) {
        return clientService.deleteClient(id);
    }
}
