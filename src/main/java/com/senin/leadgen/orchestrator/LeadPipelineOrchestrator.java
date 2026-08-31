package com.senin.leadgen.orchestrator;

import com.senin.leadgen.agent.contentgen.ContentGenAgent;
import com.senin.leadgen.agent.contentgen.GeneratedSite;
import com.senin.leadgen.agent.outreach.OutreachAgent;
import com.senin.leadgen.agent.outreach.OutreachResult;
import com.senin.leadgen.agent.websitecheck.WebsiteCheckAgent;
import com.senin.leadgen.agent.websitecheck.WebsiteCheckResult;
import com.senin.leadgen.places.PlaceDto;
import com.senin.leadgen.places.PlacesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Tüm pipeline'ı uçtan uca yöneten sınıf:
 * PlacesClient -> WebsiteCheckAgent -> ContentGenAgent -> OutreachAgent
 * <p>
 * Bilinçli olarak agent'lar arası iletişim doğrudan Java metod çağrısı
 * ile yapılıyor (henüz kuyruk/HTTP yok) - bkz. önceki konuşmadaki
 * "Seviye 1: aynı proje içinde ayrı paketler" kararı.
 * <p>
 * TODO (core logic):
 *  - yarıçap büyütme döngüsü (yeterli sonuç yoksa radiusMeters'i artır)
 *  - bir adım başarısız olduğunda zinciri durdurma/atlama stratejisi
 *    (AgentResult kullanılabilir)
 *  - her işletme için ayrı ayrı mı yoksa toplu mu (batch) işleneceği
 */
@Component
public class LeadPipelineOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(LeadPipelineOrchestrator.class);

    private final PlacesClient placesClient;
    private final WebsiteCheckAgent websiteCheckAgent;
    private final ContentGenAgent contentGenAgent;
    private final OutreachAgent outreachAgent;

    public LeadPipelineOrchestrator(
            PlacesClient placesClient,
            WebsiteCheckAgent websiteCheckAgent,
            ContentGenAgent contentGenAgent,
            OutreachAgent outreachAgent
    ) {
        this.placesClient = placesClient;
        this.websiteCheckAgent = websiteCheckAgent;
        this.contentGenAgent = contentGenAgent;
        this.outreachAgent = outreachAgent;
    }

    public void runFor(double latitude, double longitude, int initialRadiusMeters) {
        List<PlaceDto> places = placesClient.searchNearby(latitude, longitude, initialRadiusMeters);
        log.info("{} işletme bulundu (radius={}m)", places.size(), initialRadiusMeters);

        for (PlaceDto place : places) {
            WebsiteCheckResult checkResult = websiteCheckAgent.execute(place);
            if (!checkResult.needsWebsite()) {
                log.debug("Atlandı (zaten sitesi var): {}", place.displayName());
                continue;
            }

            GeneratedSite site = contentGenAgent.execute(checkResult);
            OutreachResult outreachResult = outreachAgent.execute(site);

            log.info("İşlendi: {} -> gönderildi={}", place.displayName(), outreachResult.sent());
        }
    }
}
