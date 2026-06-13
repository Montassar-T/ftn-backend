package com.ftn.backend.controller;

import com.ftn.backend.dtos.SingleResultDto;
import com.ftn.backend.dtos.event.CreateEventDto;
import com.ftn.backend.dtos.event.EventDto;
import com.ftn.backend.dtos.event.UpdateEventDto;
import com.ftn.backend.service.EventService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Competition event management APIs")
public class EventController {

    private final EventService eventService;

    @GetMapping("/events/competition/{competitionId}")
    public ResponseEntity<SingleResultDto<List<EventDto>>> getByCompetition(@PathVariable Long competitionId) {
        return ResponseEntity.ok(new SingleResultDto<>(eventService.getByCompetition(competitionId)));
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<SingleResultDto<EventDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(eventService.getById(id)));
    }

    @PostMapping("/events")
    public ResponseEntity<SingleResultDto<EventDto>> create(@Valid @RequestBody CreateEventDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(eventService.create(dto)));
    }

    @PutMapping("/events/{id}")
    public ResponseEntity<SingleResultDto<EventDto>> update(@PathVariable Long id, @RequestBody UpdateEventDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(eventService.update(id, dto)));
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
