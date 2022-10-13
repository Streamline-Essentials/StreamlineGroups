package tv.quaint.configs;

import de.leonhard.storage.Config;
import net.streamline.api.configs.DatabaseConfig;
import net.streamline.api.configs.FlatFileResource;
import net.streamline.api.configs.ModularizedConfig;
import net.streamline.api.configs.StorageUtils;
import tv.quaint.StreamlineGroups;
import tv.quaint.savable.flags.GroupFlag;

import java.util.List;

public class Messages extends ModularizedConfig {
    public Messages() {
        super(StreamlineGroups.getInstance(), "messages.yml", true);
        init();
    }

    public void init() {
        levelTitleMain();
        levelTitleSub();
        levelTitleInTicks();
        levelTitleStayTicks();
        levelTitleOutTicks();

        levelChat();

        errorsBaseAlreadyExists();
        errorsBaseAlreadyInOther();
        errorsBaseAlreadyInSelf();
        errorsBaseCannotDemoteLeader();
        errorsBaseCannotDemoteSame();
        errorsBaseCannotDemoteSelf();
        errorsBaseCannotPromoteLeader();
        errorsBaseCannotPromoteSame();
        errorsBaseCannotPromoteSelf();
        errorsBaseNotExists();
        errorsBaseNotFlag();
        errorsBaseNotInOther();
        errorsBaseNotInSelf();
        errorsBaseNotInvited();
        
        guildsCreate();
        guildsAcceptMembers();
        guildsAcceptOther();
        guildsAcceptSender();
        guildsDenyMembers();
        guildsDenyOther();
        guildsDenySender();
        guildsChat();
        guildsDisbandLeader();
        guildsDisbandMembers();
        guildsDisbandSender();
        guildsLeaveMembers();
        guildsLeaveOther();
        guildsLeaveSender();
        guildsLeaveMembers();
        guildsListMain();
        guildsListRole();
        guildsTimeoutInviteMembers();
        guildsTimeoutInviteSender();
        guildsTimeoutInviteOther();
        guildsRenameMembers();
        guildsRenameOther();
        guildsRenameSender();
        
        partiesAcceptMembers();
        partiesAcceptOther();
        partiesAcceptSender();
        partiesDenyMembers();
        partiesDenyOther();
        partiesDenySender();
        partiesChat();
        partiesDisbandLeader();
        partiesDisbandMembers();
        partiesDisbandSender();
        partiesLeaveMembers();
        partiesLeaveOther();
        partiesLeaveSender();
        partiesLeaveMembers();
        partiesListMain();
        partiesListRole();
        partiesTimeoutInviteMembers();
        partiesTimeoutInviteSender();
        partiesTimeoutInviteOther();
    }

    public String errorsBaseAlreadyExists() {
        reloadResource();

        return resource.getOrSetDefault("errors.base.already.exists", "&cThat group already exists!");
    }

    public String errorsBaseAlreadyInSelf() {
        reloadResource();

        return resource.getOrSetDefault("errors.base.already.in.self", "&cYou are already in a group!");
    }

    public String errorsBaseAlreadyInOther() {
        reloadResource();

        return resource.getOrSetDefault("errors.base.already.in.other", "&cThat player is already in a group!");
    }

    public String errorWithoutFlag(GroupFlag flag) {
        return errorsBaseNotFlag().replace("%this_flag%", flag.toString());
    }

    public String errorsBaseNotFlag() {
        reloadResource();

        return resource.getOrSetDefault("errors.base.not.flag", "&cYou do not have the %this_flag% flag in this group!");
    }

    public String errorsBaseNotInvited() {
        reloadResource();

        return resource.getOrSetDefault("errors.base.not.invited", "&cThat user is not invited to this group!");
    }

    public String errorsBaseNotExists() {
        reloadResource();

        return resource.getOrSetDefault("errors.base.not.exists", "&cThat group does not exist!");
    }

    public String errorsBaseNotInSelf() {
        reloadResource();

        return resource.getOrSetDefault("errors.base.not.in.self", "&cYou are not in a group!");
    }

    public String errorsBaseNotInOther() {
        reloadResource();

        return resource.getOrSetDefault("errors.base.not.in.other", "&cThat player is not in a group!");
    }

    public String errorsBaseCannotPromoteSelf() {
        reloadResource();

        return resource.getOrSetDefault("errors.base.cannot.promote.self", "&cYou cannot promote yourself!");
    }

