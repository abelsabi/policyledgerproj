package com.hdfclife.ledger.repo;

import com.hdfclife.ledger.domain.Rider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RiderRepository extends JpaRepository<Rider, Long> {

    Optional<Rider> findByCode(String code);
}
