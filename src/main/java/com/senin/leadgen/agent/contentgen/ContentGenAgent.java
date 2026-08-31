package com.senin.leadgen.agent.contentgen;

import com.senin.leadgen.agent.Agent;
import com.senin.leadgen.agent.AgentResult;
import com.senin.leadgen.agent.websitecheck.WebsiteCheckResult;
import com.senin.leadgen.places.PlaceDto;
import com.senin.leadgen.places.WebsitePresence;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

/**
 * Girdi: web sitesine ihtiyacı olduğu tespit edilmiş işletme kaydı.
 * Çıktı: üretilmiş statik site içeriği (HTML + meta bilgiler).
 * <p>
 * Spring AI ChatClient constructor injection ile geliyor
 * (ChatClient.Builder Spring AI starter tarafından otomatik konfigüre edilir).
 * <p>
 * TODO (core logic - en kritik ve en çok emek isteyen kısım burası):
 *  1) Prompt tasarımı:
 *     - system mesajı: LLM'e rolünü anlat ("küçük işletmeler için tanıtım
 *       metni yazan bir asistansın"), format/ton beklentisini belirt
 *     - user mesajına input.place()'ten displayName, formattedAddress,
 *       editorialSummary geç
 *     - WebsiteCheckResult.reason() içindeki presence bilgisini de bağlam
 *       olarak ekle (SOCIAL_ONLY ise farklı bir CTA/ton düşünülebilir -
 *       bkz. önceki konuşma)
 *  2) Structured output çağrısı:
 *     chatClient.prompt()
 *         .system(...)
 *         .user(...)
 *         .call()
 *         .entity(GeneratedSiteContent.class);   // Spring AI otomatik JSON şema + parse yapar
 *  3) GeneratedSiteContent -> HTML dönüşümü:
 *     - burada LLM'e HTML yazdırma, kendi sabit HTML template'ini (String.format
 *       veya bir template engine - Thymeleaf de düşünülebilir) kullan ve
 *       sadece metin alanlarını doldur. Tasarım tutarlılığı böyle sağlanır.
 *  4) Kalite kontrolü:
 *     - aboutText boş/çok kısa geldiyse ne yapılacak? (retry mi, fail mi)
 *     - entity() dönüşümü parse hatası verirse (LLM beklenmedik format
 *       döndürürse) try/catch ve fallback stratejisi
 */
@Component
public class ContentGenAgent implements Agent<WebsiteCheckResult, GeneratedSite> {

    private final ChatClient chatClient;
    private final TemplateEngine templateEngine;

    public ContentGenAgent(ChatClient.Builder chatClientBuilder, TemplateEngine templateEngine) {
        this.chatClient = chatClientBuilder.build();
        this.templateEngine = templateEngine;
    }

    @Override
    public AgentResult<GeneratedSite> execute(WebsiteCheckResult input) {
        PlaceDto place = input.place();

        String systemPrompt = """
                Sen küçük ve orta ölçekli işletmeler için tanıtım web sitesi
                içeriği yazan bir asistansın. Yazdığın metinler samimi ama
                profesyonel bir tonda olmalı, abartılı pazarlama dilinden kaçın.
                Sadece istenen alanları doldur, ekstra açıklama yazma.
                """;

        String userPrompt = """
                İşletme adı: %s
                Adres: %s
                Google Haritalar açıklaması: %s
                Web sitesi durumu: %s
                
                Bu bilgilere dayanarak işletme için bir tanıtım sitesi içeriği üret.
                """.formatted(
                place.displayName(),
                place.formattedAddress(),
                place.editorialSummary() != null ? place.editorialSummary() : "Açıklama verilmemiş; işletme adında bağlam çıkarılabiliyorsa onu kullan",
                input.websitePresence() == WebsitePresence.SOCIAL_ONLY ? "Sosyal medya hesabı var, website tonunu/içeriğini buna göre hazırla" : "Herhangi bir website ya da sosyal medya hesabı yok"
        );

        GeneratedSiteContent content = chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .entity(GeneratedSiteContent.class);

        String html = renderHtml(content);

        return new GeneratedSite(place, html, null);
    }

    private String renderHtml(GeneratedSiteContent content) {
        Context context = new Context();
        context.setVariable("businessName", content.businessName());
        context.setVariable("tagline", content.tagline());
        context.setVariable("aboutText", content.aboutText());
        context.setVariable("services", content.services() != null ? content.services() : List.of());
        context.setVariable("callToActionText", content.callToActionText());

        return templateEngine.process("business-site", context);
    }
}
