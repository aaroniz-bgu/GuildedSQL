package github.aaroniz.api;

import github.aaroniz.data.GuildedBuffer;
import github.aaroniz.data.GuildedDataEntry;
import github.aaroniz.data.GuildedTable;
import github.aaroniz.data.MetaManager;
import github.aaroniz.guilded.models.ChatMessage;
import github.aaroniz.guilded.requests.CreateChatMessage;
import github.aaroniz.guilded.requests.UpdateChatMessage;
import github.aaroniz.guilded.responses.ChannelResponse;
import github.aaroniz.guilded.requests.CreateServerChannel;
import github.aaroniz.guilded.requests.UpdateServerChannel;
import github.aaroniz.guilded.responses.MessageResponse;
import github.aaroniz.util.StringHelper;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static github.aaroniz.api.Constants.*;
/* If you're trying to understand this code, its possible,
 * but you should know that I really didn't plan on it.
 * So its on you! XD
 * It's going to change soon anyway :P
 */
public class GuildedSQLClient implements GuildedSQL {

    private final String visibility; // server.visibility = "private" ? "public" : "private";
    private final MetaManager meta;
    private final WebClient client;
    private final String serverId;

    protected GuildedSQLClient(WebClient connection, MetaManager meta, String visibility, String serverId) {
        this.visibility = visibility;
        this.client = connection;
        this.serverId = serverId;
        this.meta = meta;
    }

    private void checkMetaData(String table) {
        if(table != null && table.equalsIgnoreCase(META)) throw new IllegalArgumentException("Access to meta table is denied.");
    }

    @Override
    public GuildedTable createTable(String name) {
        return createTable(name, null);
    }

    @Override
    public GuildedTable createTable(String name, String description) {
        if(meta.cacheContainsTable(name)) {
            throw new KeyAlreadyExistsException("Table with name " + name + " already exists.");
        }
        checkMetaData(name);
        GuildedTable table = new GuildedTable(name, description);

        Mono<CreateServerChannel> requestMono = Mono.just(createServerChannel(name, description));
        Mono<ChannelResponse> resultMono = client.post()
                .uri(CHANNEL)
                .body(requestMono, CreateServerChannel.class)
                .retrieve()
                .bodyToMono(ChannelResponse.class);

        try {
            ChannelResponse result = resultMono.block();
            if(result != null) {
                table.setUUID(result.channel().id());
            } else throw new NullPointerException("Something went wrong while creating the table");
        } catch (WebClientResponseException e) {
            throw e;
        }
        return meta.cacheAndMetaSave(table);
    }

    private CreateServerChannel createServerChannel(String name, String description) {
        return new CreateServerChannel(name.toLowerCase(), "chat", description, visibility, serverId);
    }

    @Override
    public GuildedTable updateTable(String oldName, GuildedTable updated) {
        return updateTable(oldName, updated.getName(), updated.getDescription());
    }

    @Override
    public GuildedTable updateTable(String oldName, String newName, String newDescription) {
        checkMetaData(oldName);
        checkMetaData(newName);

        GuildedTable table = meta.getCachedTable(oldName.toLowerCase());

        newName = newName == null || newName.isBlank() ? oldName : newName;
        newDescription = newDescription == null || newDescription.isBlank() ? (
                table.getDescription() == null ? "no-description" : table.getDescription()
                ) : newDescription;

        Mono<UpdateServerChannel> requestMono = Mono.just(updateServerChannel(newName, newDescription));
        Mono<ChannelResponse> responseMono = client.patch()
                .uri(CHANNEL+"/{id}", table.getUUID())
                .body(requestMono, UpdateServerChannel.class)
                .retrieve()
                .bodyToMono(ChannelResponse.class);

        try {
            ChannelResponse response = responseMono.block();
            if(response == null) {
                throw new RuntimeException("Something went wrong while creating the table");
            }
        } catch (WebClientResponseException e) {
            throw e;
        }

        return new GuildedTable(newName, newDescription);
    }

    public UpdateServerChannel updateServerChannel(String name, String desc) {
        return new UpdateServerChannel(name, desc, visibility);
    }

    @Override
    public boolean deleteTable(GuildedTable table) {
        return deleteTable(table.getName());
    }

