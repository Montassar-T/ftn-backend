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
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/club-memberships")
    public ResponseEntity<SingleResultDto<ClubMembershipDto>> create(@Valid @RequestBody CreateClubMembershipDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(membershipService.create(dto)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/club-memberships/{id}")
    public ResponseEntity<SingleResultDto<ClubMembershipDto>> update(
            @PathVariable Long id, @RequestBody UpdateClubMembershipDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(membershipService.update(id, dto)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/club-memberships/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        membershipService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
