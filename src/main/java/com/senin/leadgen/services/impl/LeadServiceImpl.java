package com.senin.leadgen.services.impl;

import com.senin.leadgen.domain.Lead;
import com.senin.leadgen.domain.LeadStatus;
import com.senin.leadgen.repository.LeadRepository;
import com.senin.leadgen.services.LeadService;
import org.springframework.stereotype.Service;

@Service
public class LeadServiceImpl implements LeadService {
    private final LeadRepository leadRepository;

    public LeadServiceImpl(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    @Override
    public Lead createLead(String placeId, String displayName, LeadStatus status) {
        return leadRepository.findByPlaceId(placeId)
                .orElseGet(() -> leadRepository.save(
                        new Lead(placeId, displayName, status)
                ));
    }

    @Override
    public void markStatus(Lead lead, LeadStatus newStatus) {
        lead.updateStatus(newStatus);
        leadRepository.save(lead);
    }
}
