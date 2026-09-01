package com.senin.leadgen.services;

import com.senin.leadgen.domain.Lead;
import com.senin.leadgen.domain.LeadStatus;

public interface LeadService {

    Lead createLead(String placeId, String displayName, LeadStatus status);

    void markStatus(Lead lead, LeadStatus newStatus);
}
