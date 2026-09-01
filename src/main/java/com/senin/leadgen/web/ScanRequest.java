package com.senin.leadgen.web;

import jakarta.validation.constraints.NotNull;

public record ScanRequest(
        @NotNull Double latitude,
        @NotNull Double longitude,
        @NotNull Integer initialRadiusMeters
) {
}