    @Override
    public boolean deleteTable(String tableName) {
        StringHelper.nullOrBlank(tableName);
        checkMetaData(tableName);

        if(!meta.cacheContainsTable(tableName)) return false;

        GuildedTable table = meta.getCachedTable(tableName);

        Mono<Void> responseMono =  client.delete()
                .uri(CHANNEL + "/{id}", table.getUUID())
                .retrieve()
                .bodyToMono(Void.class);
        try {
            responseMono.block();
            meta.deleteAndMetaRemove(tableName);
            return true;
        } catch (WebClientResponseException e) {
            return false;
        }
    }

    @Override
    public boolean contains(String table, String key) {
        return get(table, key) != null;
    }

    @Override
    public List<String> get(String table, int limit) {
        if(!meta.cacheContainsTable(table)) throw new NoSuchElementException("Table " + table + " does not exist");
        GuildedTable tableObj = meta.getCachedTable(table);

        ArrayList<String> found = new ArrayList<>();
        ArrayList<String> results = new ArrayList<>();
        GuildedBuffer buf = new GuildedBuffer(limit, client, tableObj.getUUID(), false);
        while(found.size() < limit && buf.getEntries() != null && buf.getEntries().length > 0) {
            for(GuildedDataEntry entry : buf.getEntries()) {
                if(found.contains(entry.getKey())) continue;
                results.add(getContinuation(tableObj.getUUID(), entry));
                found.add(entry.getKey());
            }
            buf = new GuildedBuffer(100, client, tableObj.getUUID(), false, buf.getLastsDate());
        }

        return results;
    }

    @Override
    public List<String> get(String table) {
        return get(table, 50);
    }

    @Override
    public String get(String table, String key) {
        if(!meta.cacheContainsTable(table)) throw new NoSuchElementException("Table " + table + " does not exist");
        GuildedTable tableObj = meta.getCachedTable(table);

        GuildedDataEntry resultEntry = null;

        boolean found = false;
        GuildedBuffer buf = new GuildedBuffer(100, client, tableObj.getUUID(), false);
        while(!found && buf.getEntries() != null && buf.getEntries().length > 0) {
            for(GuildedDataEntry entry : buf.getEntries()) {
                if(entry.getKey().equals(key)) {
                    found = true;
                    resultEntry = entry;
                    break;
                }
            }
            buf = new GuildedBuffer(100, client, tableObj.getUUID(), false, buf.getLastsDate());
        }

        return getContinuation(tableObj.getUUID(), resultEntry);
    }

    @Override
    public List<String> filter(String table, int limit, GuildedFilter filter) {
        if(!meta.cacheContainsTable(table)) throw new NoSuchElementException("Table " + table + " does not exist");
        GuildedTable tableObj = meta.getCachedTable(table);

        ArrayList<String> found = new ArrayList<>();
        ArrayList<String> results = new ArrayList<>();
        GuildedBuffer buf = new GuildedBuffer(limit, client, tableObj.getUUID(), false);
        // Note that the difference here is that we track results size, since we do not want to parse
        // previous blocks of already found blocks since they're adjacent.
        // Additionally, sending partially parsed from previous node strings to filter may produce errors,
        // depends on the client's implementation.
        while(results.size() < limit && buf.getEntries() != null && buf.getEntries().length > 0) {
            for(GuildedDataEntry entry : buf.getEntries()) {
                if(found.contains(entry.getKey())) continue;
                found.add(entry.getKey());
                String current = getContinuation(tableObj.getUUID(), entry);
                if(filter.filter(current)) // The biggest change
                    results.add(current);
            }
            buf = new GuildedBuffer(100, client, tableObj.getUUID(), false, buf.getLastsDate());
        }

        return results;
    }

    @Override
    public void insert(String table, String key, String data) {
        if(contains(table, key)) throw new KeyAlreadyExistsException();
        if(data == null) throw new NullPointerException();

        GuildedTable tableObj = meta.getCachedTable(table);

        ArrayList<GuildedDataEntry> entries = new ArrayList<>();
        while(!data.isBlank()) {
            String content;
            if(data.length() + key.length() < MAX_CHUNK) {
                content = key + "~" + data;
                data = "";
            } else {
                content = key + "~" + data.substring(0, MAX_CHUNK - key.length() - 1);
            }
            entries.add(new GuildedDataEntry(key, content, true, null));
        }

        String last = null;
        for(GuildedDataEntry entry : entries) {
            last = saveDataEntry(tableObj.getUUID(), entry, last);
        }
    }

