package com.hdfclife.ledger.dto;

import com.hdfclife.ledger.validation.PolicyTypeConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreatePolicyRequest(
        @NotBlank
        @Pattern(regexp = "HDFC-LIFE-[0-9]{4}")
        String policyNo,

        @NotBlank
        @Size(min = 2, max = 120)
        String customer,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Pattern(regexp = "TERM|ULIP|ENDOWMENT")
        @PolicyTypeConstraint
        String type,

        @NotNull
        @Min(1)
        Integer basePremium,

        @NotBlank
        @Pattern(regexp = "Active|Lapsed|Pending")
        String status
) {
}
