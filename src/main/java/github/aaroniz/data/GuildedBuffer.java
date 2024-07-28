package github.aaroniz.data;

import github.aaroniz.guilded.models.ChatMessage;
import github.aaroniz.guilded.responses.MessagesResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;

import static github.aaroniz.api.Constants.CHANNEL;
import static github.aaroniz.api.Constants.MESSAGE;

/**
 * Not a real buffer.
 */
public class GuildedBuffer {
    private GuildedDataEntry[] entries;

    public GuildedBuffer(int size, WebClient client, String uuid, String before) {
        entries = new GuildedDataEntry[0];

        String uri = getUri(size, uuid, before);
        Mono<MessagesResponse> requestMono = client.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(MessagesResponse.class);
        MessagesResponse response = requestMono.block();

        if(response == null)
            throw new RuntimeException("Ran into issue while fetching data");

        final ChatMessage[] msgs = response.messages();
        final ArrayList<GuildedDataEntry> entryList = new ArrayList<>();

        for(ChatMessage msg : msgs) {
            if(!msg.type().equals("chat")) continue;

            final String prev = msg.replyMessageIds() != null && msg.replyMessageIds().length > 0 ?
                    msg.replyMessageIds()[0]:
                    null;
            int firstTilda = msg.content().indexOf("~");
            firstTilda = firstTilda == -1 ? 0 : firstTilda;

            entryList.add(new GuildedDataEntry(
                    msg.id(),
                    msg.content().substring(0, firstTilda),
                    msg.content().substring(firstTilda),
                    prev, !msg.isSilent(), msg.createdAt()));
        }

        entries = entryList.toArray(entries);
    }

    private static String getUri(int size, String uuid, String before) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(CHANNEL + "/{id}/" + MESSAGE)
                .queryParam("limit", size);

        if(before != null) builder.queryParam("before", before);

        return builder.build()
                .expand(uuid)
                .toUriString();
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

    public boolean notNullOrEmpty() {
        return entries != null && entries.length > 0;
    }
}
