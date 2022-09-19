package tv.quaint.configs;

import de.leonhard.storage.Config;
import net.streamline.api.configs.DatabaseConfig;
import net.streamline.api.configs.FlatFileResource;
import net.streamline.api.configs.ModularizedConfig;
import net.streamline.api.configs.StorageUtils;
import tv.quaint.StreamlineGroups;
import tv.quaint.savable.flags.GroupFlag;

public class Messages extends ModularizedConfig {
    public Messages() {
        super(StreamlineGroups.getInstance(), "messages.yml", true);
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

    public String errorWithoutFlag(GroupFlag flag) {
        return errorsBaseNotFlag().replace("%this_flag%", flag.toString());
    }

    public String errorsBaseNotFlag() {
        reloadResource();

        return resource.getString("errors.base.not.flag");
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

    public String errorsBaseCannotPromoteSelf() {
        reloadResource();

        return resource.getString("errors.base.cannot.promote.self");
    }

    public String errorsBaseCannotPromoteLeader() {
        reloadResource();

        return resource.getString("errors.base.cannot.promote.leader");
    }

    public String errorsBaseCannotPromoteSame() {
        reloadResource();

        return resource.getString("errors.base.cannot.promote.same");
    }

    public String errorsBaseCannotDemoteSelf() {
        reloadResource();

        return resource.getString("errors.base.cannot.demote.self");
    }

    public String errorsBaseCannotDemoteLeader() {
        reloadResource();

        return resource.getString("errors.base.cannot.demote.leader");
    }

    public String errorsBaseCannotDemoteSame() {
        reloadResource();

        return resource.getString("errors.base.cannot.demote.same");
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

    public String guildsListMain() {
        reloadResource();

        return resource.getString("guilds.list.main");
    }

    public String guildsListRole() {
        reloadResource();

        return resource.getString("guilds.list.role");
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

    public String guildsPromoteSender() {
        reloadResource();

        return resource.getString("guilds.promote.sender");
    }

    public String guildsPromoteMembers() {
        reloadResource();

        return resource.getString("guilds.promote.members");
    }

    public String guildsPromoteOther() {
        reloadResource();

        return resource.getString("guilds.promote.other");
    }

    public String guildsDemoteSender() {
        reloadResource();

        return resource.getString("guilds.demote.sender");
    }

    public String guildsDemoteMembers() {
        reloadResource();

        return resource.getString("guilds.demote.members");
    }

    public String guildsDemoteOther() {
        reloadResource();

        return resource.getString("guilds.demote.other");
    }

    public String guildsLeaveSender() {
        reloadResource();

        return resource.getString("guilds.leave.sender");
    }

    public String guildsLeaveMembers() {
        reloadResource();

        return resource.getString("guilds.leave.members");
    }

    public String guildsLeaveOther() {
        reloadResource();

        return resource.getString("guilds.leave.other");
    }

    public String guildsChat() {
        reloadResource();

        return resource.getString("parties.chat");
    }

    public String guildsRenameSender() {
        reloadResource();

        return resource.getString("guilds.rename.sender");
    }

    public String guildsRenameMembers() {
        reloadResource();

        return resource.getString("guilds.rename.members");
    }

    public String guildsRenameOther() {
        reloadResource();

        return resource.getString("guilds.rename.other");
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

    public String partiesListMain() {
        reloadResource();

        return resource.getString("parties.list.main");
    }

    public String partiesListRole() {
        reloadResource();

        return resource.getString("parties.list.role");
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

    public String partiesPromoteSender() {
        reloadResource();

        return resource.getString("parties.promote.sender");
    }

    public String partiesPromoteMembers() {
        reloadResource();

        return resource.getString("parties.promote.members");
    }

    public String partiesPromoteOther() {
        reloadResource();

        return resource.getString("parties.promote.other");
    }

    public String partiesDemoteSender() {
        reloadResource();

        return resource.getString("parties.demote.sender");
    }

    public String partiesDemoteMembers() {
        reloadResource();

        return resource.getString("parties.demote.members");
    }

    public String partiesDemoteOther() {
        reloadResource();

        return resource.getString("parties.demote.other");
    }

    public String partiesLeaveSender() {
        reloadResource();

        return resource.getString("parties.leave.sender");
    }

    public String partiesLeaveMembers() {
        reloadResource();

        return resource.getString("parties.leave.members");
    }

    public String partiesLeaveOther() {
        reloadResource();

        return resource.getString("parties.leave.other");
    }

    public String partiesChat() {
        reloadResource();

        return resource.getOrSetDefault("parties.chat", "&5Party &7&l> &d%streamline_parse_%this_sender%:::*/*streamline_user_formatted*/*%&8: &f%this_message%");
    }

    public String placeholdersGuildNotFound() {
        reloadResource();

        return resource.getOrSetDefault("placeholders.guild.not-found", "&cNo Guild Found");
    }

    public String placeholdersPartyNotFound() {
        reloadResource();

        return resource.getOrSetDefault("placeholders.party.not-found", "&cNo Party Found");
    }
}
