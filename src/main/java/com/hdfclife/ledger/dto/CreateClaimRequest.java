package com.hdfclife.ledger.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateClaimRequest(
        @NotBlank
        @Pattern(regexp = "HDFC-LIFE-[0-9]{4}")
        String policyNo,

        @NotNull
        @Min(1)
        @Max(500000)
        Integer claimAmount,

        @NotBlank
        @Pattern(regexp = "HIGH|MEDIUM|LOW")
        String urgency,

        @Size(max = 120)
        String hospitalName
) {
}
