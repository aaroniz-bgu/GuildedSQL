package github.aaroniz.guilded.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ChatMessage(
        //https://www.guilded.gg/docs/api/chat/ChatMessage
        @JsonProperty("id") String id,
        @JsonProperty("type") String type,
        @JsonProperty("serverId") String serverId,
        @JsonProperty("groupId") String groupId,
        @JsonProperty("channelId") String channelId,
        @JsonProperty("content") String content,
        @JsonProperty("hiddenLinkPreviewUrls") String[] hiddenLinkPreviewUrls,
        @JsonProperty("replyMessageIds") String[] replyMessageIds, // the secret to chain chunks easier
        @JsonProperty("isPrivate") boolean isPrivate,
        @JsonProperty("isSilent") boolean isSilent,
        @JsonProperty("isPinned") boolean isPinned,
        @JsonProperty("createdAt") String createdAt,
        @JsonProperty("createdBy") String createdBy,
        @JsonProperty("createdByWebhookId") String createdByWebhookId,
        @JsonProperty("updatedAt") String updatedAt
    ) {
}
