
# leadgen

Google Maps koordinatı temelli işletme tespiti → website eksikliği kontrolü →
LLM ile statik site üretimi → WhatsApp üzerinden outreach linki üretme pipeline'ı.

## Akış

```
PlacesClient.searchNearby()      (Google Places API, senkron, yarıçap büyütme ile)
│
▼
WebsiteCheckAgent                (site var mı / sadece sosyal medya mı / yok mu — plain Java)
│
▼
ContactDiscoveryAgent            (telefon numarası var mı -> WhatsApp kanalı)
│
▼
ContentGenAgent                  (LLM + structured output + Thymeleaf -> statik HTML)
│
▼
OutreachAgent                    (wa.me linki üretimi — gönderim manuel yapılır)
```

`LeadPipelineOrchestrator` bu zinciri yönetir ve `@Async` ile arka planda çalışır.

## Paket yapısı

- `agent/` — her pipeline adımı kendi alt paketinde, `Agent<I,O>` arayüzünü implemente ediyor
  - `websitecheck/` — web sitesi/sosyal medya varlığı tespiti (`WebsitePresence` enum)
  - `contactdiscovery/` — iletişim kanalı tespiti (`ContactChannel` enum)
  - `contentgen/` — LLM ile içerik üretimi + Thymeleaf render
  - `outreach/` — wa.me linki üretimi
  - `Agent.java`, `AgentResult.java` — genel sözleşme ve hata yönetimi sarmalayıcısı
- `places/` — Google Places API erişimi (agent değil, düz veri erişimi + HTTP hata yönetimi)
- `orchestrator/` — akışı yöneten sınıf
- `domain/` — `Lead` JPA entity'si, `LeadStatus` enum, `LeadRepository`
- `services/` — `LeadService` (lead oluşturma/durum güncelleme, duplicate önleme)
- `web/` — REST endpoint (`POST /api/leads/scan`)
- `config/` — `RestClient` bean tanımı (Places API için)

## Mevcut özellikler

- **Konum bazlı tarama**: verilen koordinat + başlangıç yarıçapı ile Places API (New)
  üzerinden yakındaki işletmeler çekilir; sonuç boşsa yarıçap 500m adımlarla
  3 kez büyütülür
- **Website tespiti**: işletmenin gerçek bir web sitesi mi, sadece sosyal medya
  (Instagram/Facebook/Linktree/Wix) hesabı mı, yoksa hiçbir web varlığı mı
  olmadığı ayırt edilir
- **İletişim kanalı tespiti**: Places API'den dönen telefon numarasına göre
  WhatsApp üzerinden ulaşılabilir mi kontrol edilir (henüz email arama yok)
- **LLM ile site üretimi**: Spring AI + Claude ile structured output
  (`GeneratedSiteContent`) üretilir, Thymeleaf template'i ile güvenli
  (auto-escaped) statik HTML'e dönüştürülür
- **Duplicate önleme**: aynı `placeId` tekrar bulunursa yeni kayıt açılmaz,
  mevcut kayıt/durumu korunur
- **Retry mantığı**: `SITE_GENERATION_FAILED` / `CONTACT_DISCOVERY_FAILED`
  durumundaki kayıtlar bir sonraki taramada tekrar denenir; `SKIPPED_HAS_WEBSITE`
  / `CONTACT_LINK_READY` durumundakiler atlanır
- **Asenkron çalıştırma**: `/api/leads/scan` endpoint'i isteği hemen kabul eder
  (`202 Accepted`), pipeline arka planda (`@Async`) çalışır
- **Hata yönetimi**: `AgentResult<T>` deseni ile hem `PlacesClient` hem
  `ContentGenAgent` başarısızlık durumunu (exception yerine) değer olarak taşır

## API

```
POST /api/leads/scan
Content-Type: application/json

{
"latitude": 39.9208,
"longitude": 32.8541,
"initialRadiusMeters": 500
}
```

`202 Accepted` döner, işlem arka planda devam eder.

## Testler

- `WebsiteCheckAgentTest` — website/sosyal medya/yok senaryoları
- `ContactDiscoveryAgentTest` — telefon var/yok senaryoları
- `ContentGenAgent` ve `OutreachAgent` için henüz test yok (dış bağımlılıklar —
  `ChatClient`, `TemplateEngine` — mock'lanması gerekiyor, sıradaki adım)

## Önemli yasal not

`OutreachAgent` gerçek/rastgele işletmelere **otomatik mesaj göndermez** —
sadece bir `wa.me` linki üretir, gönderim kararı ve eylemi kişiye bırakılır.
Bu, Türkiye'deki İYS (İleti Yönetim Sistemi) onay zorunluluğunu ve WhatsApp
Business API'nin şablon/opt-in kısıtlarını MVP aşamasında bypass etmek için
bilinçli bir tasarım kararıdır — tam otomatik gönderim eklenirse bu kısıtlar
yeniden değerlendirilmelidir.

## Bilinen eksikler / sıradaki adımlar

- Gerçek API key'lerle (Places + Anthropic) uçtan uca test yapılmadı
- Email arama (LLM + web search tool) henüz yok — MVP kapsamı dışında bırakıldı,
  telefon/WhatsApp bulunamayan işletmeler `CONTACT_DISCOVERY_FAILED` olarak işaretlenir
- `ContentGenAgent` / `OutreachAgent` için unit test yok
- `Lead` entity'sinde `id` getter'ı Lombok `@Getter` ile üretiliyor — JDK 21
  (LTS) gerektirir, daha yeni JDK sürümlerinde Lombok annotation processing
  sessizce çalışmayabilir
