package github.aaroniz.guilded.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import github.aaroniz.guilded.models.ChatMessage;

public record MessageResponse(@JsonProperty("message") ChatMessage message) {
}
