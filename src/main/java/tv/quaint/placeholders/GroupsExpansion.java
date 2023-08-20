package tv.quaint.placeholders;

import net.streamline.api.SLAPI;
import net.streamline.api.interfaces.IStreamline;
import net.streamline.api.messages.answered.ReturnableMessage;
import net.streamline.api.messages.builders.ProxyParseMessageBuilder;
import net.streamline.api.modules.ModuleUtils;
import net.streamline.api.objects.AtomicString;
import net.streamline.api.placeholders.expansions.RATExpansion;
import net.streamline.api.placeholders.replaceables.IdentifiedReplaceable;
import net.streamline.api.placeholders.replaceables.IdentifiedUserReplaceable;
import net.streamline.api.savables.users.StreamlinePlayer;
import net.streamline.api.savables.users.StreamlineUser;
import net.streamline.api.utils.UserUtils;
import tv.quaint.StreamlineGroups;
import tv.quaint.savable.GroupManager;
import tv.quaint.savable.GroupedUser;
import tv.quaint.savable.SavableGroup;
import tv.quaint.savable.SavableGroupRole;
import tv.quaint.savable.guilds.SavableGuild;
import tv.quaint.savable.parties.SavableParty;
import tv.quaint.utils.MatcherUtils;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class GroupsExpansion extends RATExpansion {

    public GroupsExpansion() {
        super(new RATExpansionBuilder("groups"));
    }

    @Override
    public void init() {
        new IdentifiedReplaceable(this, "guild_default_level", (s) -> String.valueOf(StreamlineGroups.getConfigs().guildStartingLevel())).register();
        new IdentifiedReplaceable(this, "guild_default_xp", (s) -> String.valueOf(StreamlineGroups.getConfigs().guildStartingExperienceAmount())).register();

        new IdentifiedReplaceable(this, "loaded_groups", (s) -> String.valueOf(GroupManager.getLoadedGroups().size())).register();
        new IdentifiedReplaceable(this, "loaded_guilds", (s) -> String.valueOf(GroupManager.getGroupsOf(SavableGuild.class).size())).register();
        new IdentifiedReplaceable(this, "loaded_parties", (s) -> String.valueOf(GroupManager.getGroupsOf(SavableParty.class).size())).register();

        new IdentifiedReplaceable(this, "loaded_users", (s) -> String.valueOf(GroupManager.getLoadedGroupedUsers().size())).register();
        new IdentifiedReplaceable(this, "loaded_grouped_users", (s) -> String.valueOf(GroupManager.getLoadedGroupedUsers().size())).register();

        new IdentifiedUserReplaceable(this, MatcherUtils.makeLiteral("guild_") + "(.*?)", 1, (s, u) -> {
            GroupedUser groupedUser = GroupManager.getOrGetGroupedUser(u.getUuid());
            SavableGuild guild = groupedUser.getGroup(SavableGuild.class);

            if (guild == null) {
                return StreamlineGroups.getMessages().placeholdersGuildNotFound();
            }

            String string = startsWithGuild(s.get(), guild, u);
            return string == null ? s.string() : string;
        }).register();

        new IdentifiedUserReplaceable(this, MatcherUtils.makeLiteral("party_") + "(.*?)", 1, (s, u) -> {
            GroupedUser user = GroupManager.getOrGetGroupedUser(u.getUuid());
            SavableParty party = user.getGroup(SavableParty.class);

            if (party == null) {
                return StreamlineGroups.getMessages().placeholdersPartyNotFound();
            }

            String string = startsWithParty(s.get(), party, u);
            return string == null ? s.string() : string;
        }).register();
    }

    public String startsWithParty(String params, SavableParty party, StreamlineUser streamlineUser) {
        return startsWithGroup(params, party, streamlineUser);
    }

    public String startsWithGuild(String params, SavableGuild guild, StreamlineUser streamlineUser) {
        if (params.equals("level")) {
            return String.valueOf(guild.level);
        }
        if (params.equals("xp_total")) {
            return String.valueOf(guild.totalXP);
        }
        if (params.equals("xp_current")) {
            return String.valueOf(guild.currentXP);
        }
        if (params.equals("name")) {
            return guild.name;
        }
        return startsWithGroup(params, guild, streamlineUser);
    }

    public String startsWithGroup(String params, SavableGroup group, StreamlineUser streamlineUser) {
        if (params.startsWith("role_")) {
            SavableGroupRole role = group.getRole(streamlineUser);
            if (role == null) return null;
            if (params.equals("role_identifier")) {
                return String.valueOf(role.getIdentifier());
            }
            if (params.equals("role_name")) {
                return String.valueOf(role.getName());
            }
            if (params.equals("role_max")) {
                return String.valueOf(role.getMax());
            }
            if (params.equals("role_priority")) {
                return String.valueOf(role.getPriority());
            }
            if (params.equals("role_flags")) {
                return ModuleUtils.getListAsFormattedString(new ArrayList<>(role.getFlags()));
            }
        }
        if (params.equals("total_size")) {
            return String.valueOf(group.getAllUsers().size());
        }
        if (params.equals("size_max_current")) {
            return String.valueOf(group.maxSize);
        }
        if (params.equals("size_max_absolute")) {
            return String.valueOf(group.getMaxSize(group.owner));
        }
        if (params.equals("leader_absolute")) {
            return ModuleUtils.getAbsolute(group.owner);
        }
        if (params.equals("leader_formatted")) {
            return ModuleUtils.getFormatted(group.owner);
        }
        if (params.equals("leader_absolute_onlined")) {
            return ModuleUtils.getOffOnAbsolute(group.owner);
        }
        if (params.equals("leader_formatted_onlined")) {
            return ModuleUtils.getOffOnFormatted(group.owner);
        }
        return null;
    }
}
