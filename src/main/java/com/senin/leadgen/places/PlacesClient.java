package com.senin.leadgen.places;

import com.senin.leadgen.agent.AgentResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Component
public class PlacesClient {

    private static final Logger log = LoggerFactory.getLogger(PlacesClient.class);
    private static final int MAX_RADIUS_GROWTH_ATTEMPTS = 3;
    private static final int RADIUS_GROWTH_STEP_METERS = 500;
    private static final String FIELD_MASK = "places.id,places.displayName,places.formattedAddress,"
            + "places.location,places.websiteUri,places.internationalPhoneNumber,places.editorialSummary";

    private final RestClient restClient;
    private final String apiKey;

    public PlacesClient(RestClient placesRestClient, @Value("${places.api-key}") String apiKey) {
        this.restClient = placesRestClient;
        this.apiKey = apiKey;
    }

    public AgentResult<List<PlaceDto>> searchNearby(double latitude, double longitude, int initialRadiusMeters) {
        int radius = initialRadiusMeters;

        for (int attempt = 0; attempt <= MAX_RADIUS_GROWTH_ATTEMPTS; attempt++) {
            List<PlaceDto> results;
            try {
                results = callApi(latitude, longitude, radius);
            } catch (RestClientException e) {
                log.error("Places API çağrısı başarısız (radius={}m): {}", radius, e.getMessage());
                return AgentResult.failed("Places API hatası: " + e.getMessage());
            }

            if (!results.isEmpty()) {
                log.info("Sonuç bulundu (radius={}m, deneme={})", radius, attempt);
                return AgentResult.ok(results);
            }

            log.debug("Sonuç yok (radius={}m), yarıçap büyütülüyor", radius);
            radius += RADIUS_GROWTH_STEP_METERS;
        }

        log.warn("{} denemeden sonra hiç sonuç bulunamadı (son radius={}m)", MAX_RADIUS_GROWTH_ATTEMPTS + 1, radius);
        return AgentResult.ok(List.of());   // gerçekten sonuç yok - bu bir "başarısızlık" değil
    }

    private List<PlaceDto> callApi(double latitude, double longitude, int radiusMeters) {
        var request = new SearchNearbyRequest(
                new SearchNearbyRequest.LocationRestriction(
                        new SearchNearbyRequest.Circle(
                                new SearchNearbyRequest.Center(latitude, longitude),
                                radiusMeters
                        )
                )
        );

        SearchNearbyResponse response = restClient.post()
                .uri("/places:searchNearby")
                .header("X-Goog-Api-Key", apiKey)
                .header("X-Goog-FieldMask", FIELD_MASK)
                .body(request)
                .retrieve()
                .body(SearchNearbyResponse.class);

        if (response == null || response.places() == null) {
            return List.of();
        }

        return response.places().stream()
                .map(this::toPlaceDto)
                .toList();
    }

    private PlaceDto toPlaceDto(PlaceApiResult apiResult) {
        return new PlaceDto(
                apiResult.id(),
                apiResult.displayName() != null ? apiResult.displayName().text() : null,
                apiResult.formattedAddress(),
                apiResult.location() != null ? apiResult.location().latitude() : 0,
                apiResult.location() != null ? apiResult.location().longitude() : 0,
                apiResult.websiteUri(),
                apiResult.editorialSummary() != null ? apiResult.editorialSummary().text() : null,
                apiResult.internationalPhoneNumber()
        );
    }
}