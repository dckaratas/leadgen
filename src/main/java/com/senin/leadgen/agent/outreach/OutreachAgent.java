package com.senin.leadgen.agent.outreach;

import com.senin.leadgen.agent.Agent;
import com.senin.leadgen.agent.contactdiscovery.ContactChannel;
import com.senin.leadgen.agent.contactdiscovery.ContactInfo;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Girdi: iletişim kanalı bilgisi + üretilmiş site.
 * Çıktı: wa.me linki üretilebildi mi.
 * <p>
 * TODO (core logic):
 *  - contactInfo.channel() == WHATSAPP ise numarayı wa.me formatına çevir
 *    (uluslararası format zaten +90... şeklinde geliyor, wa.me linki için
 *    başındaki '+' ve boşlukların temizlenmesi gerekir)
 *  - mesaj metnini oluştur (site linkini/tanıtımını içeren kısa bir metin,
 *    URL-encode edilmeli çünkü wa.me query param'a gidecek)
 *  - channel == NONE ise linkGenerated=false, failureReason doldur
 */
@Component
public class OutreachAgent implements Agent<OutreachInput, OutreachResult> {

    private static final String MESSAGE_TEMPLATE =
            "Merhaba %s, işletmeniz için ücretsiz bir web sitesi taslağı hazırladık. "
                    + "İncelemek ister misiniz?";

    @Override
    public OutreachResult execute(OutreachInput input) {
        ContactInfo contactInfo = input.contactInfo();

        if (contactInfo.channel() != ContactChannel.WHATSAPP) {
            return new OutreachResult(false, null, "Kullanılabilir kanal yok");
        }

        String businessName = input.site().place().displayName();
        String message = MESSAGE_TEMPLATE.formatted(businessName);

        String phoneNumber = Objects.requireNonNull(
                contactInfo.phoneNumber(),
                "ContactChannel.WHATSAPP ise phoneNumber null olmamalı — ContactDiscoveryAgent kontratı ihlal edildi"
        );
        String cleanedPhone = phoneNumber.replaceAll("[^0-9]", "");

        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
        String link = "https://wa.me/%s?text=%s".formatted(cleanedPhone, encodedMessage);

        return new OutreachResult(true, link, null);
    }
}