package com.example.code.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Results(
        @JsonProperty("sunrise")
        String sunrise,
        @JsonProperty("sunset")
        String sunset,
        @JsonProperty("solar_noon")
        String solarNoon,
        @JsonProperty("day_length")
        String dayLength,
        @JsonProperty("civil_twilight_begin")
        String civilTwilightBegin,
        @JsonProperty("civil_twilight_end")
        String civilTwilightEnd,
        @JsonProperty("nautical_twilight_begin")
        String nauTwilightBegin,
        @JsonProperty("nautical_twilight_end")
        String nauTwilightEnd,
        @JsonProperty("astronomical_twilight_begin")
        String astTwilightBegin,
        @JsonProperty("astronomical_twilight_end")
        String astTwilightEnd

) {
}
