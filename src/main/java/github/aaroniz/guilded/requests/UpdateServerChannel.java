package github.aaroniz.guilded.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateServerChannel(
        //https://www.guilded.gg/docs/api/channels/ChannelUpdate
        @JsonProperty("name") String name,
        @JsonProperty("topic") String topic,
        @JsonProperty("visibility") String visibility
    ) {
}
