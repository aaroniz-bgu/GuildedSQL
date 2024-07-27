package github.aaroniz.guilded.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import github.aaroniz.guilded.models.ChatMessage;

public record GetChatMessages (
        @JsonProperty("before") String before,
        @JsonProperty("after") String after,
        @JsonProperty("limit") int limit,
        @JsonProperty("includePrivate") boolean includePrivate
    ) {
}
