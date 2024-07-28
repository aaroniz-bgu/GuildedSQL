# GuildedSQL Interface

## Overview
The `GuildedSQL` interface provides methods to create, update, delete, and query tables and entries within your Guilded server. 

## Methods

### `createTable(String name)`
Creates a new table with the given name.

- **Parameters:**
  - `name`: The table's name. Must be between `GuildedTable.MIN_STR` and `GuildedTable.MAX_NAME`.
- **Returns:** `GuildedTable` instance of the newly created table.
- **Throws:** `KeyAlreadyExistsException` if a table with the given name already exists.

### `createTable(String name, String description)`
Creates a new table with the given name and description.

- **Parameters:**
  - `name`: The table's name. Must be between `GuildedTable.MIN_STR` and `GuildedTable.MAX_NAME`.
  - `description`: The table's description. Must be between `GuildedTable.MIN_STR` and `GuildedTable.MAX_DESC`.
- **Returns:** `GuildedTable` instance of the newly created table.
- **Throws:** `KeyAlreadyExistsException` if a table with the given name already exists.

### `updateTable(String oldName, GuildedTable updated)`
Updates a table with the name `oldName` using the provided `updated` instance. If the updated instance's name or description is `null`, the old values are retained.

- **Parameters:**
  - `oldName`: The current name of the table to update.
  - `updated`: The instance containing the updated data for the table.
- **Returns:** The updated `GuildedTable` instance.
- **Throws:** `NoSuchElementException` if a table with the given `oldName` doesn't exist.

### `updateTable(String oldName, String newName, String newDescription)`
Updates a table with the name `oldName` to the given `newName` and `newDescription`. If `newName` or `newDescription` is `null`, the old values are retained.

- **Parameters:**
  - `oldName`: The current name of the table to update.
  - `newName`: The new name for the table. If `null`, the name remains unchanged.
  - `newDescription`: The new description for the table. If `null`, the description remains unchanged.
- **Returns:** The updated `GuildedTable` instance.
- **Throws:** `NoSuchElementException` if a table with the given `oldName` doesn't exist.

### `deleteTable(GuildedTable table)`
Deletes the specified table.

- **Parameters:**
  - `table`: The table to delete.
- **Returns:** `true` if the table was successfully deleted, `false` otherwise.

### `deleteTable(String tableName)`
Deletes the table with the specified name.

- **Parameters:**
  - `tableName`: The name of the table to delete.
- **Returns:** `true` if the table was successfully deleted, `false` otherwise.

### `contains(String table, String key)`
Checks whether a key exists in the specified table.

- **Parameters:**
  - `table`: The table to check.
  - `key`: The key to check for.
- **Returns:** `true` if the key exists, `false` otherwise.
- **Throws:** `NoSuchElementException` if the table doesn't exist.

### `get(String table, int limit)`
Retrieves a list of the last `limit` entries from the specified table.

- **Parameters:**
  - `table`: The table from which to retrieve entries.
  - `limit`: The number of entries to retrieve. Must be less than or equal to `Constants.MAX_LIMIT`.
- **Returns:** A list of the last `limit` entries from the table.
- **Throws:** `NoSuchElementException` if the table doesn't exist.

### `get(String table)`
Retrieves a list of the last 50 entries from the specified table.

- **Parameters:**
  - `table`: The table from which to retrieve entries.
- **Returns:** A list of the last 50 entries from the table.
- **Throws:** `NoSuchElementException` if the table doesn't exist.

### `get(String table, String key)`
Retrieves a single entry with the specified key from the specified table.

- **Parameters:**
  - `table`: The table from which to retrieve the entry.
  - `key`: The key of the entry to retrieve.
- **Returns:** The entry if it exists, `null` otherwise.
- **Throws:** `NoSuchElementException` if the table or key doesn't exist.

### `filter(String table, int limit, GuildedFilter filter)`
Retrieves a list of the first `limit` entries from the specified table that match the user-defined filter.

- **Parameters:**
  - `table`: The table from which to retrieve entries.
  - `limit`: The number of entries to retrieve. Must be less than or equal to `Constants.MAX_LIMIT`.
  - `filter`: The user-defined filter to apply.
- **Returns:** A list of the filtered entries.
- **Throws:** `NoSuchElementException` if the table doesn't exist.

### `insert(String table, String key, String data)`
Inserts the specified data into the specified table with the specified key.

- **Parameters:**
  - `table`: The table to insert the data into.
  - `key`: The key for the data. Must be unique.
  - `data`: The data to insert.
- **Throws:**
  - `NoSuchElementException` if the table doesn't exist.
  - `KeyAlreadyExistsException` if the key already exists in the table.

### `update(String table, String key, String data)`
Updates an entry in the specified table with the specified key.

- **Parameters:**
  - `table`: The table containing the entry to update.
  - `key`: The key of the entry to update. Must exist in the table.
  - `data`: The new data for the entry.
- **Throws:** `NoSuchElementException` if the table or key doesn't exist.

### `updateKey(String table, String oldKey, String newKey)`
Updates the key of an entry in the specified table.

- **Parameters:**
  - `table`: The table containing the entry to update.
  - `oldKey`: The current key of the entry. Must exist in the table.
  - `newKey`: The new key for the entry. Must not exist in the table.
- **Throws:**
  - `NoSuchElementException` if the table or old key doesn't exist.
  - `KeyAlreadyExistsException` if the new key already exists in the table.

### `delete(String table, String key)`
Deletes an entry with the specified key from the specified table.

- **Parameters:**
  - `table`: The table containing the entry to delete.
  - `key`: The key of the entry to delete.
- **Returns:** `true` if the entry was successfully deleted, `false` otherwise.
