package github.aaroniz.guilded.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import github.aaroniz.guilded.models.ServerChannel;

public record ChannelResponse(@JsonProperty("channel") ServerChannel channel) {
}
