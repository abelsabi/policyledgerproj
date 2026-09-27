package com.hdfclife.ledger.service;

import com.hdfclife.ledger.domain.Customer;
import com.hdfclife.ledger.domain.Policy;
import com.hdfclife.ledger.domain.Rider;
import com.hdfclife.ledger.dto.CreatePolicyRequest;
import com.hdfclife.ledger.dto.PolicyResponse;
import com.hdfclife.ledger.exception.DuplicatePolicyException;
import com.hdfclife.ledger.exception.InvalidRequestException;
import com.hdfclife.ledger.exception.PolicyNotFoundException;
import com.hdfclife.ledger.repo.ClaimRepository;
import com.hdfclife.ledger.repo.CustomerRepository;
import com.hdfclife.ledger.repo.PolicyRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final CustomerRepository customerRepository;
    private final ClaimRepository claimRepository;

    public PolicyService(
            PolicyRepository policyRepository,
            CustomerRepository customerRepository,
            ClaimRepository claimRepository
    ) {
        this.policyRepository = policyRepository;
        this.customerRepository = customerRepository;
        this.claimRepository = claimRepository;
    }

    @Transactional(readOnly = true)
    public List<PolicyResponse> list(String status, String type, String customer) {
        List<Policy> policies;
        if (status != null && !status.isBlank()) {
            policies = policyRepository.findByStatusOrderByPolicyNoAsc(status);
        } else if (type != null && !type.isBlank()) {
            policies = policyRepository.findByProductTypeOrderByPolicyNoAsc(type);
        } else if (customer != null && !customer.isBlank()) {
            policies = policyRepository.findByCustomer_FullNameOrderByPolicyNoAsc(customer);
        } else {
            policies = policyRepository.findAll(Sort.by(Sort.Direction.ASC, "policyNo"));
        }
        return policies.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PolicyResponse getByPolicyNo(String policyNo) {
        Policy policy = policyRepository.findByPolicyNo(policyNo)
                .orElseThrow(() -> new PolicyNotFoundException(policyNo));
        return toResponse(policy);
    }

    @Transactional(readOnly = true)
    public List<PolicyResponse> searchByMinPremium(Integer minPremium) {
        if (minPremium == null || minPremium < 0) {
            throw new InvalidRequestException("minPremium is required and must be >= 0");
        }
        return policyRepository.findWithPremiumAtLeast(minPremium).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public PolicyResponse create(CreatePolicyRequest request) {
        if (policyRepository.existsByPolicyNo(request.policyNo())) {
            throw new DuplicatePolicyException(request.policyNo());
        }
        Customer customer = customerRepository.findByEmail(request.email())
                .orElseGet(() -> customerRepository.save(
                        new Customer(request.customer(), request.email())));
        Policy policy = new Policy(
                request.policyNo(),
                customer,
                request.type(),
                request.basePremium(),
                request.status()
        );
        return toResponse(policyRepository.save(policy));
    }

    @Transactional
    public void delete(String policyNo) {
        Policy policy = policyRepository.findByPolicyNo(policyNo)
                .orElseThrow(() -> new PolicyNotFoundException(policyNo));
        claimRepository.findByPolicy_PolicyNoOrderByClaimNoAsc(policyNo)
                .forEach(claimRepository::delete);
        policy.getRiders().clear();
        policyRepository.delete(policy);
    }

    @Transactional(readOnly = true)
    public long count() {
        return policyRepository.count();
    }

    public PolicyResponse toResponse(Policy policy) {
        List<String> riderCodes = policy.getRiders().stream()
                .map(Rider::getCode)
                .sorted()
                .toList();
        return new PolicyResponse(
                policy.getPolicyNo(),
                policy.getCustomer().getFullName(),
                policy.getCustomer().getEmail(),
                policy.getProductType(),
                policy.getBasePremium(),
                policy.getStatus(),
                riderCodes
        );
    }
}
