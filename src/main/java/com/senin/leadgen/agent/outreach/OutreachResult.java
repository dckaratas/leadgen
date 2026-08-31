package com.senin.leadgen.agent.outreach;

public record OutreachResult(
        boolean sent,
        String recipientEmail,
        String failureReason // sent=false ise doldurulur
) {
}
