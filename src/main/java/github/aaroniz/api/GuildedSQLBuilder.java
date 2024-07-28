package github.aaroniz.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import github.aaroniz.data.GuildedTable;
import github.aaroniz.data.MetaManager;
import github.aaroniz.guilded.requests.CreateServerChannel;
import github.aaroniz.guilded.responses.ChannelResponse;
import github.aaroniz.util.JacksonConfig;
import github.aaroniz.util.StringHelper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static github.aaroniz.api.Constants.META;
import static github.aaroniz.api.Constants.PATH;

public class GuildedSQLBuilder {

    private String token;
    private String meta;
    private String database;
    private boolean saveMeta;
    private boolean isPrivate;

    public GuildedSQLBuilder() {
        this.isPrivate = false;
        this.saveMeta = false;
        this.database = null;
        this.token = null;
        this.meta = null;
    }

    /**
     * Guilded Access Token.
     *
     * @param token Guilded Access Token.
     * @return this.
     */
    public GuildedSQLBuilder token(String token) {
        if(this.token != null) throw new RuntimeException("Token has already been set.");
        StringHelper.nullOrBlank(token);
        this.token = token;
        return this;
    }

    /**
     * Allows to add the meta channel id.
     *
     * @param metaChannelId the meta channel id.
     * @param saveMeta whether to override the current id in the 'db_meta.json' file.
     * @return this.
     */
    public GuildedSQLBuilder metaChannel(String metaChannelId, boolean saveMeta) {
        if(this.meta != null) throw new RuntimeException("Meta channel id has already been set.");
        StringHelper.nullOrBlank(metaChannelId);
        this.meta = metaChannelId;
        this.saveMeta = saveMeta;
        return this;
    }

    /**
     * Required.
     *
     * @param connectionString The server id which is used as database.
     * @return this.
     */
    public GuildedSQLBuilder connectionString(String connectionString) {
        return setServerId(connectionString);
    }

    /**
     * Same as {@code connectionString(String)}
     *
     * @param serverId The server id which is used as database.
     * @return this.
     */
    public GuildedSQLBuilder setServerId(String serverId) {
        if(this.database != null) throw new RuntimeException("Server id has already been ser.");
        this.database = serverId;
        return this;
    }

    /**
     * If your Guilded server is private you must set this to true.
     *
     * @param isPrivate is the server is private.
     * @return this.
     */
    public GuildedSQLBuilder isPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
        return this;
    }

    /**
     * Builds an instance of an API to interact with this database.
     *
     * @return GuildedSQL instance.
     * @throws IOException if there was a problem while creating/reading 'db_meta.json'.
     */
    public GuildedSQL build() throws IOException {
        StringHelper.nullOrBlank(token);
        StringHelper.nullOrBlank(database);

        final WebClient client = createClient();
        final MetaManager metaManager = getMetaManager(client);
        final String visibility = isPrivate ? "public" : "private";

        metaManager.loadCacheFromMeta();


        return new GuildedSQLClient(client, metaManager, visibility, database);
    }

    private WebClient createClient() {
        return WebClient.builder()
                .baseUrl(PATH)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .codecs(configurer -> configurer.defaultCodecs().jackson2JsonEncoder(
                        new Jackson2JsonEncoder(new JacksonConfig().objectMapper())))
                .build();
    }

    private MetaManager getMetaManager(WebClient client) throws IOException {
        final ObjectMapper mapper = new JacksonConfig().objectMapper();
        final File file = new File("db_meta.json");

        boolean fileCreated = file.createNewFile();

        if(meta == null) {
            meta = fileCreated?
                createMeta(client, mapper, file):
                readMeta("meta", mapper, file);
        } else if (saveMeta) {
            saveMeta("meta", meta, mapper, file);
        }

        return new MetaManager(new HashMap<>(), client, new GuildedTable(meta, META, null));
    }

    private String readMeta(String param, ObjectMapper mapper, File file) {
        try {
            JsonNode node = mapper.readTree(file);
            return node.get(param).textValue();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void saveMeta(String param, String value, ObjectMapper mapper, File file) {
        try {
            JsonNode node = mapper.readTree(file.getAbsoluteFile());
            if (node instanceof ObjectNode objectNode) {
                objectNode.put(param, value);
                mapper.writeValue(file.getAbsoluteFile(), objectNode);
            } else {
                ObjectNode objNode = mapper.createObjectNode();
                objNode.put(param, value);
                mapper.writeValue(file.getAbsoluteFile(), objNode);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // better not.
    private String createMeta(WebClient client, ObjectMapper mapper, File file) {
        final String visibility = isPrivate ? "public" : "private";

        final CreateServerChannel request = new CreateServerChannel(META, "chat",
                "Contains meta data about the database for the usage of the reading head.",
                visibility, database);
        final Mono<CreateServerChannel> channelMono = Mono.just(request);
        final Mono<ChannelResponse> resultMono = client.post()
                .uri("channels")
                .body(channelMono, CreateServerChannel.class)
                .retrieve()
                .bodyToMono(ChannelResponse.class);
        final ChannelResponse result = resultMono.block();
        if(result != null) {
            String meta = result.channel().id();
            saveMeta("meta", meta, mapper, file);
            return meta;
        } else throw new NullPointerException("Something went wrong while creating meta table");
    }

    private record DbMeta(@JsonProperty("meta") String meta, @JsonProperty("server") String server) {};
}
