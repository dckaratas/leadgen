package com.senin.leadgen.places;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Google Places API (New) - Nearby Search / Text Search çağrılarını
 * saran ince katman. Bilinçli olarak agent DEĞİL: bu düz bir veri
 * erişim servisi, karar mantığı içermiyor.
 *
 * TODO (core logic - kendin yazacaksın):
 *  - WebClient/RestClient ile POST https://places.googleapis.com/v1/places:searchNearby
 *  - API key config (application.yml -> places.api-key)
 *  - fieldMask ile sadece gerekli alanları çekmek (maliyet kontrolü için önemli)
 *  - yarıçap büyütme stratejisi (örn. 500m -> 1000m -> 2000m, sonuç azsa)
 */
@Component
public class PlacesClient {

    public List<PlaceDto> searchNearby(double latitude, double longitude, int radiusMeters) {
        throw new UnsupportedOperationException("TODO: Places API entegrasyonu");
    }
}
