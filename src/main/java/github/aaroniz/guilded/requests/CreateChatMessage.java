package github.aaroniz.guilded.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateChatMessage(
    //https://www.guilded.gg/docs/api/chat/ChannelMessageCreate
    @JsonProperty("isPrivate") boolean isPrivate,
    @JsonProperty("isSilent") boolean isSilent,
    @JsonProperty("replyMessageIds") String[] replyMessageIds,
    @JsonProperty("content") String content
    ) {
}
