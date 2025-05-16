package ca.ckay9;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Group {
    public String name;
    public ArrayList<UUID> members;
    public ArrayList<UUID> invites;
    public UUID creator;

    public Group(UUID creator, String name, ArrayList<UUID> uuids) {
        this.name = name;
        this.invites = new ArrayList<>();
        this.creator = creator;
        if (uuids.size() == 0 || uuids == null) {
            this.members = new ArrayList<>();
            this.members.add(creator);
        } else {
            this.members = uuids;
        }

        this.saveGroup();
    }

    public boolean saveGroup() {
        try {
            String path = "groups." + creator.toString();

            Config.data.set(path + ".name", this.name);
            Config.data.set(path + ".creator", creator.toString());

            ArrayList<String> string_uuids = new ArrayList<>();
            for (UUID uuid : this.members) {
                string_uuids.add(uuid.toString());
            }
            Config.data.set(path + ".members", string_uuids.subList(0, string_uuids.size()));
            Config.data.save(Config.data_file);

            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    public void invitePlayerToGroup(UUID invitee_uuid) {
        this.invites.add(invitee_uuid);
    }

    public void acceptInvite(UUID invitee_uuid, String invitee_name) {
        for (UUID member_uuid : this.members) {
            Player player = Bukkit.getPlayer(member_uuid);
            if (player == null) {
                continue;
            }

            player.sendMessage(Utils.formatText("&a" + invitee_name + " has joined the group."));
        }

        this.members.add(invitee_uuid);
        this.invites.remove(invitee_uuid);
        this.saveGroup();
    }

    public void leaveGroup(UUID leaver_uuid, String leaver_name) {
        for (UUID member_uuid : this.members) {
            if (member_uuid == leaver_uuid) {
                continue;
            }

            Player player = Bukkit.getPlayer(member_uuid);
            if (player == null) {
                continue;
            }

            player.sendMessage(Utils.formatText("&a" + leaver_name + " has left the group."));
        }

        this.members.remove(leaver_uuid);
        this.saveGroup();
    }

    public void deleteGroup(UUID owner_uuid, CxWar cx_war) {
        for (UUID member_uuid : this.members) {
            Player player = Bukkit.getPlayer(member_uuid);
            if (player == null) {
                continue;
            }

            player.sendMessage(Utils.formatText("&a" + this.name + " has been deleted."));
            Group.resetPlayerNames(player);
        }

        this.members.clear();
        this.invites.clear();
        cx_war.groups.remove(this);

        try {
            Config.data.set("groups." + owner_uuid, null);
            Config.data.save(Config.data_file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public boolean isPlayerInvited(UUID player_uuid) {
        for (UUID uuid : this.invites) {
            if (uuid == player_uuid) {
                return true;
            }
        }

        return false;
    }

    public boolean isPlayerInGroup(UUID player_uuid) {
        for (UUID uuid : this.members) {
            if (uuid.equals(player_uuid)) {
                return true;
            }
        }

        return false;
    }

    public void setupPlayerForGroup(Player player) {
        String formatted = Utils.formatText("&6&l[" + this.name + "]&r " + player.getName());
        player.setDisplayName(formatted);
        player.setCustomName(formatted);
        player.setPlayerListName(formatted);

        for (UUID member_uuid : this.members) {
            Player temp = Bukkit.getPlayer(member_uuid);
            if (temp == null || player.getUniqueId().equals(member_uuid)) {
                continue;
            }

            temp.sendMessage(Utils.formatText("&a" + player.getName() + " is now online."));
        }
    }

    public static ArrayList<Group> loadAllGroups() {
        String base_path = "groups.";
        ArrayList<Group> groups = new ArrayList<>();

        if (Config.data.getConfigurationSection(base_path) == null) {
            return groups;
        }

        for (String group_key : Config.data.getConfigurationSection(base_path).getKeys(false)) {
            String group_path = base_path + group_key;
            String name = Config.data.getString(group_path + ".name");
            UUID creator = UUID.fromString(Config.data.getString(group_path + ".creator"));
            ArrayList<String> member_strings = new ArrayList<String>(
                    Config.data.getStringList(group_path + ".members"));
            ArrayList<UUID> members = new ArrayList<>();
            for (String member : member_strings) {
                UUID uuid = UUID.fromString(member);
                members.add(uuid);
            }

            groups.add(new Group(creator, name, members));
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            Group g = Group.getPlayerGroup(player.getUniqueId(), groups);
            if (g == null) {
                Group.resetPlayerNames(player);
            } else {
                g.setupPlayerForGroup(player);
            }
        }

        return groups;
    }

    public static Group getPlayerGroup(UUID player_uuid, ArrayList<Group> groups) {
        for (Group group : groups) {
            Utils.getPlugin().getLogger().info(group.name);
            if (group.isPlayerInGroup(player_uuid)) {
                return group;
            }
        }

        return null;
    }

    public static Group getGroupByName(String group_name, ArrayList<Group> groups) {
        for (Group group : groups) {
            if (group.name.equals(group_name)) {
                return group;
            }
        }

        return null;
    }

    public static ArrayList<String> getGroupsInvitedTo(UUID player_uuid, ArrayList<Group> groups) {
        ArrayList<String> names = new ArrayList<>();
        for (Group group : groups) {
            if (group.isPlayerInvited(player_uuid)) {
                names.add(group.name);
            }
        }

        return names;
    }

    public static void resetPlayerNames(Player player) {
        String name = player.getName();

        player.setDisplayName(name);
        player.setCustomName(name);
        player.setPlayerListName(name);
    }
}
