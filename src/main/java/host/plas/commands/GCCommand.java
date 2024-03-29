package host.plas.commands;

import host.plas.StreamlineGroups;
import host.plas.savable.GroupManager;
import net.streamline.api.command.ModuleCommand;
import net.streamline.api.modules.ModuleUtils;
import net.streamline.api.savables.users.StreamlineUser;
import tv.quaint.utils.StringUtils;

import java.util.concurrent.ConcurrentSkipListSet;

public class GCCommand extends ModuleCommand {
    String errorsNoMessage;

    public GCCommand() {
        super(StreamlineGroups.getInstance(), "gc", "streamline.command.guild.quick-chat", "gchat", "guildchat", "guildc");

        errorsNoMessage = getCommandResource().getOrSetDefault("messages.errors.no-message", "&cYou must provide a message to send!");
    }

    @Override
    public void run(StreamlineUser streamlineUser, String[] strings) {
        if (strings.length > 0) {
            String message = StringUtils.argsToString(strings);
            GroupManager.chatGuild(streamlineUser, streamlineUser, message);
        } else {
            ModuleUtils.sendMessage(streamlineUser, errorsNoMessage);
        }
    }

    @Override
    public ConcurrentSkipListSet<String> doTabComplete(StreamlineUser streamlineUser, String[] strings) {
        return new ConcurrentSkipListSet<>();
    }
}