    public String errorsBaseCannotPromoteLeader() {
        reloadResource();

        return resource.getOrSetDefault("errors.base.cannot.promote.leader", "&cThis player is already leader!");
    }

    public String errorsBaseCannotPromoteSame() {
        reloadResource();

        return resource.getOrSetDefault("errors.base.cannot.promote.same", "&cYou are of the same role as this player!");
    }

    public String errorsBaseCannotDemoteSelf() {
        reloadResource();

        return resource.getOrSetDefault("errors.base.cannot.demote.self", "&cYou cannot demote yourself!");
    }

    public String errorsBaseCannotDemoteLeader() {
        reloadResource();

        return resource.getOrSetDefault("errors.base.cannot.demote.leader", "&cThis player is a leader!");
    }

    public String errorsBaseCannotDemoteSame() {
        reloadResource();

        return resource.getOrSetDefault("errors.base.cannot.demote.same", "&cYou are of the same role as this player!");
    }

    public String guildsCreate() {
        reloadResource();

        return resource.getOrSetDefault("guilds.create", "&eYou just created a guild named &r%groups_guild_name%&8!");
    }

    public String guildsSendInviteSender() {
        reloadResource();

        return resource.getOrSetDefault("guilds.invite.send.sender", "&eJust invited %streamline_parse_%this_other%:::*/*streamline_user_formatted*/*% &eto %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es guild&8!");
    }

    public String guildsSendInviteMembers() {
        reloadResource();

        return resource.getOrSetDefault("guilds.invite.send.members", "%streamline_parse_%this_sender%:::*/*streamline_user_formatted*/*% &ejust invited %streamline_parse_%this_other%:::*/*streamline_user_formatted*/*% &eto %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es guild&8!");
    }

    public String guildsSendInviteOther() {
        reloadResource();

        return resource.getOrSetDefault("guilds.invite.send.other", "%streamline_parse_%this_sender%:::*/*streamline_user_formatted*/*% &ejust invited &dyou &eto %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es guild&8!");
    }

    public String guildsTimeoutInviteSender() {
        reloadResource();

        return resource.getOrSetDefault("guilds.invite.timeout.sender", "&eInvite to %streamline_parse_%this_other%:::*/*streamline_user_formatted*/*% &eto %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es guild &c&lexpired&8!");
    }

    public String guildsTimeoutInviteMembers() {
        reloadResource();

        return resource.getOrSetDefault("guilds.invite.timeout.members", "%streamline_parse_%this_sender%:::*/*streamline_user_formatted*/*%&8'&es invite to %streamline_parse_%this_other%:::*/*streamline_user_formatted*/*% &eto %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es guild &c&lexpired&8!");
    }

    public String guildsTimeoutInviteOther() {
        reloadResource();

        return resource.getOrSetDefault("guilds.invite.timeout.other", "%streamline_parse_%this_sender%:::*/*streamline_user_formatted*/*%&8'&es invite to &dyou &eto %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es guild &c&lexpired&8!");
    }

    public String guildsAcceptSender() {
        reloadResource();

        return resource.getOrSetDefault("guilds.accept.sender", "&dYou &aaccepted %streamline_parse_%this_sender%:::*/*streamline_user_formatted*/*%&8'&es invite &eto %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es guild&8!");
    }

    public String guildsAcceptMembers() {
        reloadResource();

        return resource.getOrSetDefault("guilds.accept.members", "%streamline_parse_%this_other%:::*/*streamline_user_formatted*/*% &aaccepted &ethe invite from %streamline_parse_%this_sender%:::*/*streamline_user_formatted*/*%&8 &eto %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es guild&8!");
    }

    public String guildsAcceptOther() {
        reloadResource();

        return resource.getOrSetDefault("guilds.accept.other", "%streamline_parse_%this_other%:::*/*streamline_user_formatted*/*% &aaccepted &ethe invite to %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es guild&8!");
    }

    public String guildsDenySender() {
        reloadResource();

        return resource.getOrSetDefault("guilds.deny.sender", "&dYou &cdenied %streamline_parse_%this_sender%:::*/*streamline_user_formatted*/*%&8'&es invite &eto %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es guild&8!");
    }

    public String guildsDenyMembers() {
        reloadResource();

        return resource.getOrSetDefault("guilds.deny.members", "%streamline_parse_%this_other%:::*/*streamline_user_formatted*/*% &cdenied &ethe invite from %streamline_parse_%this_sender%:::*/*streamline_user_formatted*/*%&8 &eto %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es guild&8!");
    }

