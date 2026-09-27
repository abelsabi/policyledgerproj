package com.hdfclife.ledger.web;

import com.hdfclife.ledger.dto.ClaimResponse;
import com.hdfclife.ledger.dto.CreateClaimRequest;
import com.hdfclife.ledger.service.ClaimService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/claims")
@Tag(name = "Claims", description = "HDFC Life claim operations")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping
    @Operation(summary = "Submit a claim against an existing policy")
    @ApiResponse(responseCode = "201", description = "Claim created")
    @ApiResponse(responseCode = "400", description = "Validation failed")
    @ApiResponse(responseCode = "404", description = "Policy not found")
    public ResponseEntity<ClaimResponse> create(@Valid @RequestBody CreateClaimRequest request) {
        ClaimResponse created = claimService.create(request);
        return ResponseEntity
                .created(URI.create("/api/claims/" + created.claimNo()))
                .body(created);
    }

    @GetMapping("/{claimNo}")
    @Operation(summary = "Get a claim by claim number")
    @ApiResponse(responseCode = "200", description = "Claim found")
    @ApiResponse(responseCode = "404", description = "Claim not found")
    public ClaimResponse get(@PathVariable String claimNo) {
        return claimService.getByClaimNo(claimNo);
    }
}
