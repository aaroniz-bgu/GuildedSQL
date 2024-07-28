package github.aaroniz.data;

import github.aaroniz.guilded.models.ChatMessage;
import github.aaroniz.guilded.models.ServerChannel;
import github.aaroniz.guilded.requests.CreateChatMessage;

import static github.aaroniz.api.Constants.META;
import static github.aaroniz.util.StringHelper.getFirstTilda;

public class Mapper {

    public static GuildedDataEntry map(ChatMessage msg) {
        return map(msg, false);
    }

    public static GuildedDataEntry map(ChatMessage msg, boolean meta) {
        final int firstTilda = getFirstTilda(msg.content());
        final String prev = msg.replyMessageIds() != null && msg.replyMessageIds().length > 0 ?
                msg.replyMessageIds()[0] : null;

        final String key = meta ? META : msg.content().substring(0, firstTilda);
        final String ctx = meta ? msg.content() : msg.content().substring(firstTilda + 1);

        return new GuildedDataEntry(msg.id(), key, ctx, prev, !msg.isSilent(), msg.createdAt());
    }

    public static CreateChatMessage map(GuildedDataEntry entry) {
        final String[] prev = entry.getPrevious() == null ? null : new String[]{entry.getPrevious()};
        return new CreateChatMessage(false, !entry.isUser(), prev, entry.getData());
    }

    public static GuildedTable map(ServerChannel channel) {
        final String desc = channel.topic() == null ? "no-desc" : channel.topic();
        return new GuildedTable(channel.id(), channel.name(), desc);
    }
}
