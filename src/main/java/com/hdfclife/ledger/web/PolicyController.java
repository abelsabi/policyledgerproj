package com.hdfclife.ledger.web;

import com.hdfclife.ledger.dto.ClaimResponse;
import com.hdfclife.ledger.dto.CreatePolicyRequest;
import com.hdfclife.ledger.dto.PolicyResponse;
import com.hdfclife.ledger.service.ClaimService;
import com.hdfclife.ledger.service.PolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/policies")
@Tag(name = "Policies", description = "HDFC Life policy operations")
public class PolicyController {

    private final PolicyService policyService;
    private final ClaimService claimService;

    public PolicyController(PolicyService policyService, ClaimService claimService) {
        this.policyService = policyService;
        this.claimService = claimService;
    }

    @GetMapping
    @Operation(summary = "List policies, optionally filtered by status, type, or customer")
    @ApiResponse(responseCode = "200", description = "Policies returned")
    public List<PolicyResponse> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String customer
    ) {
        return policyService.list(status, type, customer);
    }

    @GetMapping("/search")
    @Operation(summary = "Find policies with base premium at least minPremium")
    @ApiResponse(responseCode = "200", description = "Matching policies")
    @ApiResponse(responseCode = "400", description = "Invalid minPremium")
    public List<PolicyResponse> search(@RequestParam(required = false) Integer minPremium) {
        return policyService.searchByMinPremium(minPremium);
    }

    @GetMapping("/{policyNo}")
    @Operation(summary = "Get a policy by policy number")
    @ApiResponse(responseCode = "200", description = "Policy found")
    @ApiResponse(responseCode = "404", description = "Policy not found")
    public PolicyResponse get(@PathVariable String policyNo) {
        return policyService.getByPolicyNo(policyNo);
    }

    @PostMapping
    @Operation(summary = "Create a policy, inserting a customer when the email is new")
    @ApiResponse(responseCode = "201", description = "Policy created")
    @ApiResponse(responseCode = "400", description = "Validation failed")
    @ApiResponse(responseCode = "409", description = "Duplicate policy number")
    public ResponseEntity<PolicyResponse> create(@Valid @RequestBody CreatePolicyRequest request) {
        PolicyResponse created = policyService.create(request);
        return ResponseEntity
                .created(URI.create("/api/policies/" + created.policyNo()))
                .body(created);
    }

    @DeleteMapping("/{policyNo}")
    @Operation(summary = "Delete a policy")
    @ApiResponse(responseCode = "204", description = "Policy deleted")
    @ApiResponse(responseCode = "404", description = "Policy not found")
    public ResponseEntity<Void> delete(@PathVariable String policyNo) {
        policyService.delete(policyNo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{policyNo}/claims")
    @Operation(summary = "List claims for a policy")
    @ApiResponse(responseCode = "200", description = "Claims returned")
    @ApiResponse(responseCode = "404", description = "Policy not found")
    public List<ClaimResponse> claims(@PathVariable String policyNo) {
        return claimService.listByPolicyNo(policyNo);
    }
}
