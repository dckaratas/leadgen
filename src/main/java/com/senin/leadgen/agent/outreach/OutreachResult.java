package com.senin.leadgen.agent.outreach;

public record OutreachResult(
        boolean linkGenerated,   // eski "sent" -> artık "link üretilebildi mi"
        String contactLink,
        String failureReason // sent=false ise doldurulur
) {
}
