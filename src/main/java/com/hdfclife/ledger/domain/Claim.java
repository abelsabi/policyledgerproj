package com.hdfclife.ledger.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "claims")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "claim_no", nullable = false, unique = true)
    private String claimNo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private String urgency;

    @Column(nullable = false)
    private String status;

    public Claim() {
    }

    public Claim(String claimNo, Policy policy, Integer amount, String urgency, String status) {
        this.claimNo = claimNo;
        this.policy = policy;
        this.amount = amount;
        this.urgency = urgency;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getClaimNo() {
        return claimNo;
    }

    public void setClaimNo(String claimNo) {
        this.claimNo = claimNo;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
