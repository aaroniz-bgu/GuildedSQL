# User Instructions
GuildedSQL is an API which wraps the Guilded REST API, and utilizes a single server as a database. It was developed in order to store blog posts, and various display data for my blog.

It is highly discouraged to use in your projects as you put your data at risk and at the mercy of Guilded moderation team. If you decided to use it anyway, by using this system you agree to the [disclaimer](#disclaimer).

## Local Generated Files
Since Guilded REST API (atm) does not provide a method to retrieve `ServerChannel`s and `Group`s as a collection GuildedSQL keeps their identifiers in a designated channel `%metadata_guildedsql%` which means you may not have a channel with this name, but more importantly the id of this channel is saved locally. You may find it at:
```xpath
// TODO
```
If you delete this file, GuildedSQL will not be able to access tables and schemas created beforehand, and you will have to manually recreate it and make sure it's readable by GuildedSQL and has the `%metadata_guildedsql%` channel id. In case you're switching environments/ machines make sure to copy this file.

### Disclaimer
- The "**system**" or "**software**" refers to this repository and any build of it.
- The "**developers**", "**maintainers**", "**contributors**" refers to anyone who has been part of the making of GuildedSQL, including but not limited to aaroniz-bgu@github.

By using GuildedSQL, you acknowledge and agree that you are solely responsible for any harm or violations caused by your actions to any third party, including but not limited to violations of the Guilded Terms of Service (TOS). The developers, maintainers, and contributors of GuildedSQL are not liable for any actions you take while using this software. You agree to indemnify and hold harmless the developers, maintainers, and contributors of GuildedSQL from any claims, damages, or other liabilities arising from your use of the software.

Furthermore, you agree to compensate the developers, maintainers, and contributors for any harmful consequences resulting from your actions. The developers, maintainers, and contributors of GuildedSQL are not responsible for any damage caused to you by using GuildedSQL, including but not limited to loss of data, loss of customer data, or any other damages. You are solely responsible for the consequences of using GuildedSQL, and you agree that you will not pursue any legal claims against the developers, maintainers, and contributors of GuildedSQL in any court of law.