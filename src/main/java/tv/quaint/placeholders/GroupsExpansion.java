package tv.quaint.placeholders;

import lombok.Getter;
import lombok.Setter;
import net.streamline.api.SLAPI;
import net.streamline.api.configs.given.MainMessagesHandler;
import net.streamline.api.interfaces.IStreamline;
import net.streamline.api.messages.ProxyMessageHelper;
import net.streamline.api.modules.ModuleUtils;
import net.streamline.api.objects.SingleSet;
import net.streamline.api.placeholder.RATExpansion;
import net.streamline.api.savables.users.StreamlineUser;
import tv.quaint.StreamlineGroups;
import tv.quaint.savable.GroupManager;
import tv.quaint.savable.SavableGroupRole;
import tv.quaint.savable.guilds.SavableGuild;
import tv.quaint.savable.parties.SavableParty;

import java.util.Date;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GroupsExpansion extends RATExpansion {
    public GroupsExpansion() {
        super("groups", "Quaint", "0.0.1");
    }

    @Override
    public String onLogic(String params) {
        if (params.equals("guild_default_level")) {
            return String.valueOf(StreamlineGroups.getConfigs().guildStartingLevel());
        }
        if (params.equals("guild_default_xp")) {
            return String.valueOf(StreamlineGroups.getConfigs().guildStartingExperienceAmount());
        }
        if (params.equals("loaded_groups")) {
            return String.valueOf(GroupManager.getLoadedGroups().size());
        }
        if (params.equals("loaded_guilds")) {
            return String.valueOf(GroupManager.getGroupsOf(SavableGuild.class).size());
        }
        if (params.equals("loaded_parties")) {
            return String.valueOf(GroupManager.getGroupsOf(SavableParty.class).size());
        }
        if (params.equals("loaded_users") || params.equals("loaded_grouped_users")) {
            return String.valueOf(GroupManager.getLoadedGroupedUsers().size());
        }
        return null;
    }

    @Override
    public String onRequest(StreamlineUser streamlineUser, String params) {
        if (SLAPI.getInstance().getPlatform().getServerType().equals(IStreamline.ServerType.PROXY)) {
            if (params.startsWith("guild_")) {
                SavableGuild guild = GroupManager.getGroupOfUser(SavableGuild.class, streamlineUser);
                if (guild == null) {
                    return StreamlineGroups.getMessages().placeholdersGuildNotFound();
                }
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
                        return ModuleUtils.getListAsFormattedString(role.getFlags());
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
            }
            if (params.startsWith("party_")) {
                SavableParty party = GroupManager.getGroupOfUser(SavableParty.class, streamlineUser);
                if (party == null) {
                    return StreamlineGroups.getMessages().placeholdersPartyNotFound();
                }
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
                        return ModuleUtils.getListAsFormattedString(role.getFlags());
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
            }
        } else {
            return ProxyMessageHelper.parseOnProxy("%" + getIdentifier() + "_" + params + "%", streamlineUser);
        }
        return null;
    }
}
