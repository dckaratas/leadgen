package com.senin.leadgen.orchestrator;

import com.senin.leadgen.agent.AgentResult;
import com.senin.leadgen.agent.contactdiscovery.ContactChannel;
import com.senin.leadgen.agent.contactdiscovery.ContactDiscoveryAgent;
import com.senin.leadgen.agent.contactdiscovery.ContactInfo;
import com.senin.leadgen.agent.contentgen.ContentGenAgent;
import com.senin.leadgen.agent.contentgen.GeneratedSite;
import com.senin.leadgen.agent.outreach.OutreachAgent;
import com.senin.leadgen.agent.outreach.OutreachInput;
import com.senin.leadgen.agent.outreach.OutreachResult;
import com.senin.leadgen.agent.websitecheck.WebsiteCheckAgent;
import com.senin.leadgen.agent.websitecheck.WebsiteCheckResult;
import com.senin.leadgen.domain.Lead;
import com.senin.leadgen.domain.LeadStatus;
import com.senin.leadgen.places.PlaceDto;
import com.senin.leadgen.places.PlacesClient;
import com.senin.leadgen.services.LeadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LeadPipelineOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(LeadPipelineOrchestrator.class);

    private final PlacesClient placesClient;
    private final WebsiteCheckAgent websiteCheckAgent;
    private final ContentGenAgent contentGenAgent;
    private final OutreachAgent outreachAgent;
    private final LeadService leadService;
    private final ContactDiscoveryAgent contactDiscoveryAgent;

    public LeadPipelineOrchestrator(
            PlacesClient placesClient,
            WebsiteCheckAgent websiteCheckAgent,
            ContentGenAgent contentGenAgent,
            OutreachAgent outreachAgent,
            LeadService leadService,
            ContactDiscoveryAgent contactDiscoveryAgent
    ) {
        this.placesClient = placesClient;
        this.websiteCheckAgent = websiteCheckAgent;
        this.contentGenAgent = contentGenAgent;
        this.outreachAgent = outreachAgent;
        this.leadService = leadService;
        this.contactDiscoveryAgent = contactDiscoveryAgent;
    }

    @Async
    public void runFor(double latitude, double longitude, int initialRadiusMeters) {
        AgentResult<List<PlaceDto>> placesResult = placesClient.searchNearby(latitude, longitude, initialRadiusMeters);
        if (!placesResult.success()) {
            log.error("İşlem durduruldu: {}", placesResult.errorMessage());
            return;
        }
        List<PlaceDto> places = placesResult.value();

        for (PlaceDto place : places) {
            Lead lead = leadService.createLead(place.placeId(), place.displayName(), LeadStatus.FOUND);

            if (lead.getStatus() == LeadStatus.CONTACT_LINK_READY || lead.getStatus() == LeadStatus.SKIPPED_HAS_WEBSITE) {
                continue;
            }

            WebsiteCheckResult checkResult = websiteCheckAgent.execute(place);
            if (!checkResult.needsWebsite()) {
                leadService.markStatus(lead, LeadStatus.SKIPPED_HAS_WEBSITE);
                log.debug("Atlandı (zaten sitesi var): {}", place.displayName());
                continue;
            }

            ContactInfo contactInfo = contactDiscoveryAgent.execute(place);
            if (contactInfo.channel() == ContactChannel.NONE) {
                leadService.markStatus(lead, LeadStatus.CONTACT_DISCOVERY_FAILED);
                log.debug("Atlandı (iletişim kanalı yok): {}", place.displayName());
                continue;
            }

            AgentResult<GeneratedSite> siteResult = contentGenAgent.execute(checkResult);
            if (!siteResult.success()) {
                leadService.markStatus(lead, LeadStatus.SITE_GENERATION_FAILED);
                log.warn("Site üretimi başarısız: {} - {}", place.displayName(), siteResult.errorMessage());
                continue;
            }
            leadService.markStatus(lead, LeadStatus.SITE_GENERATED);

            OutreachResult outreachResult = outreachAgent.execute(new OutreachInput(contactInfo, siteResult.value()));
            leadService.markStatus(lead, outreachResult.linkGenerated() ? LeadStatus.CONTACT_LINK_READY : LeadStatus.CONTACT_DISCOVERY_FAILED);

            log.info("İşlendi: {} -> link üretildi={}", place.displayName(), outreachResult.linkGenerated());
        }
    }
}