package github.aaroniz.api;

import github.aaroniz.data.GuildedTable;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.util.List;
import java.util.NoSuchElementException;

public interface GuildedSQL {

    /**
     * Creates a new table with the given name.
     *
     * @param name the table's name. {@code GuildedTable.MIN_STR <= name <= GuildedTable.MAX_NAME}
     * @return {@link GuildedTable} instance of the newly created.
     * @throws {@link KeyAlreadyExistsException} if a table with the given name already exists.
     */
    GuildedTable createTable(String name);

    /**
     * Creates a new table with the given name and the description.
     *
     * @param name the table's name. {@code GuildedTable.MIN_STR <= name <= GuildedTable.MAX_NAME}
     * @param description the table's description. {@code GuildedTable.MIN_STR <= name <= GuildedTable.MAX_DESC}
     * @return {@link GuildedTable} instance of the newly created.
     * @throws {@link KeyAlreadyExistsException} if a table with the given name already exists.
     */
    GuildedTable createTable(String name, String description);

    /**
     * Updates table with the name {@code oldName} according to the given instance.<br>
     * Note that if {@code updated.getName() == null || updated.getDescription() == null} the old name or description
     * will be kept.
     *
     * @param oldName the current name of the table which needs to be updated.
     * @param updated the updated instance containing the data of the table to update.
     * @return The updated instance with updated fields.
     * @throws {@link NoSuchElementException} if a table with the given {@oldName} doesn't exist.
     */
    GuildedTable updateTable(String oldName, GuildedTable updated);

    /**
     * Updates table with the name {@code oldName} to the given name and description.<br>
     * Note that if {@param newName == null || newDescription == null} the old name or description
     * will be kept.
     *
     * @param oldName the current name of the table which needs to be updated.
     * @param newName the new name, if null name will remain unchanged.
     * @param newDescription the new description, if null will remain unchanged.
     * @return {@link GuildedTable} instance with updated fields.
     * @throws {@link NoSuchElementException} if a table with the given {@oldName} doesn't exist.
     */
    GuildedTable updateTable(String oldName, String newName, String newDescription);

    /**
     * Drops the table.
     *
     * @param table the table to drop.
     * @return true if the table was deleted.
     */
    boolean deleteTable(GuildedTable table);

    /**
     * Drops the table.
     *
     * @param tableName the table's name to drop.
     * @return true if the table was deleted.
     */
    boolean deleteTable(String tableName);

    /**
     * Checks whether a key exists in the given table.
     *
     * @param table the table.
     * @param key the key to check.
     * @return true if exists, false otherwise.
     * @throws {@link NoSuchElementException} If the table doesn't exist.
     */
    boolean contains(String table, String key);

    /**
     * Retrieve a list of the last {@code limit} entries.
     *
     * @param table The table from which to query entries.
     * @param limit must be {@code limit <= Constants.MAX_LIMIT}
     * @return list of the last {@code limit} entries from {@code table}.
     * @throws {@link NoSuchElementException} If the table doesn't exist.
     */
    List<String> get(String table, int limit);

    /**
     * Retrieve a list of the last {@code < 50} entries.
     *
     * @param table the table from which to query entries.
     * @return list of the last {@code < 50} entries from {@code table}.
     * @throws {@link NoSuchElementException} If the table doesn't exist.
     */
    List<String> get(String table);

    /**
     * Retrieve a single record with the given key from the given table.
     *
     * @param table the table from which to query.
     * @param key the key of the entry.
     * @return the entry if exists, null otherwise.
     * @throws {@link NoSuchElementException} If the table of the key don't exist.
     */
    String get(String table, String key);

    /**
     * Retrieves a list of the first {@code limit} entries, where for each entry {@code e, filter(e) == true}.
     *
     * @param table the table from which to query entries.
     * @param limit must be {@code limit <= Constants.MAX_LIMIT}
     * @param filter user defined filter.
     * @return @throws {@link NoSuchElementException} If the table doesn't exist.
     */
    List<String> filter(String table, int limit, GuildedFilter filter);

    /**
     * Insert the given data to the given table with the given key.
     *
     * @param table the table.
     * @param key the key, must be unique.
     * @param data the data.
     * @throws {@link NoSuchElementException} If the table doesn't exist.
     * @throws {@link KeyAlreadyExistsException} if the key already exists in the table.
     * @throws {@link NullPointerException} if data is null.
     */
    void insert(String table, String key, String data);

    /**
     * Updates an entry in the given table with the given key.
     *
     * @param table the table.
     * @param key the key, must exist in the table.
     * @param data the data.
     * @throws {@link NoSuchElementException} If the table of the key don't exist.
     */
    void update(String table, String key, String data);

    /**
     * Updates the key of an entry in the given table.
     *
     * @param table the table.
     * @param oldKey must exist in the table.
     * @param newKey must not exist in the table.
     * @throws {@link NoSuchElementException} If the table of the key don't exist.
     * @throws {@link KeyAlreadyExistsException} if the key already exists in the table.
     */
    void updateKey(String table, String oldKey, String newKey);

    /**
     * Deletes an entry in the given table.
     *
     * @param table the table.
     * @param key the key.
     * @return true if successful, false otherwise.
     */
    boolean delete(String table, String key);
}
