package github.aaroniz.api;

import github.aaroniz.util.StringHelper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import static github.aaroniz.api.Constants.PATH;

public class GuildedSQLBuilder {

    private String token;
    private String meta;
    private String database;

    public GuildedSQLBuilder token(String token) {
        if(this.token != null) throw new RuntimeException("Token has already been set.");
        StringHelper.nullOrBlank(token);
        this.token = token;
        return this;
    }

    public GuildedSQLBuilder metaChannel(String metaChannelId) {
        if(this.meta != null) throw new RuntimeException("Meta channel id has already been set.");
        StringHelper.nullOrBlank(metaChannelId);
        this.meta = metaChannelId;
        return this;
    }

    public GuildedSQLBuilder connectionString(String connectionString) {
        return setServerId(connectionString);
    }

    public GuildedSQLBuilder setServerId(String serverId) {
        if(this.database != null) throw new RuntimeException("Server id has already been ser.");
        this.database = serverId;
        return this;
    }

    public GuildedSQL build() {
        StringHelper.nullOrBlank(token);
        WebClient client = WebClient.builder()
                .baseUrl(PATH)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        throw new UnsupportedOperationException();
        //return null;
    }
}
