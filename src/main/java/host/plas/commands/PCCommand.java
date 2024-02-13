package host.plas.commands;

import net.streamline.api.command.ModuleCommand;
import net.streamline.api.modules.ModuleUtils;
import net.streamline.api.savables.users.StreamlineUser;
import host.plas.StreamlineGroups;
import host.plas.savable.GroupManager;
import tv.quaint.utils.StringUtils;

import java.util.concurrent.ConcurrentSkipListSet;

public class PCCommand extends ModuleCommand {
    String errorsNoMessage;

    public PCCommand() {
        super(StreamlineGroups.getInstance(), "pc", "streamline.command.party.quick-chat", "pchat", "partychat", "partyc");

        errorsNoMessage = getCommandResource().getOrSetDefault("messages.errors.no-message", "&cYou must provide a message to send!");
    }

    @Override
    public void run(StreamlineUser streamlineUser, String[] strings) {
        if (strings.length > 0) {
            String message = StringUtils.argsToString(strings);
            GroupManager.chatParty(streamlineUser, streamlineUser, message);
        } else {
            ModuleUtils.sendMessage(streamlineUser, errorsNoMessage);
        }
    }

    @Override
    public ConcurrentSkipListSet<String> doTabComplete(StreamlineUser streamlineUser, String[] strings) {
        return new ConcurrentSkipListSet<>();
    }
}
