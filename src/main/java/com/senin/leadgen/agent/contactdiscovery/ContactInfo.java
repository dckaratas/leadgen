package com.senin.leadgen.agent.contactdiscovery;

public record ContactInfo(
        ContactChannel channel,
        String phoneNumber   // channel == NONE ise null
) {
}