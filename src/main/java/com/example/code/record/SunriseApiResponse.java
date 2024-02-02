package com.example.code.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SunriseApiResponse(
        @JsonProperty("results")
        Results results,
        @JsonProperty("status")
        String status,
        @JsonProperty("tzId")
        String tzId

) {
}
