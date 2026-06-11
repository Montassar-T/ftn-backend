package com.ftn.backend.controller;

import com.ftn.backend.dtos.SingleResultDto;
import com.ftn.backend.dtos.membership.ClubMembershipDto;
import com.ftn.backend.dtos.membership.CreateClubMembershipDto;
import com.ftn.backend.dtos.membership.UpdateClubMembershipDto;
import com.ftn.backend.service.ClubMembershipService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "ClubMemberships", description = "Club membership management APIs")
public class ClubMembershipController {

    private final ClubMembershipService membershipService;

    @GetMapping("/clubs/{clubId}/members")
    public ResponseEntity<SingleResultDto<List<ClubMembershipDto>>> getByClub(@PathVariable Long clubId) {
        return ResponseEntity.ok(new SingleResultDto<>(membershipService.getByClub(clubId)));
    }

    @PostMapping("/club-memberships")
    public ResponseEntity<SingleResultDto<ClubMembershipDto>> create(@Valid @RequestBody CreateClubMembershipDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(membershipService.create(dto)));
    }

    @PutMapping("/club-memberships/{id}")
    public ResponseEntity<SingleResultDto<ClubMembershipDto>> update(
            @PathVariable Long id, @RequestBody UpdateClubMembershipDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(membershipService.update(id, dto)));
    }

    @DeleteMapping("/club-memberships/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        membershipService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
