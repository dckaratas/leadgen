package com.senin.leadgen.agent.websitecheck;

import com.senin.leadgen.agent.Agent;
import com.senin.leadgen.places.PlaceDto;
import com.senin.leadgen.places.WebsitePresence;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;

/**
 * Girdi: PlacesClient'tan gelen ham işletme kaydı.
 * Çıktı: bu işletmenin pipeline'da ilerlemesi gerekip gerekmediği kararı.
 *
 * TODO (core logic):
 *  - hasWebsite() false ise devam kararı
 *  - true olsa bile "kalitesiz/eski site" tespiti gibi ek sezgisel kural eklemek
 *    istersen burası (örn. websiteUri sadece bir Instagram/Facebook linkiyse
 *    bunu "gerçek site yok" say)
 */
@Component
public class WebsiteCheckAgent implements Agent<PlaceDto, WebsiteCheckResult> {

    @Override
    public WebsiteCheckResult execute(PlaceDto input) {
        Objects.requireNonNull(input, "PlaceDto input null olamaz");

        WebsitePresence presence = classify(input.websiteUri());
        boolean needsWebsite = presence != WebsitePresence.HAS_WEBSITE;

        return needsWebsite
                ? new WebsiteCheckResult(input, true, presence, "Website presence: " + presence)
                : new WebsiteCheckResult(input, false, presence, "Website presence: " + presence);
    }

    private static final Set<String> SOCIAL_DOMAINS = Set.of(
            "facebook.com", "instagram.com", "linktr.ee", "wix.com" // vb.
    );

    private WebsitePresence classify(String websiteUri) {
        if (websiteUri == null || websiteUri.isBlank()) {
            return WebsitePresence.NONE;
        }
        boolean isSocialOnly = SOCIAL_DOMAINS.stream()
                .anyMatch(websiteUri::contains);
        return isSocialOnly ? WebsitePresence.SOCIAL_ONLY : WebsitePresence.HAS_WEBSITE;
    }
}
