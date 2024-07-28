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
     * If the data is bigger than {@code MAX_CHUNK - key.length()} than it'll be
     * split into chunks. Each chunk is appended to the previous one.
     * The first one, if no chunking is necessary this is null.
     */
    private String previous;
    /**
     * Messages with {@code isPrivate = true} else, it was made by this system.
     */
    private boolean isUser;
    /**
     * The date which the message / entry was sent / saved.
     */
    private String date;

    public GuildedDataEntry(String uuid, String key, String data, String previous, boolean isUser, String date) {
        this.uuid = uuid;
        this.key = key;
        this.data = StringHelper.nullOrBlank(data);
        this.previous = previous;
        this.isUser = isUser;
        this.date = date;
    }

    public GuildedDataEntry(String key, String data, boolean isUser, String date) {
        this(null, key, data, null, isUser, date);
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
        this.key = key;
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

    public boolean isUser() {
        return isUser;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
