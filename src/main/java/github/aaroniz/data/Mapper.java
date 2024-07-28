package github.aaroniz.data;

import github.aaroniz.guilded.models.ChatMessage;
import github.aaroniz.guilded.models.ServerChannel;
import github.aaroniz.guilded.requests.CreateChatMessage;

import static github.aaroniz.util.StringHelper.getFirstTilda;

public class Mapper {
    public static GuildedDataEntry map(ChatMessage msg) {
        final int firstTilda = getFirstTilda(msg.content());
        String prev = msg.replyMessageIds() != null && msg.replyMessageIds().length > 0 ?
                msg.replyMessageIds()[0] : null;
        return new GuildedDataEntry(msg.id(),
                msg.content().substring(0, firstTilda), msg.content().substring(firstTilda),
                prev, !msg.isSilent(), msg.createdAt());
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
