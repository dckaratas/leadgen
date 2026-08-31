package com.senin.leadgen.agent.contentgen;

import java.util.List;

/**
 * LLM'in doldurması istenen yapı. Spring AI'ın BeanOutputConverter'ı
 * bu record'un alan adlarını ve tiplerini kullanarak LLM'e JSON şema
 * talimatı verir ve dönen yanıtı otomatik bu tipe map'ler.
 * <p>
 * ÖNEMLİ: alan adları prompt'ta LLM'e görünür şekilde kullanılır,
 * bu yüzden açıklayıcı seç (örn. "aboutText" yerine "x" değil).
 */
public record GeneratedSiteContent(
        String businessName,
        String tagline,          // kısa, çarpıcı slogan
        String aboutText,        // 2-3 paragraf tanıtım metni
        List<String> services,   // öne çıkan hizmet/ürün listesi
        String callToActionText  // örn. "Hemen arayın: ..."
) {
}
