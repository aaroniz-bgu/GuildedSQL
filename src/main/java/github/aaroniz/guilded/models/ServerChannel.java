package github.aaroniz.guilded.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ServerChannel(
        @JsonProperty("id") String id,
        @JsonProperty("type") String type,
        @JsonProperty("name") String name,
        @JsonProperty("topic") String topic,
        @JsonProperty("createdAt") String createdAt,
        @JsonProperty("createdBy") String createdBy,
        @JsonProperty("updatedAt") String updatedAt,
        @JsonProperty("serverId") String serverId,
        @JsonProperty("rootId") String rootId,
        @JsonProperty("parentId") String parentId,
        @JsonProperty("messageId") String messageId,
        @JsonProperty("categoryId") int categoryId,
        @JsonProperty("groupId") String groupId,
        @JsonProperty("visibility") String visibility,
        @JsonProperty("archivedBy") String archivedBy,
        @JsonProperty("archivedAt") String archivedAt,
        @JsonProperty("priority") int priority
    ) {
}
