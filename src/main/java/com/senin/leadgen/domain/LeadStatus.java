package com.senin.leadgen.domain;

public enum LeadStatus {
    FOUND,                  // Places API'den çekildi
    SKIPPED_HAS_WEBSITE,    // zaten sitesi var, pipeline durdu
    SITE_GENERATION_FAILED, // ContentGenAgent başarısız oldu
    SITE_GENERATED,         // site üretildi
    EMAIL_SENT,
    EMAIL_FAILED,
    CONTACT_LINK_READY,
    CONTACT_DISCOVERY_FAILED
}