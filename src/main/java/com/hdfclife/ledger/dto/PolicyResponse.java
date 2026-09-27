package com.hdfclife.ledger.dto;

import java.util.List;

public record PolicyResponse(
        String policyNo,
        String customer,
        String email,
        String type,
        int basePremium,
        String status,
        List<String> riderCodes
) {
}
