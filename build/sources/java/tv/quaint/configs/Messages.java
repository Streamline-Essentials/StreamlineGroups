package tv.quaint.configs;

import de.leonhard.storage.Config;
import net.streamline.api.configs.DatabaseConfig;
import net.streamline.api.configs.FlatFileResource;
import net.streamline.api.configs.ModularizedConfig;
import net.streamline.api.configs.StorageUtils;
import net.streamline.api.modules.BundledModule;
import tv.quaint.StreamlineGroups;

public class Messages extends ModularizedConfig {
    public Messages(BundledModule module) {
        super(module, "messages.yml", true);
    }

    public String errorsBaseAlreadyExists() {
        reloadResource();

        return resource.getString("errors.base.already.exists");
    }

    public String errorsBaseAlreadyInSelf() {
        reloadResource();

        return resource.getString("errors.base.already.in.self");
    }

    public String errorsBaseAlreadyInOther() {
        reloadResource();

        return resource.getString("errors.base.already.in.other");
    }

    public String errorsBaseNotInvited() {
        reloadResource();

        return resource.getString("errors.base.not.invited");
    }

    public String errorsBaseNotExists() {
        reloadResource();

        return resource.getString("errors.base.not.exists");
    }

    public String errorsBaseNotInSelf() {
        reloadResource();

        return resource.getString("errors.base.not.in.self");
    }

    public String errorsBaseNotInOther() {
        reloadResource();

        return resource.getString("errors.base.not.in.other");
    }

    public String guildsCreate() {
        reloadResource();

        return resource.getString("guilds.create");
    }

    public String guildsSendInviteSender() {
        reloadResource();

        return resource.getString("guilds.invite.send.sender");
    }

    public String guildsSendInviteMembers() {
        reloadResource();

        return resource.getString("guilds.invite.send.members");
    }

    public String guildsSendInviteOther() {
        reloadResource();

        return resource.getString("guilds.invite.send.other");
    }

    public String guildsTimeoutInviteSender() {
        reloadResource();

        return resource.getString("guilds.invite.timeout.sender");
    }

    public String guildsTimeoutInviteMembers() {
        reloadResource();

        return resource.getString("guilds.invite.timeout.members");
    }

    public String guildsTimeoutInviteOther() {
        reloadResource();

        return resource.getString("guilds.invite.timeout.other");
    }

    public String guildsAcceptSender() {
        reloadResource();

        return resource.getString("guilds.accept.sender");
    }

    public String guildsAcceptMembers() {
        reloadResource();

        return resource.getString("guilds.accept.members");
    }

    public String guildsAcceptOther() {
        reloadResource();

        return resource.getString("guilds.accept.other");
    }

    public String guildsDenySender() {
        reloadResource();

        return resource.getString("guilds.deny.sender");
    }

    public String guildsDenyMembers() {
        reloadResource();

        return resource.getString("guilds.deny.members");
    }

    public String guildsDenyOther() {
        reloadResource();

        return resource.getString("guilds.deny.other");
    }

    public String guildsList() {
        reloadResource();

        return resource.getString("guilds.list");
    }

    public String guildsDisbandSender() {
        reloadResource();

        return resource.getString("guilds.disband.sender");
    }

    public String guildsDisbandMembers() {
        reloadResource();

        return resource.getString("guilds.disband.members");
    }

    public String guildsDisbandLeader() {
        reloadResource();

        return resource.getString("guilds.disband.leader");
    }

    public String partiesCreate() {
        reloadResource();

        return resource.getString("parties.create");
    }

    public String partiesSendInviteSender() {
        reloadResource();

        return resource.getString("parties.invite.send.sender");
    }

    public String partiesSendInviteMembers() {
        reloadResource();

        return resource.getString("parties.invite.send.members");
    }

    public String partiesSendInviteOther() {
        reloadResource();

        return resource.getString("parties.invite.send.other");
    }

    public String partiesTimeoutInviteSender() {
        reloadResource();

        return resource.getString("parties.invite.timeout.sender");
    }

    public String partiesTimeoutInviteMembers() {
        reloadResource();

        return resource.getString("parties.invite.timeout.members");
    }

    public String partiesTimeoutInviteOther() {
        reloadResource();

        return resource.getString("parties.invite.timeout.other");
    }

    public String partiesAcceptSender() {
        reloadResource();

        return resource.getString("parties.accept.sender");
    }

    public String partiesAcceptMembers() {
        reloadResource();

        return resource.getString("parties.accept.members");
    }

    public String partiesAcceptOther() {
        reloadResource();

        return resource.getString("parties.accept.other");
    }

    public String partiesDenySender() {
        reloadResource();

        return resource.getString("parties.deny.sender");
    }

    public String partiesDenyMembers() {
        reloadResource();

        return resource.getString("parties.deny.members");
    }

    public String partiesDenyOther() {
        reloadResource();

        return resource.getString("parties.deny.other");
    }

    public String partiesList() {
        reloadResource();

        return resource.getString("parties.list");
    }

    public String partiesDisbandSender() {
        reloadResource();

        return resource.getString("parties.disband.sender");
    }

    public String partiesDisbandMembers() {
        reloadResource();

        return resource.getString("parties.disband.members");
    }

    public String partiesDisbandLeader() {
        reloadResource();

        return resource.getString("parties.disband.leader");
    }
}
