package github.aaroniz.data;

import github.aaroniz.util.StringHelper;

public class GuildedDataEntry {


    /**
     * The message uuid in the Guilded API.
     */
    private String uuid;
    /**
     * The key of this entry.
     */
    private String key;
    /**
     * The data of this entry.
     */
    private String data;
    /**
     * If the data is bigger than {@code MAX_CHUNK - key.length() - 1} than it'll be
     * split into chunks. Each chunk is appended to the previous one.
     * The first one, if no chunking is necessary this is null.
     */
    private String previous;

    public GuildedDataEntry(String uuid, String key, String data, String previous) {
        this.uuid = uuid;
        this.key = StringHelper.nullOrBlank(key);
        this.data = StringHelper.nullOrBlank(data);
        this.previous = previous;
    }

    public GuildedDataEntry(String key, String data) {
        this(null, key, data, null);
    }

    public String getUUID() {
        return uuid;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = StringHelper.nullOrBlank(key);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = StringHelper.nullOrBlank(data);
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }
}
