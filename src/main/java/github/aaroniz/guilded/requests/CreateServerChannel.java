package github.aaroniz.guilded.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateServerChannel(
        //https://www.guilded.gg/docs/api/channels/ChannelCreate
        @JsonProperty("name") String name,
        @JsonProperty("type") String type,
        @JsonProperty("topic") String topic,
        @JsonProperty("visibility") String visibility,
        @JsonProperty("serverId") String serverId
    ) {
}
