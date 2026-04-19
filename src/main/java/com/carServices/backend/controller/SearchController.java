package com.carServices.backend.controller;

import com.carServices.backend.dtos.GlobalSearchDto;
import com.carServices.backend.dtos.SingleResultDto;
import com.carServices.backend.service.SearchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
@Tag(name = "Search", description = "Global search API")
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<SingleResultDto<GlobalSearchDto>> search(@RequestParam String query) {

        GlobalSearchDto result = searchService.search(query);

        return ResponseEntity.ok(
                SingleResultDto.<GlobalSearchDto>builder().data(result).build());
    }
}
