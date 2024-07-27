package github.aaroniz.guilded.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

//https://www.guilded.gg/docs/api/chat/ChannelMessageUpdate
public record UpdateChatMessage(@JsonProperty("content") String content) {
}
