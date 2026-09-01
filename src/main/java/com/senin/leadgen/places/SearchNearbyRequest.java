package com.senin.leadgen.places;

import java.util.List;

record SearchNearbyRequest(LocationRestriction locationRestriction) {

    record LocationRestriction(Circle circle) {
    }

    record Circle(Center center, double radius) {
    }

    record Center(double latitude, double longitude) {
    }
}

record SearchNearbyResponse(List<PlaceApiResult> places) {
}

record PlaceApiResult(
        String id,
        LocalizedText displayName,
        String formattedAddress,
        Location location,
        String websiteUri,
        String internationalPhoneNumber,
        LocalizedText editorialSummary
) {
    record LocalizedText(String text, String languageCode) {
    }

    record Location(double latitude, double longitude) {
    }
}