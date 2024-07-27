package github.aaroniz.guilded.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import github.aaroniz.guilded.models.ChatMessage;

public record MessagesResponse(@JsonProperty("messages") ChatMessage[] messages) {
}
