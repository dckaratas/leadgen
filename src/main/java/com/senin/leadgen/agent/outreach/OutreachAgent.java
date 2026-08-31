package com.senin.leadgen.agent.outreach;

import com.senin.leadgen.agent.Agent;
import com.senin.leadgen.agent.contentgen.GeneratedSite;
import org.springframework.stereotype.Component;

/**
 * Girdi: üretilmiş site.
 * Çıktı: gönderim sonucu.
 *
 * ÖNEMLİ (bkz. önceki konuşma): Türkiye'de ticari elektronik ileti
 * göndermek için İYS onayı gerekiyor. Bu agent'ı gerçek toplu soğuk
 * mail için PRODUCTION'da kullanmadan önce bu kısıtı çöz - aksi halde
 * yasal risk var. MVP/demo aşamasında kendi test adreslerinle sınırlı tut.
 *
 * TODO (core logic):
 *  - e-posta bulma stratejisi (çoğu zaman Places API bunu sağlamaz)
 *  - e-posta şablonu / kişiselleştirme
 *  - gönderim + bounce/başarısızlık takibi
 */
@Component
public class OutreachAgent implements Agent<GeneratedSite, OutreachResult> {

    @Override
    public OutreachResult execute(GeneratedSite input) {
        throw new UnsupportedOperationException("TODO: e-posta gönderim mantığı");
    }
}