    public String guildsDenyOther() {
        reloadResource();

        return resource.getOrSetDefault("guilds.deny.other", "%streamline_parse_%this_other%:::*/*streamline_user_formatted*/*% &cdenied &ethe invite to %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es guild&8!");
    }

    public String guildsDisbandSender() {
        reloadResource();

        return resource.getOrSetDefault("guilds.disband.sender", "&dYou &cdisbanded %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es guild&8!");
    }

    public String guildsDisbandMembers() {
        reloadResource();

        return resource.getOrSetDefault("guilds.disband.members", "%streamline_parse_%this_sender%:::*/*streamline_user_formatted*/*% &cdisbanded %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es guild&8!");
    }

    public String guildsDisbandLeader() {
        reloadResource();

        return resource.getOrSetDefault("guilds.disband.leader", "%streamline_parse_%this_sender%:::*/*streamline_user_formatted*/*% &cdisbanded &dyour &eguild&8!");
    }

    public String guildsPromoteSender() {
        reloadResource();

        return resource.getOrSetDefault("guilds.promote.sender", "&dYou &apromoted %streamline_parse_%this_other%:::*/*streamline_user_formatted*/*% &ein %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es guild&8!");
    }

    public String guildsPromoteMembers() {
        reloadResource();

        return resource.getOrSetDefault("guilds.promote.members", "%streamline_parse_%this_sender%:::*/*streamline_user_formatted*/*% &apromoted %streamline_parse_%this_other%:::*/*streamline_user_formatted*/*% &ein %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es guild&8!");
    }

    public String guildsPromoteOther() {
        reloadResource();

        return resource.getOrSetDefault("guilds.promote.other", "%streamline_parse_%this_sender%:::*/*streamline_user_formatted*/*% &apromoted &dyou &ein %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es &eguild&8!");
    }

    public String guildsDemoteSender() {
        reloadResource();

        return resource.getOrSetDefault("guilds.demote.sender", "&dYou &cdemoted %streamline_parse_%this_other%:::*/*streamline_user_formatted*/*% &ein %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es guild&8!");
    }

    public String guildsDemoteMembers() {
        reloadResource();

        return resource.getOrSetDefault("guilds.demote.members", "%streamline_parse_%this_sender%:::*/*streamline_user_formatted*/*% &cdemoted %streamline_parse_%this_other%:::*/*streamline_user_formatted*/*% &ein %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es guild&8!");
    }

    public String guildsDemoteOther() {
        reloadResource();

        return resource.getOrSetDefault("guilds.demote.other", "%streamline_parse_%this_sender%:::*/*streamline_user_formatted*/*% &cdemoted &dyou &ein %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es &eguild&8!");
    }

    public String guildsLeaveSender() {
        reloadResource();

        return resource.getOrSetDefault("guilds.leave.sender", "&dYou &cremoved %streamline_parse_%this_other%:::*/*streamline_user_formatted*/*% &efrom %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es guild&8!");
    }

    public String guildsLeaveMembers() {
        reloadResource();

        return resource.getOrSetDefault("guilds.leave.members", "%streamline_parse_%this_other%:::*/*streamline_user_formatted*/*% &cleft %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es guild&8!");
    }

    public String guildsLeaveOther() {
        reloadResource();

        return resource.getOrSetDefault("guilds.leave.other", "&dYou &cleft %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es &eguild&8!");
    }

    public String guildsListMain() {
        reloadResource();

        return resource.getOrSetDefault("guilds.list.main", "&7&l----+ &5Guild List &7+----%this_for_roles%");
    }

    public String guildsListRole() {
        reloadResource();

        return resource.getOrSetDefault("guilds.list.role", "%newline%%this_role_name%&8: %this_role_members%");
    }

    public String guildsChat() {
        reloadResource();

        return resource.getOrSetDefault("parties.chat", "&5Guild &7&l> &d%streamline_parse_%this_sender%:::*/*streamline_user_formatted*/*%&8: &f%this_message%");
    }

    public String guildsRenameSender() {
        reloadResource();

        return resource.getOrSetDefault("guilds.rename.sender", "&dYou &erenamed %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es guild to &r%this_name_new% &efrom &r%this_name_old%&8!");
    }

