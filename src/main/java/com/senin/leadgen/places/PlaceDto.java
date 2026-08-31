package com.senin.leadgen.places;

/**
 * Google Places API (New) yanıtından çıkarılan minimal alan seti.
 * İhtiyaç oldukça (telefon, kategori, rating vb.) genişlet.
 */
public record PlaceDto(
        String placeId,
        String displayName,
        String formattedAddress,
        double latitude,
        double longitude,
        String websiteUri,   // null/boş ise -> web sitesi yok demektir
        String editorialSummary // Google'daki açıklama metni, site üretiminde kullanılacak
) {
    public boolean hasWebsite() {
        return websiteUri != null && !websiteUri.isBlank();
    }
}
