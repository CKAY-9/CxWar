package ca.ckay9;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Group {
    public String name;
    public ArrayList<UUID> members;
    public ArrayList<UUID> invites;
    public UUID creator;
    public static String[] possible_colors = {
            "&6", "&1", "&9", "&a", "&c", "&e", "&5", "&0", "&f"
    };
    public static Map<String, String> convert_color_to_minecraft = new HashMap<String, String>() {{
        put("gold", possible_colors[0]);
        put("darkblue", possible_colors[1]);
        put("blue", possible_colors[2]);
        put("green", possible_colors[3]);
        put("red", possible_colors[4]);
        put("yellow", possible_colors[5]);
        put("darkpurple", possible_colors[6]);
        put("black", possible_colors[7]);
        put("white", possible_colors[8]);
    }};
    public String color = "&6";

    public Group(UUID creator, String name, ArrayList<UUID> uuids, String color) {
        this.name = name;
        this.color = color;
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

            Storage.data.set(path + ".name", this.name);
            Storage.data.set(path + ".creator", creator.toString());
            Storage.data.set(path + ".color", color);

            ArrayList<String> string_uuids = new ArrayList<>();
            for (UUID uuid : this.members) {
                string_uuids.add(uuid.toString());
            }
            Storage.data.set(path + ".members", string_uuids.subList(0, string_uuids.size()));
            Storage.data.save(Storage.data_file);

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
            Storage.data.set("groups." + owner_uuid, null);
            Storage.data.save(Storage.data_file);
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
        String regex = Pattern.quote(Utils.formatText(this.color + "&l[")) + ".*?"
                + Pattern.quote(Utils.formatText("]&r "));
        String display_name = player.getDisplayName().replaceAll(regex, "").trim();
        String formatted = Utils.formatText(this.color + "&l[" + this.name + "]&r " + display_name);
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

    public void kickPlayer(Player target_player) {
        for (int i = 0; i < this.members.size(); i++) {
            UUID temp = this.members.get(i);
            if (temp.equals(target_player.getUniqueId())) {
                this.members.remove(i);
                break;
            }
        }

        this.saveGroup();
        target_player.sendMessage(Utils.formatText("&cYou have been kicked from &c&l" + this.name));
    }

    public static ArrayList<Group> loadAllGroups() {
        String base_path = "groups.";
        ArrayList<Group> groups = new ArrayList<>();

        if (Storage.data.getConfigurationSection(base_path) == null) {
            return groups;
        }

        for (String group_key : Storage.data.getConfigurationSection(base_path).getKeys(false)) {
            String group_path = base_path + group_key;
            String name = Storage.data.getString(group_path + ".name", "InvalidGroup");
            String color = Storage.data.getString(group_path + ".color", "&6");
            UUID creator = UUID.fromString(Storage.data.getString(group_path + ".creator"));
            ArrayList<String> member_strings = new ArrayList<String>(
                    Storage.data.getStringList(group_path + ".members"));
            ArrayList<UUID> members = new ArrayList<>();
            for (String member : member_strings) {
                UUID uuid = UUID.fromString(member);
                members.add(uuid);
            }

            groups.add(new Group(creator, name, members, color));
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
        String regex = "§[0-9a-fA-F]§l\\[.*?\\]" + Pattern.quote(Utils.formatText("§r "));
        String display_name = player.getDisplayName().replaceAll(regex, "").trim();

        player.setDisplayName(display_name);
        player.setCustomName(display_name);
        player.setPlayerListName(display_name);
    }
}
