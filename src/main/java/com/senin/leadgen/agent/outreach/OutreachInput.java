package com.senin.leadgen.agent.outreach;

import com.senin.leadgen.agent.contactdiscovery.ContactInfo;
import com.senin.leadgen.agent.contentgen.GeneratedSite;

public record OutreachInput(ContactInfo contactInfo, GeneratedSite site) {
}
