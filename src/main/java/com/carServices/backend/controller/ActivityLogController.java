package com.carServices.backend.controller;

import com.carServices.backend.dtos.*;
import com.carServices.backend.service.ActivityLogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/activity-logs")
@Tag(name = "Activity Log", description = "Activity Logs APIs")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService service;

    @GetMapping
    public ResponseEntity<PageDto<ActivityLogDto>> getAll(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(service.getLogs(params));
    }
}
