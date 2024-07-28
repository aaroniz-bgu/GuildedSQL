package github.aaroniz.data;

import github.aaroniz.guilded.requests.CreateChatMessage;
import github.aaroniz.guilded.requests.UpdateChatMessage;
import github.aaroniz.guilded.responses.ChannelResponse;
import github.aaroniz.guilded.responses.MessageResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.util.Map;
import java.util.NoSuchElementException;

import static github.aaroniz.api.Constants.*;

public class MetaManager {
    private final Map<String, GuildedTable> cachedTables;
    private final WebClient client;
    private final GuildedTable meta;

    public MetaManager(Map<String, GuildedTable> cachedTables, WebClient client, GuildedTable meta) {
        this.cachedTables = cachedTables;
        this.client = client;
        this.meta = meta;
    }

    public void loadCacheFromMeta() {
        GuildedBuffer buf = new GuildedBuffer(100, client, meta.getUUID(), true);
        while(buf.getEntries() != null && buf.getEntries().length > 0) {
            for(GuildedDataEntry entry : buf.getEntries()) {
                String[] tables = entry.getData().split(",");
                for(String uuid : tables) {
                    ChannelResponse res = client.get().uri(CHANNEL + "/" + uuid)
                            .retrieve()
                            .bodyToMono(ChannelResponse.class)
                            .block();
                    if(res == null) continue;
                    cacheTable(new GuildedTable(uuid, res.channel().name(), res.channel().topic()));
                }
            }
            buf = new GuildedBuffer(100, client, meta.getUUID(), true, buf.getLastsDate());
        }
    }

    public GuildedTable cacheAndMetaSave(GuildedTable table) {
        addTableToMeta(table.getUUID());
        return cacheTable(table);
    }

    public void deleteAndMetaRemove(String name) {
        if(!cacheContainsTable(name)) return;
        deleteTableFromMeta(getCachedTable(name).getUUID());
        deleteCachedTable(name);
    }

    public GuildedTable cacheTable(GuildedTable table) {
        if(table.getUUID() == null || table.getUUID().isBlank()) {
            throw new RuntimeException("Ran into a problem while caching a table");
        } else if (cacheContainsTable(table.getName())) {
            throw new KeyAlreadyExistsException("Table with name " + table.getName() + " already cached.");
        }
        return cachedTables.put(table.getName().toLowerCase(), table);
    }

    public GuildedTable getCachedTable(String name) {
        name = name.toLowerCase();
        if(cachedTables.containsKey(name)) {
            return cachedTables.get(name);
        }
        throw new NoSuchElementException("Table with name " + name + " was not found.");
    }

    public boolean cacheContainsTable(String name) {
        name = name.toLowerCase();
        return cachedTables.containsKey(name);
    }

    public boolean deleteCachedTable(String name) {
        return cachedTables.remove(name.toLowerCase()) != null;
    }

    private void addTableToMeta(String uuid) {
        MessageResponse response = client.get()
                .uri(CHANNEL + "/{channelId}/" + MESSAGE, meta.getUUID())
                .retrieve()
                .bodyToMono(MessageResponse.class)
                .block();

        if(response == null) throw new RuntimeException("Could not save table identifier to meta-table");

        if(response.message() != null && response.message().content().length() + uuid.length() <= MAX_CHUNK) {
            String newContent = response.message().content().concat(",").concat(uuid);
            client.patch()
                    .uri(CHANNEL + "/{channelId}/" + MESSAGE, meta.getUUID())
                    .body(Mono.just(new UpdateChatMessage(newContent)), UpdateChatMessage.class)
                    .retrieve()
                    .bodyToMono(ChannelResponse.class)
                    .block();
        } else {
            CreateChatMessage request = new CreateChatMessage(true, false, null, uuid);
            client.post()
                    .uri(CHANNEL + "/{channelId}/" + MESSAGE, meta.getUUID())
                    .body(Mono.just(request), CreateChatMessage.class)
                    .retrieve()
                    .bodyToMono(MessageResponse.class)
                    .block();
        }
    }

    public void deleteTableFromMeta(String uuid) {
        try {
            GuildedBuffer buf = new GuildedBuffer(100, client, meta.getUUID(), true);

            while (buf.getEntries().length > 0) {
                for (GuildedDataEntry entry : buf.getEntries()) {
                    if (entry.getData().contains(uuid))
                        deleteTableFromMetaHelper(uuid, entry);
                }
                buf = new GuildedBuffer(100, client, meta.getUUID(), true, buf.getLastsDate());
            }
        } catch(Exception ignored) {}
    }

    private void deleteTableFromMetaHelper(String uuid, GuildedDataEntry msg) {
        final String[] content = msg.getData().split(",");
        final StringBuilder builder = new StringBuilder();
        for(String s : content) {
            if(!s.equals(uuid)) builder.append(s).append(",");
        }

        String newContent = builder.toString();
        int sub = builder.lastIndexOf(",");
        sub = sub == -1 ? newContent.length() : sub;
        newContent = newContent.substring(0, sub);

        if(!newContent.isBlank()) {
            Mono<UpdateChatMessage> requestMono = Mono.just(new UpdateChatMessage(newContent));
            client.patch()
                    .uri(CHANNEL + "/{channelId}/" + MESSAGE + "/{msgId}", meta.getUUID(), msg.getUUID())
                    .body(requestMono, UpdateChatMessage.class)
                    .retrieve()
                    .bodyToMono(MessageResponse.class)
                    .block();
        } else {
            client.delete()
                    .uri(CHANNEL + "/{channelId}/" + MESSAGE + "/{msgId}", meta.getUUID(), msg.getUUID())
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        }
    }
}
