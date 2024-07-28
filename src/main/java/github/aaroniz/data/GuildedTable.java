package github.aaroniz.data;

import github.aaroniz.util.StringHelper;

/**
 * Abstracts the channel object of Guilded to serve as a table.
 */
public class GuildedTable {
    /* @IMPORTANT:
     * Remember to set:
     * { "type": "chat", "visibility": "private" }
     */

    public static final int MIN_STR = 1, MAX_NAME = 100, MAX_DESC = 512;

    /**
     * Once saved to the database will serve for retrieval.
     */
    private String uuid;
    /**
     * The table's name.
     * 1 <= name.length() <= 100.
     */
    private String name;
    /**
     * Table's description (optional).
     * 1 <= description <= 512.
     */
    private String description;

    public GuildedTable(String uuid, String name, String description) {
        this.uuid = uuid;
        this.name = StringHelper.checkLengthThrow(name, MIN_STR, MAX_NAME);
        this.description = description == null ? "no-description" : StringHelper.checkLengthThrow(description, MIN_STR, MAX_DESC);
    }

    public GuildedTable(String name, String description) {
        this(null, name, description);
    }

    public GuildedTable(String name) {
        this(name, null);
    }

    public String getUUID() {
        return uuid;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = StringHelper.checkLengthThrow(name, MIN_STR, MAX_NAME);;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : StringHelper.checkLengthThrow(description, MIN_STR, MAX_DESC);
    }
}
