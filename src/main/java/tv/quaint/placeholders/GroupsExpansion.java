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
import tv.quaint.savable.SavableGroupRole;
import tv.quaint.savable.guilds.SavableGuild;
import tv.quaint.savable.parties.SavableParty;
import tv.quaint.utils.MatcherUtils;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
            SavableGuild guild = GroupManager.getGroupOfUser(SavableGuild.class, u);
            if (guild == null) {
                return StreamlineGroups.getMessages().placeholdersGuildNotFound();
            }
            AtomicString string = new AtomicString(s.string());
            s.handledString().isolateIn(s.string()).forEach(str -> {
                string.set(startsWithGuild(str, guild, u));
            });
            return string.get() == null ? s.string() : string.get();
        }).register();

        new IdentifiedUserReplaceable(this, MatcherUtils.makeLiteral("guild_") + "(.*?)", 1, (s, u) -> {
            SavableParty party = GroupManager.getGroupOfUser(SavableParty.class, u);
            if (party == null) {
                return StreamlineGroups.getMessages().placeholdersPartyNotFound();
            }
            AtomicString string = new AtomicString(s.string());
            s.handledString().isolateIn(s.string()).forEach(str -> {
                string.set(startsWithParty(str, party, u));
            });
            return string.get() == null ? s.string() : string.get();
        }).register();
    }

    public String startsWithParty(String params, SavableParty party, StreamlineUser streamlineUser) {
        if (params.startsWith("party_role_")) {
            SavableGroupRole role = party.getRole(streamlineUser);
            if (role == null) return null;
            if (params.equals("party_role_identifier")) {
                return String.valueOf(role.getIdentifier());
            }
            if (params.equals("party_role_name")) {
                return String.valueOf(role.getName());
            }
            if (params.equals("party_role_max")) {
                return String.valueOf(role.getMax());
            }
            if (params.equals("party_role_priority")) {
                return String.valueOf(role.getPriority());
            }
            if (params.equals("party_role_flags")) {
                return ModuleUtils.getListAsFormattedString(role.getFlags().stream().toList());
            }
        }
        if (params.equals("party_total_size")) {
            return String.valueOf(party.getAllUsers().size());
        }
        if (params.equals("party_size_max_current")) {
            return String.valueOf(party.maxSize);
        }
        if (params.equals("party_size_max_absolute")) {
            return String.valueOf(party.getMaxSize(party.owner));
        }
        if (params.equals("party_leader_absolute")) {
            return ModuleUtils.getAbsolute(party.owner);
        }
        if (params.equals("party_leader_formatted")) {
            return ModuleUtils.getFormatted(party.owner);
        }
        if (params.equals("party_leader_absolute_onlined")) {
            return ModuleUtils.getOffOnAbsolute(party.owner);
        }
        if (params.equals("party_leader_formatted_onlined")) {
            return ModuleUtils.getOffOnFormatted(party.owner);
        }
        return null;
    }

    public String startsWithGuild(String params, SavableGuild guild, StreamlineUser streamlineUser) {
        if (params.equals("guild_level")) {
            return String.valueOf(guild.level);
        }
        if (params.equals("guild_xp_total")) {
            return String.valueOf(guild.totalXP);
        }
        if (params.equals("guild_xp_current")) {
            return String.valueOf(guild.currentXP);
        }
        if (params.equals("guild_name")) {
            return guild.name;
        }
        if (params.startsWith("guild_role_")) {
            SavableGroupRole role = guild.getRole(streamlineUser);
            if (role == null) return null;
            if (params.equals("guild_role_identifier")) {
                return String.valueOf(role.getIdentifier());
            }
            if (params.equals("guild_role_name")) {
                return String.valueOf(role.getName());
            }
            if (params.equals("guild_role_max")) {
                return String.valueOf(role.getMax());
            }
            if (params.equals("guild_role_priority")) {
                return String.valueOf(role.getPriority());
            }
            if (params.equals("guild_role_flags")) {
                return ModuleUtils.getListAsFormattedString(role.getFlags().stream().toList());
            }
        }
        if (params.equals("guild_total_size")) {
            return String.valueOf(guild.getAllUsers().size());
        }
        if (params.equals("guild_size_max_current")) {
            return String.valueOf(guild.maxSize);
        }
        if (params.equals("guild_size_max_absolute")) {
            return String.valueOf(guild.getMaxSize(guild.owner));
        }
        if (params.equals("guild_leader_absolute")) {
            return ModuleUtils.getAbsolute(guild.owner);
        }
        if (params.equals("guild_leader_formatted")) {
            return ModuleUtils.getFormatted(guild.owner);
        }
        if (params.equals("guild_leader_absolute_onlined")) {
            return ModuleUtils.getOffOnAbsolute(guild.owner);
        }
        if (params.equals("guild_leader_formatted_onlined")) {
            return ModuleUtils.getOffOnFormatted(guild.owner);
        }
        return null;
    }
}
