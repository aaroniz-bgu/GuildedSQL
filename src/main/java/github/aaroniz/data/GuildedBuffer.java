package github.aaroniz.data;

import github.aaroniz.guilded.models.ChatMessage;
import github.aaroniz.guilded.responses.MessagesResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Arrays;

/**
 * Not a real buffer.
 */
public class GuildedBuffer {
    private GuildedDataEntry[] entries;

    public GuildedBuffer(int size, WebClient client, String uuid, boolean getPrivate) {
        entries = null;
        Mono<MessagesResponse> requestMono = client.get()
                .uri("channels/{id}?limit={limit},includePrivate={private}", size, getPrivate)
                .retrieve()
                .bodyToMono(MessagesResponse.class);

        try {
            MessagesResponse response = requestMono.block();
            if(response == null) throw new RuntimeException("Ran into issue while fetching data");
            ChatMessage[] msgs = response.messages();
            entries = new GuildedDataEntry[Math.min(size, msgs.length)];
            for(int i = 0; i < entries.length; i++) {
                String prev = msgs[i].replyMessageIds() != null && msgs[i].replyMessageIds().length > 0 ?
                        msgs[i].replyMessageIds()[0]:
                        null;
                int firstTilda = msgs[i].content().indexOf("~");
                firstTilda = firstTilda == -1 ? 0 : firstTilda;
                entries[i] = new GuildedDataEntry(msgs[i].id(),
                        msgs[i].content().substring(0,firstTilda),
                        msgs[i].content().substring(firstTilda + 1),
                        prev, msgs[i].isPrivate(), msgs[i].createdAt());
            }
        } catch (WebClientResponseException e) {
            throw e;
        }
    }

    public GuildedBuffer(int size, WebClient client, String uuid, boolean getPrivate, String before) {
        entries = null;
        Mono<MessagesResponse> requestMono = client.get()
                .uri("channels/{id}?limit={limit},includePrivate={private},before={before}", size, getPrivate, before)
                .retrieve()
                .bodyToMono(MessagesResponse.class);

        try {
            MessagesResponse response = requestMono.block();
            if(response == null) throw new RuntimeException("Ran into issue while fetching data");
            ChatMessage[] msgs = response.messages();
            entries = new GuildedDataEntry[Math.min(size, msgs.length)];
            for(int i = 0; i < entries.length; i++) {
                String prev = msgs[i].replyMessageIds() != null && msgs[i].replyMessageIds().length > 0 ?
                        msgs[i].replyMessageIds()[0]:
                        null;
                int firstTilda = msgs[i].content().indexOf("~");
                firstTilda = firstTilda == -1 ? 0 : firstTilda;
                entries[i] = new GuildedDataEntry(msgs[i].id(),
                        msgs[i].content().substring(0,firstTilda),
                        msgs[i].content().substring(firstTilda + 1),
                        prev, msgs[i].isPrivate(), msgs[i].createdAt());
            }
        } catch (WebClientResponseException e) {
            throw e;
        }
    }

    public GuildedDataEntry[] getEntries() {
        return entries;
    }

    public String[] getContentOnly() {
        return entries == null ? null : Arrays.stream(entries)
                .map(GuildedDataEntry::getData)
                .toList().toArray(new String[0]);
    }

    public String[] getKeysOnly() {
        return entries == null ? null : Arrays.stream(entries)
                .map(GuildedDataEntry::getUUID)
                .toList().toArray(new String[0]);
    }

    public String getLastsDate() {
        return entries == null ? null : entries[entries.length - 1].getDate();
    }
}
