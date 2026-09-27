package com.hdfclife.ledger.exception;

public class PolicyNotFoundException extends DeskException {

    public PolicyNotFoundException(String policyNo) {
        super("Policy not found: " + policyNo);
    }
}