    public String guildsRenameMembers() {
        reloadResource();

        return resource.getOrSetDefault("guilds.rename.members", "%streamline_parse_%this_sender%:::*/*streamline_user_formatted*/*% &erenamed %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es guild to &r%this_name_new% &efrom &r%this_name_old%&8!");
    }

    public String guildsRenameOther() {
        reloadResource();

        return resource.getOrSetDefault("guilds.rename.other", "%streamline_parse_%this_sender%:::*/*streamline_user_formatted*/*% &erenamed &dyour &eguild to &r%this_name_new% &efrom &r%this_name_old%&8!");
    }

    public String partiesCreate() {
        reloadResource();

        return resource.getOrSetDefault("parties.create", "&eYou just created a party&8!");
    }

    public String partiesSendInviteSender() {
        reloadResource();

        return resource.getOrSetDefault("parties.invite.send.sender", "&eJust invited %streamline_parse_%this_other%:::*/*streamline_user_formatted*/*% &eto %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es party&8!");
    }

    public String partiesSendInviteMembers() {
        reloadResource();

        return resource.getOrSetDefault("parties.invite.send.members", "%streamline_parse_%this_sender%:::*/*streamline_user_formatted*/*% &ejust invited %streamline_parse_%this_other%:::*/*streamline_user_formatted*/*% &eto %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es party&8!");
    }

    public String partiesSendInviteOther() {
        reloadResource();

        return resource.getOrSetDefault("parties.invite.send.other", "%streamline_parse_%this_sender%:::*/*streamline_user_formatted*/*% &ejust invited &dyou &eto %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es party&8!");
    }

    public String partiesTimeoutInviteSender() {
        reloadResource();

        return resource.getOrSetDefault("parties.invite.timeout.sender", "&eInvite to %streamline_parse_%this_other%:::*/*streamline_user_formatted*/*% &eto %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es party &c&lexpired&8!");
    }

    public String partiesTimeoutInviteMembers() {
        reloadResource();

        return resource.getOrSetDefault("parties.invite.timeout.members", "%streamline_parse_%this_sender%:::*/*streamline_user_formatted*/*%&8'&es invite to %streamline_parse_%this_other%:::*/*streamline_user_formatted*/*% &eto %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es party &c&lexpired&8!");
    }

    public String partiesTimeoutInviteOther() {
        reloadResource();

        return resource.getOrSetDefault("parties.invite.timeout.other", "%streamline_parse_%this_sender%:::*/*streamline_user_formatted*/*%&8'&es invite to &dyou &eto %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es party &c&lexpired&8!");
    }

    public String partiesAcceptSender() {
        reloadResource();

        return resource.getOrSetDefault("parties.accept.sender", "&dYou &aaccepted %streamline_parse_%this_sender%:::*/*streamline_user_formatted*/*%&8'&es invite &eto %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es party&8!");
    }

    public String partiesAcceptMembers() {
        reloadResource();

        return resource.getOrSetDefault("parties.accept.members", "%streamline_parse_%this_other%:::*/*streamline_user_formatted*/*% &aaccepted &ethe invite from %streamline_parse_%this_sender%:::*/*streamline_user_formatted*/*%&8 &eto %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es party&8!");
    }

    public String partiesAcceptOther() {
        reloadResource();

        return resource.getOrSetDefault("parties.accept.other", "%streamline_parse_%this_other%:::*/*streamline_user_formatted*/*% &aaccepted &ethe invite to %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es party&8!");
    }

    public String partiesDenySender() {
        reloadResource();

        return resource.getOrSetDefault("parties.deny.sender", "&dYou &cdenied %streamline_parse_%this_sender%:::*/*streamline_user_formatted*/*%&8'&es invite &eto %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es party&8!");
    }

    public String partiesDenyMembers() {
        reloadResource();

        return resource.getOrSetDefault("parties.deny.members", "%streamline_parse_%this_other%:::*/*streamline_user_formatted*/*% &cdenied &ethe invite from %streamline_parse_%this_sender%:::*/*streamline_user_formatted*/*%&8 &eto %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es party&8!");
    }

    public String partiesDenyOther() {
        reloadResource();

        return resource.getOrSetDefault("parties.deny.other", "%streamline_parse_%this_other%:::*/*streamline_user_formatted*/*% &cdenied &ethe invite to %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es party&8!");
    }

    public String partiesDisbandSender() {
        reloadResource();

        return resource.getOrSetDefault("parties.disband.sender", "&dYou &cdisbanded %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es party&8!");
    }

