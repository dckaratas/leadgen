package com.senin.leadgen.agent;

import com.senin.leadgen.agent.contactdiscovery.ContactChannel;
import com.senin.leadgen.agent.contactdiscovery.ContactDiscoveryAgent;
import com.senin.leadgen.agent.contactdiscovery.ContactInfo;
import com.senin.leadgen.places.PlaceDto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContactDiscoveryAgentTest {

    private final ContactDiscoveryAgent agent = new ContactDiscoveryAgent();

    @Test
    void hasPhoneNumber_returnsWhatsappChannel() {
        PlaceDto place = placeWithPhone("+905551234567");

        ContactInfo result = agent.execute(place);

        assertThat(result.channel()).isEqualTo(ContactChannel.WHATSAPP);
        assertThat(result.phoneNumber()).isEqualTo("+905551234567");
    }

    @Test
    void noPhoneNumber_returnsNoneChannel() {
        PlaceDto place = placeWithPhone(null);

        ContactInfo result = agent.execute(place);

        assertThat(result.channel()).isEqualTo(ContactChannel.NONE);
        assertThat(result.phoneNumber()).isNull();
    }

    private PlaceDto placeWithPhone(String phoneNumber) {
        return new PlaceDto(
                "place-1", "Örnek İşletme", "Örnek Adres", 0, 0,
                null, "Örnek açıklama", phoneNumber
        );
    }
}