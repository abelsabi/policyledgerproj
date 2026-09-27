package com.hdfclife.ledger.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "policies")
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "policy_no", nullable = false, unique = true)
    private String policyNo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "product_type", nullable = false)
    private String productType;

    @Column(name = "base_premium", nullable = false)
    private Integer basePremium;

    @Column(nullable = false)
    private String status;

    @OneToMany(mappedBy = "policy")
    private List<Claim> claims = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "policy_riders",
            joinColumns = @JoinColumn(name = "policy_id"),
            inverseJoinColumns = @JoinColumn(name = "rider_id")
    )
    private Set<Rider> riders = new HashSet<>();

    public Policy() {
    }

    public Policy(String policyNo, Customer customer, String productType, Integer basePremium, String status) {
        this.policyNo = policyNo;
        this.customer = customer;
        this.productType = productType;
        this.basePremium = basePremium;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getPolicyNo() {
        return policyNo;
    }

    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Integer getBasePremium() {
        return basePremium;
    }

    public void setBasePremium(Integer basePremium) {
        this.basePremium = basePremium;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Claim> getClaims() {
        return claims;
    }

    public Set<Rider> getRiders() {
        return riders;
    }
}
