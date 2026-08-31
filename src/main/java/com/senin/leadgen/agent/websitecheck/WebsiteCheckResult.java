package com.senin.leadgen.agent.websitecheck;

import com.senin.leadgen.places.PlaceDto;
import com.senin.leadgen.places.WebsitePresence;

public record WebsiteCheckResult(
        PlaceDto place,
        boolean needsWebsite,
        WebsitePresence  websitePresence,
        String reason // loglama/debug için: neden bu karar verildi
) {
}
