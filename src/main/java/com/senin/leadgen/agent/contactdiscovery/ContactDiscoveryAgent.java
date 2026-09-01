package com.senin.leadgen.agent.contactdiscovery;

import com.senin.leadgen.agent.Agent;
import com.senin.leadgen.places.PlaceDto;
import org.springframework.stereotype.Component;

/**
 * Girdi: ham işletme kaydı.
 * Çıktı: hangi kanaldan ulaşılabileceği bilgisi.
 * <p>
 * TODO (core logic):
 *  - hasPhoneNumber() true ise WHATSAPP, değilse NONE (MVP mantığı basit)
 *  - GELECEK: email search tool eklenince NONE yerine önce email denenecek,
 *    o da bulunamazsa WHATSAPP'a düşülecek (fallback zinciri)
 */
@Component
public class ContactDiscoveryAgent implements Agent<PlaceDto, ContactInfo> {

    @Override
    public ContactInfo execute(PlaceDto input) {
        return input.hasPhoneNumber() ?
                new ContactInfo(ContactChannel.WHATSAPP, input.internationalPhoneNumber()) :
                new ContactInfo(ContactChannel.NONE, null);
    }
}