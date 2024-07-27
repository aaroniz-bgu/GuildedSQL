package github.aaroniz.guilded.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateServerChannel(
        @JsonProperty("name") String name,
        @JsonProperty("topic") String topic,
        @JsonProperty("visibility") String visibility,
        @JsonProperty("priority") String priority
    ) {
}
