package github.aaroniz.api;

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
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static github.aaroniz.api.Constants.*;

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
        return false;
    }

    @Override
    public String get(String table, int limit) {
        return null;
    }

    @Override
    public String get(String table) {
        return null;
    }

    @Override
    public String get(String table, String key) {
        return null;
    }

    @Override
    public List<String> filter(String table, int limit, GuildedFilter filter) {
        return null;
    }

    @Override
    public void insert(String table, String key, String data) {

    }

    @Override
    public void update(String table, String key, String data) {

    }

    @Override
    public void updateKey(String table, String oldKey, String newKey) {

    }

    @Override
    public boolean delete(String table, String key) {
        return false;
    }

    private String getContinuation(String tableUUID, GuildedDataEntry entry) {
        String result = entry == null ? null :
                entry.getData().substring(entry.getData().indexOf("~"));

        while(entry != null && entry.getPrevious() != null) {
            entry = getItem(tableUUID, entry.getPrevious());
            result = entry.getData().substring(entry.getData().indexOf("~")).concat(result);
        }

        return result;
    }

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
}
