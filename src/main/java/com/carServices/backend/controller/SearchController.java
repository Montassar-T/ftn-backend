package com.carServices.backend.controller;

import com.carServices.backend.dtos.GlobalSearchDto;
import com.carServices.backend.dtos.SingleResultDto;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
public class SearchController {

    @CrossOrigin(origins = "http://localhost:5172")
    @GetMapping
    public ResponseEntity<SingleResultDto<GlobalSearchDto>> search(@RequestParam String query) {
        GlobalSearchDto globalSearch =
                GlobalSearchDto.builder().clients(new ArrayList<>()).build();

        return ResponseEntity.ok(
                SingleResultDto.<GlobalSearchDto>builder().data(globalSearch).build());
    }
}