    public String partiesDisbandMembers() {
        reloadResource();

        return resource.getOrSetDefault("parties.disband.members", "%streamline_parse_%this_sender%:::*/*streamline_user_formatted*/*% &cdisbanded %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es party&8!");
    }

    public String partiesDisbandLeader() {
        reloadResource();

        return resource.getOrSetDefault("parties.disband.leader", "%streamline_parse_%this_sender%:::*/*streamline_user_formatted*/*% &cdisbanded &dyour &eparty&8!");
    }

    public String partiesPromoteSender() {
        reloadResource();

        return resource.getOrSetDefault("parties.promote.sender", "&dYou &apromoted %streamline_parse_%this_other%:::*/*streamline_user_formatted*/*% &ein %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es party&8!");
    }

    public String partiesPromoteMembers() {
        reloadResource();

        return resource.getOrSetDefault("parties.promote.members", "%streamline_parse_%this_sender%:::*/*streamline_user_formatted*/*% &apromoted %streamline_parse_%this_other%:::*/*streamline_user_formatted*/*% &ein %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es party&8!");
    }

    public String partiesPromoteOther() {
        reloadResource();

        return resource.getOrSetDefault("parties.promote.other", "%streamline_parse_%this_sender%:::*/*streamline_user_formatted*/*% &apromoted &dyou &ein %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es &eparty&8!");
    }

    public String partiesDemoteSender() {
        reloadResource();

        return resource.getOrSetDefault("parties.demote.sender", "&dYou &cdemoted %streamline_parse_%this_other%:::*/*streamline_user_formatted*/*% &ein %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es party&8!");
    }

    public String partiesDemoteMembers() {
        reloadResource();

        return resource.getOrSetDefault("parties.demote.members", "%streamline_parse_%this_sender%:::*/*streamline_user_formatted*/*% &cdemoted %streamline_parse_%this_other%:::*/*streamline_user_formatted*/*% &ein %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es party&8!");
    }

    public String partiesDemoteOther() {
        reloadResource();

        return resource.getOrSetDefault("parties.demote.other", "%streamline_parse_%this_sender%:::*/*streamline_user_formatted*/*% &cdemoted &dyou &ein %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es &eparty&8!");
    }

    public String partiesLeaveSender() {
        reloadResource();

        return resource.getOrSetDefault("parties.leave.sender", "&dYou &cremoved %streamline_parse_%this_other%:::*/*streamline_user_formatted*/*% &efrom %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es party&8!");
    }

    public String partiesLeaveMembers() {
        reloadResource();

        return resource.getOrSetDefault("parties.leave.members", "%streamline_parse_%this_other%:::*/*streamline_user_formatted*/*% &cleft %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es party&8!");
    }

    public String partiesLeaveOther() {
        reloadResource();

        return resource.getOrSetDefault("parties.leave.other", "&dYou &cleft %streamline_parse_%this_owner%:::*/*streamline_user_formatted*/*%&8'&es &eparty&8!");
    }

    public String partiesListMain() {
        reloadResource();

        return resource.getOrSetDefault("parties.list.main", "&7&l----+ &5Party List &7+----%this_for_roles%");
    }

    public String partiesListRole() {
        reloadResource();

        return resource.getOrSetDefault("parties.list.role", "%newline%%this_role_name%&8: %this_role_members%");
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

    public String levelTitleMain() {
        reloadResource();

        return resource.getOrSetDefault("level.on-change.title.main", "&2GUILD &bLEVEL UP");
    }

    public String levelTitleSub() {
        reloadResource();

        return resource.getOrSetDefault("level.on-change.title.subtitle", "&6Your guild is now level &a%streamline_user_level%&8!");
    }

    public int levelTitleInTicks() {
        reloadResource();

        return resource.getOrSetDefault("level.on-change.title.in", 20);
    }

    public int levelTitleStayTicks() {
        reloadResource();

        return resource.getOrSetDefault("level.on-change.title.stay", 10);
    }

    public int levelTitleOutTicks() {
        reloadResource();

        return resource.getOrSetDefault("level.on-change.title.out", 20);
    }

    public List<String> levelChat() {
        reloadResource();

        return resource.getOrSetDefault("level.on-change.chat", List.of("&2GUILD &bLEVEL UP", "&6Your guild is now level &a%streamline_user_level%&8!"));
    }
}
