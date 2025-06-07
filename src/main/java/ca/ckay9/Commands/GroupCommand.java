package ca.ckay9.Commands;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ca.ckay9.CxWar;
import ca.ckay9.Group;
import ca.ckay9.Storage;
import ca.ckay9.Utils;

public class GroupCommand implements CommandExecutor {
    private CxWar cx_war;

    public GroupCommand(CxWar cx_war) {
        this.cx_war = cx_war;
    }

    private void createGroup(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(
                    Utils.formatText("&cInvalid group usage: /group create [name]"));
            return;
        }

        String name = Utils.combineStringArrayIntoSingle(args, 1);
        int limit = 15;
        if (name.length() > limit) {
            player.sendMessage(
                    Utils.formatText("&cGroup name must be at most " + limit + " characters long."));
            return;
        }

        if (Group.getGroupByName(name, this.cx_war.groups) != null) {
            player.sendMessage(
                    Utils.formatText("&cGroup name must be unique."));
            return;
        }

        ArrayList<UUID> uuids = new ArrayList<>();
        Random random = new Random();
        String random_color = Group.possible_colors[random.nextInt(Group.possible_colors.length)];
        uuids.add(player.getUniqueId());
        Group new_group = new Group(player.getUniqueId(), name, uuids, random_color);
        new_group.setupPlayerForGroup(player);
        this.cx_war.groups.add(new_group);
        player.sendMessage(Utils.formatText("&aCreated new group: " + name));
    }

    private void joinGroup(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(
                    Utils.formatText("&cInvalid group usage: /group join [name]"));
            return;
        }

        String target_group_name = Utils.combineStringArrayIntoSingle(args, 1);
        Group group = Group.getGroupByName(target_group_name, this.cx_war.groups);
        if (group == null) {
            player.sendMessage(
                    Utils.formatText("&cGroup doesn't exist."));
            return;
        }

        if (!group.isPlayerInvited(player.getUniqueId())) {
            player.sendMessage(
                    Utils.formatText("&cYou haven't been invited to this group."));
            return;
        }

        player.sendMessage(Utils.formatText("&aYou have joined " + group.name + "."));
        group.setupPlayerForGroup(player);
        group.acceptInvite(player.getUniqueId(), player.getName());
    }

    private void leaveGroup(Player player) {
        Group group = Group.getPlayerGroup(player.getUniqueId(), this.cx_war.groups);
        if (group == null) {
            player.sendMessage(
                    Utils.formatText("&cYou aren't in a group."));
            return;
        }

        if (player.getUniqueId().equals(group.creator)) {
            player.sendMessage(
                    Utils.formatText("&cYou can't leave your own group: /group delete"));
            return;
        }

        player.sendMessage(Utils.formatText("&aYou have left " + group.name + "."));
        group.leaveGroup(player.getUniqueId(), player.getName());
        Group.resetPlayerNames(player);
    }

    private void deleteGroup(Player player, Group group) {
        if (!player.getUniqueId().equals(group.creator)) {
            player.sendMessage(
                    Utils.formatText("&cOnly group owners can delete groups."));
            return;
        }

        group.deleteGroup(player.getUniqueId(), this.cx_war);
        Group.resetPlayerNames(player);
    }

    private void inviteToGroup(Player player, Group group, String[] args) {
        if (!player.getUniqueId().equals(group.creator)) {
            player.sendMessage(
                    Utils.formatText("&cOnly group owners can invite players."));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(
                    Utils.formatText("&cInvalid group usage: /group invite [name]"));
            return;
        }

        String target_player_name = args[1];
        Player target_player = Bukkit.getPlayerExact(target_player_name);
        if (target_player == null) {
            player.sendMessage(
                    Utils.formatText("&cInvalid target player."));
            return;
        }

        if (target_player.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(
                    Utils.formatText("&cYou can't invite yourself"));
            return;
        }

        target_player.sendMessage(Utils.formatText(
                "&aYou have been invited to " + group.name + ". Type /group join " + group.name + " to join"));
        player.sendMessage(Utils.formatText("&aInvited " + target_player_name + " to your group."));
        group.invitePlayerToGroup(target_player.getUniqueId());
    }

    private void changeGroupColor(Player player, Group group, String[] args) {
        if (!player.getUniqueId().equals(group.creator)) {
            player.sendMessage(
                    Utils.formatText("&cOnly group owners can change group color."));
            return;
        }
        
        if (args.length < 2) {
            player.sendMessage(
                    Utils.formatText("&cInvalid group usage: /group color [color]"));
            return;
        }

        String color = args[1].toLowerCase().strip();
        String mc_color = Group.convert_color_to_minecraft.get(color);
        if (mc_color == null) {
            player.sendMessage(
                    Utils.formatText("&cInvalid group usage: /group color [color]"));
            return;
        }

        for (UUID uuid : group.members) {
            Player member = Bukkit.getPlayer(uuid);
            if (member == null) {
                continue;
            }

            Group.resetPlayerNames(member);
        }

        group.color = mc_color;
        group.saveGroup();

        for (UUID uuid : group.members) {
            Player member = Bukkit.getPlayer(uuid);
            if (member == null) {
                continue;
            }

            group.setupPlayerForGroup(member);
        }

        player.sendMessage(
            Utils.formatText("&aUpdated group color to " + mc_color + "&l" + color));
    }

    private void kickPlayerFromGroup(Player player, Group group, String[] args) {
        if (!player.getUniqueId().equals(group.creator)) {
            player.sendMessage(
                    Utils.formatText("&cOnly group owners can change group color."));
            return;
        }
        
        if (args.length < 2) {
            player.sendMessage(
                    Utils.formatText("&cInvalid group usage: /group kick [name]"));
            return;
        }

        String target_name = args[1];
        Player target_player = Bukkit.getPlayerExact(target_name);
        if (target_player == null) {
            player.sendMessage(
                    Utils.formatText("&cPlayer must be online to be kicked"));
            return;
        }

        if (target_player.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(
                    Utils.formatText("&cYou can't kick yourself"));
            return;
        }
        
        if (!group.isPlayerInGroup(target_player.getUniqueId())) {
            player.sendMessage(
                    Utils.formatText("&cPlayer must in your group to be kicked"));
            return;
        }

        group.kickPlayer(target_player);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        if (!Storage.config.getBoolean("groups.enabled", true)) {
            sender.sendMessage(Utils.formatText("&c&lGroups &r&care disabled on this server"));
            return false;
        }

        Player player = (Player) sender;
        if (args.length <= 0) {
            player.sendMessage(
                    Utils.formatText("&cInvalid group usage: /group [create/join/leave, delete/invite/kick/color]"));
            return false;
        }

        Group current_group = Group.getPlayerGroup(player.getUniqueId(), this.cx_war.groups);

        String action = args[0];
        switch (action.strip().toLowerCase()) {
            case "color":
                if (current_group == null || !current_group.creator.equals(player.getUniqueId())) {
                    player.sendMessage(Utils.formatText("&cYou aren't the owner of a group."));
                    return false;
                }

                changeGroupColor(player, current_group, args);
                break;
            case "kick":
                if (current_group == null || !current_group.creator.equals(player.getUniqueId())) {
                    player.sendMessage(Utils.formatText("&cYou aren't the owner of a group."));
                    return false;
                }

                kickPlayerFromGroup(player, current_group, args);
                break;
            case "create":
                if (current_group != null) {
                    player.sendMessage(
                            Utils.formatText("&cYou need to leave your current group: /group [leave/delete]"));
                    return false;
                }

                createGroup(player, args);
                break;
            case "join":
                if (current_group != null) {
                    player.sendMessage(
                            Utils.formatText("&cYou need to leave your current group: /group [leave/delete]"));
                    return false;
                }

                joinGroup(player, args);
                break;
            case "leave":
                if (current_group == null) {
                    player.sendMessage(Utils.formatText("&cYou aren't in a group."));
                    return false;
                }

                leaveGroup(player);
                break;
            case "delete":
                if (current_group == null || !current_group.creator.equals(player.getUniqueId())) {
                    player.sendMessage(Utils.formatText("&cYou aren't the owner of a group."));
                    return false;
                }

                deleteGroup(player, current_group);
                break;
            case "invite":
                if (current_group == null || !current_group.creator.equals(player.getUniqueId())) {
                    player.sendMessage(Utils.formatText("&cYou aren't the owner of a group."));
                    return false;
                }

                inviteToGroup(player, current_group, args);
                break;
            default:
                break;
        }

        return false;
    }
}
