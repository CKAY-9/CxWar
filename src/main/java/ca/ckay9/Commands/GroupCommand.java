package ca.ckay9.Commands;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ca.ckay9.CxWar;
import ca.ckay9.Group;
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
        uuids.add(player.getUniqueId());
        Group new_group = new Group(player.getUniqueId(), name, uuids);
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

        target_player.sendMessage(Utils.formatText(
                "&aYou have been invited to " + group.name + ". Type /group join " + group.name + " to join"));
        player.sendMessage(Utils.formatText("&aInvited " + target_player_name + " to your group."));
        group.invitePlayerToGroup(target_player.getUniqueId());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        if (args.length <= 0) {
            player.sendMessage(
                    Utils.formatText("&cInvalid group usage: /group [create/join/leave/delete/invite] [name]"));
            return false;
        }

        Group current_group = Group.getPlayerGroup(player.getUniqueId(), this.cx_war.groups);

        String action = args[0];
        switch (action.strip().toLowerCase()) {
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
                if (current_group == null) {
                    player.sendMessage(Utils.formatText("&cYou aren't in a group."));
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
