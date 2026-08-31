package com.senin.leadgen.agent.contentgen;

import com.senin.leadgen.places.PlaceDto;

public record GeneratedSite(
        PlaceDto place,
        String htmlContent,
        String previewUrl // örn. geçici olarak sunucuda barındırılan önizleme linki
) {
}
