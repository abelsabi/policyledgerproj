package com.hdfclife.ledger.service;

import com.hdfclife.ledger.domain.Claim;
import com.hdfclife.ledger.domain.Policy;
import com.hdfclife.ledger.dto.ClaimResponse;
import com.hdfclife.ledger.dto.CreateClaimRequest;
import com.hdfclife.ledger.exception.ClaimNotFoundException;
import com.hdfclife.ledger.exception.PolicyNotFoundException;
import com.hdfclife.ledger.repo.ClaimRepository;
import com.hdfclife.ledger.repo.PolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final PolicyRepository policyRepository;

    public ClaimService(ClaimRepository claimRepository, PolicyRepository policyRepository) {
        this.claimRepository = claimRepository;
        this.policyRepository = policyRepository;
    }

    @Transactional(readOnly = true)
    public List<ClaimResponse> listByPolicyNo(String policyNo) {
        if (!policyRepository.existsByPolicyNo(policyNo)) {
            throw new PolicyNotFoundException(policyNo);
        }
        return claimRepository.findByPolicy_PolicyNoOrderByClaimNoAsc(policyNo).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClaimResponse getByClaimNo(String claimNo) {
        Claim claim = claimRepository.findByClaimNo(claimNo)
                .orElseThrow(() -> new ClaimNotFoundException(claimNo));
        return toResponse(claim);
    }

    @Transactional
    public ClaimResponse create(CreateClaimRequest request) {
        Policy policy = policyRepository.findByPolicyNo(request.policyNo())
                .orElseThrow(() -> new PolicyNotFoundException(request.policyNo()));
        String claimNo = nextClaimNo();
        Claim claim = new Claim(claimNo, policy, request.claimAmount(), request.urgency(), "SUBMITTED");
        return toResponse(claimRepository.save(claim));
    }

    private String nextClaimNo() {
        return String.format("CLM-%02d", claimRepository.count() + 1);
    }

    private ClaimResponse toResponse(Claim claim) {
        return new ClaimResponse(
                claim.getClaimNo(),
                claim.getPolicy().getPolicyNo(),
                claim.getAmount(),
                claim.getUrgency(),
                claim.getStatus()
        );
    }
}
