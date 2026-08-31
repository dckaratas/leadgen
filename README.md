# leadgen

Google Maps koordinatı temelli işletme tespiti → website eksikliği kontrolü →
LLM ile statik site üretimi → outreach e-postası pipeline'ı.

## Akış

```
PlacesClient.searchNearby()
        │
        ▼
WebsiteCheckAgent   (site var mı? -> yoksa devam)
        │
        ▼
ContentGenAgent     (LLM ile statik HTML üretimi)
        │
        ▼
OutreachAgent       (e-posta gönderimi)
```

Orchestrator: `LeadPipelineOrchestrator` bu zinciri yönetir.

## Paket yapısı

- `agent/` — her pipeline adımı kendi alt paketinde, `Agent<I,O>` arayüzünü
  implemente ediyor
- `places/` — Google Places API erişimi (agent değil, düz veri erişimi)
- `orchestrator/` — akışı yöneten sınıf
- `domain/` — JPA entity'leri

## Durum

Bu bir **iskelet**tir. Her agent'ın `execute()` metodu şu an
`UnsupportedOperationException` fırlatıyor — asıl mantık kasıtlı olarak
boş bırakıldı, sırayla doldurulacak.

## Önemli yasal not

`OutreachAgent` production'da gerçek/rastgele işletmelere toplu soğuk mail
göndermeden önce Türkiye'deki İYS (İleti Yönetim Sistemi) onay zorunluluğunu
çöz. MVP/demo aşamasında sadece kendi test adreslerinle çalıştır.

## Sıradaki adım

1. `PlacesClient.searchNearby()` — Places API (New) entegrasyonu
2. `WebsiteCheckAgent.execute()` — karar mantığı
3. `ContentGenAgent.execute()` — prompt tasarımı + LLM çağrısı
4. `OutreachAgent.execute()` — e-posta gönderimi (test modunda)
