package com.hdfclife.ledger.exception;

public class ClaimNotFoundException extends DeskException {

    public ClaimNotFoundException(String claimNo) {
        super("Claim not found: " + claimNo);
    }
}