    private String saveDataEntry(String tableUUID, GuildedDataEntry entry, String prev) {
        Mono<CreateChatMessage> requestMono = Mono.just(
                new CreateChatMessage(!entry.isUser(), false, new String[]{prev}, entry.getData()));
        Mono<MessageResponse> responseMono = client.post()
                .uri(CHANNEL + "/{channelId}/" + MESSAGE, tableUUID)
                .bodyValue(CreateChatMessage.class)
                .retrieve()
                .bodyToMono(MessageResponse.class);
        MessageResponse response = responseMono.block();
        return response != null ? response.message().id() : null;
    }

    @Override
    public void update(String table, String key, String data) {
        delete(table, key);
        insert(table, key, data);
    }

    @Override
    public void updateKey(String table, String oldKey, String newKey) {
        if(contains(table, newKey)) throw new KeyAlreadyExistsException("New key already exists in the table for another record.");
        String content = get(table, oldKey);
        delete(table, oldKey);
        insert(table, newKey, content);
    }

    @Override
    public boolean delete(String table, String key) {
        try {
            if (!meta.cacheContainsTable(table)) return false;

            GuildedTable tableObj = meta.getCachedTable(table);

            GuildedBuffer buf = new GuildedBuffer(100, client, tableObj.getUUID(), false);
            while (buf.getEntries() != null && buf.getEntries().length > 0) {
                for(GuildedDataEntry entry : buf.getEntries()) {
                    if(entry.getUUID().equals(key)) return deleteContinuation(tableObj.getUUID(), entry);
                }
                buf = new GuildedBuffer(100, client, tableObj.getUUID(), false, buf.getLastsDate());
            }
        } catch (Exception ignored) {}
        return false;
    }

    /**
     * Retrieves the item in the database in its entirety. Since we might've
     * broken it down to chunks we need to append them all together.
     *
     * @implNote This code needs some optimization, but other than optimizing string concatenation there is not
     * much we can optimize without greatly impacting other functions. And what I mean by that, is that we've
     * probably already loaded those entries, where? well, in the "buffer", but there are cases where the buffer
     * contains only some of the chunks due to paging constraints. In that case we're really doomed, since we need to
     * load the next batch and in that case it's no better than this.
     * The idea to optimize it will probably be to separate entries to buckets by keys, then if the last one in the
     * bucket we're reading contains previous field which isn't null, then we call this, and append its result to
     * the so far built result.
     *
     * @param tableUUID table
     * @param entry entry
     * @return data of item.
     */
    private String getContinuation(String tableUUID, GuildedDataEntry entry) {
        String result = entry == null ? null :
                entry.getData().substring(entry.getData().indexOf("~"));

        while(entry != null && entry.getPrevious() != null) {
            entry = getItem(tableUUID, entry.getPrevious());
            result = entry.getData().substring(entry.getData().indexOf("~")).concat(result);
        }

        return result;
    }

    /**
     * Retrieves a single message given table and msg.
     *
     * @param tableUUID table
     * @param msgUUID msg
     * @return data entry.
     */
    private GuildedDataEntry getItem(String tableUUID, String msgUUID) {
        MessageResponse response = client.get()
                .uri(CHANNEL + "/{channelId}/" + MESSAGE + "/{msgId}", tableUUID, msgUUID)
                .retrieve()
                .bodyToMono(MessageResponse.class)
                .block();
        if(response == null) throw new RuntimeException("Ran into an issue while retrieving items");
        int firstTilda = response.message().content().indexOf("~");
        firstTilda = firstTilda == -1 ? 0 :  firstTilda;
        String prev = response.message().replyMessageIds() != null && response.message().replyMessageIds().length > 0 ?
                response.message().replyMessageIds()[0] : null;
        return new GuildedDataEntry(response.message().id(),
                response.message().content().substring(0, firstTilda),
                response.message().content().substring(firstTilda + 1),
                prev,
                response.message().isPrivate(),
                response.message().createdAt());
    }

    private boolean deleteContinuation(String tableUUID, GuildedDataEntry entry) {
        while(entry != null) {
            String myUUID = entry.getUUID();
            entry = entry.getPrevious() != null ? getItem(tableUUID, entry.getPrevious()) : null;
            deleteContinuationHelper(tableUUID, myUUID);
        }
        return true;
    }

    private void deleteContinuationHelper(String tableUUID, String msgUUID) {
        client.delete()
                .uri(CHANNEL + "/{channelId}/" + MESSAGE + "/{msgId}", tableUUID, msgUUID)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
