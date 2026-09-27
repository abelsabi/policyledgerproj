package com.hdfclife.ledger.repo;

import com.hdfclife.ledger.domain.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    Optional<Claim> findByClaimNo(String claimNo);

    List<Claim> findByPolicy_PolicyNoOrderByClaimNoAsc(String policyNo);
}
