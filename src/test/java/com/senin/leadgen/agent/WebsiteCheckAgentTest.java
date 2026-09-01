package com.senin.leadgen.agent;

import com.senin.leadgen.agent.websitecheck.WebsiteCheckAgent;
import com.senin.leadgen.agent.websitecheck.WebsiteCheckResult;
import com.senin.leadgen.places.PlaceDto;
import com.senin.leadgen.places.WebsitePresence;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WebsiteCheckAgentTest {

    private final WebsiteCheckAgent agent = new WebsiteCheckAgent();

    @Test
    void noWebsite_needsWebsiteIsTrue() {
        PlaceDto place = placeWithWebsite(null);

        WebsiteCheckResult result = agent.execute(place);

        assertThat(result.needsWebsite()).isTrue();
        assertThat(result.websitePresence()).isEqualTo(WebsitePresence.NONE);
    }

    @Test
    void socialMediaOnly_needsWebsiteIsTrue() {
        PlaceDto place = placeWithWebsite("https://instagram.com/ornekisletme");

        WebsiteCheckResult result = agent.execute(place);

        assertThat(result.needsWebsite()).isTrue();
        assertThat(result.websitePresence()).isEqualTo(WebsitePresence.SOCIAL_ONLY);
    }

    @Test
    void realWebsite_needsWebsiteIsFalse() {
        PlaceDto place = placeWithWebsite("https://ornekisletme.com.tr");

        WebsiteCheckResult result = agent.execute(place);

        assertThat(result.needsWebsite()).isFalse();
        assertThat(result.websitePresence()).isEqualTo(WebsitePresence.HAS_WEBSITE);
    }

    @Test
    void nullInput_throwsException() {
        assertThatThrownBy(() -> agent.execute(null))
                .isInstanceOf(NullPointerException.class);
    }

    private PlaceDto placeWithWebsite(String websiteUri) {
        return new PlaceDto(
                "place-1", "Örnek İşletme", "Örnek Adres", 0, 0,
                websiteUri, "Örnek açıklama", "+905551234567"
        );
    }
}