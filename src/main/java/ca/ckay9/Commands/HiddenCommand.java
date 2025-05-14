package ca.ckay9.Commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ca.ckay9.Config;
import ca.ckay9.CxWar;
import ca.ckay9.Utils;

public class HiddenCommand implements CommandExecutor {
    private CxWar cx_war;
    private HashMap<UUID, HiddenPlayer> hidden_players;

    public HiddenCommand(CxWar cx_war) {
        this.cx_war = cx_war;
        this.hidden_players = new HashMap<>();

        for (Player p : Bukkit.getOnlinePlayers()) {
            this.hidden_players.put(p.getUniqueId(), new HiddenPlayer(0, Config.data.getInt("hidden.timer", 360)));
        }

        cx_war.getServer().getScheduler().scheduleSyncRepeatingTask(cx_war, new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    HiddenPlayer hidden_player = hidden_players.get(player.getUniqueId());
                    if (hidden_player == null) {
                        hidden_players.put(player.getUniqueId(),
                                new HiddenPlayer(0, Config.data.getInt("hidden.timer", 360)));
                        continue;
                    }

                    if (hidden_player.type == 0) {
                        continue;
                    }

                    hidden_player.timer -= 1;
                    if (hidden_player.timer > 0) {
                        continue;
                    }

                    switch (hidden_player.type) {
                        case 1:
                            if (!hasEnoughItemsForHidden(Material.DIAMOND, Config.data.getInt("hidden.diamonds"),
                                    player)) {
                                player.sendMessage(Utils.formatText("&cYou need "
                                        + Config.data.getInt("hidden.diamonds") + " diamonds to be hidden."));
                                endHidden(player);
                                return;
                            }

                            player.sendMessage(Utils.formatText("&aYou're hidden has renewed for "
                                    + Config.data.getInt("hidden.diamonds") + " diamonds."));
                            break;
                        case 2:
                            if (!hasEnoughItemsForHidden(Material.NETHERITE_INGOT,
                                    Config.data.getInt("hidden.netherite_ingots"),
                                    player)) {
                                player.sendMessage(
                                        Utils.formatText("&cYou need " + Config.data.getInt("hidden.netherite_ingots")
                                                + " netherite ingots to be hidden."));
                                endHidden(player);
                                return;
                            }

                            hidden_player.timer = Config.data.getInt("hidden.timer", 360);
                            player.sendMessage(Utils.formatText("&aYou're hidden has renewed for "
                                    + Config.data.getInt("hidden.netherite_ingots") + " diamonds."));
                            break;
                        default:
                            break;
                    }

                }
            }
        }, 0, 20L);
    }

    private boolean hasEnoughItemsForHidden(Material material, int required_count, Player player) {
        ArrayList<Integer> foundIndexes = new ArrayList<>();
        int currentStackAmount = 0;
        int itemNum = required_count;
        int i = 0;

        for (ItemStack it : player.getInventory()) {
            if (it != null && it.getType() == material) {
                foundIndexes.add(i);
                currentStackAmount += it.getAmount();
                if (currentStackAmount >= itemNum)
                    break;
            }
            i++;
        }

        if (currentStackAmount >= itemNum) {
            for (int j = 0; j < foundIndexes.size(); j++) {
                int index = foundIndexes.get(j);
                itemNum -= player.getInventory().getItem(index).getAmount();
                if (itemNum <= 0) {
                    player.getInventory().getItem(index).setAmount(Math.abs(itemNum));
                } else {
                    player.getInventory().getItem(index).setAmount(0);
                }
            }
            return true;
        }

        return false;
    }

    private void startHidden(String[] args, Player player) {
        HiddenPlayer hidden_player = this.hidden_players.get(player.getUniqueId());
        if (hidden_player == null) {
            hidden_player = new HiddenPlayer(0, Config.data.getInt("hidden.timer", 360));
            this.hidden_players.put(player.getUniqueId(), hidden_player);
        }

        if (hidden_player.type != 0) {
            player.sendMessage(
                    Utils.formatText("&cYou are already hidden. Type \"/hidden end\" to become visible."));
            return;
        }

        if (args.length <= 1) {
            player.sendMessage(
                    Utils.formatText("&cInvalid hidden usage: /hidden [start] [diamond/netherite]"));
            return;
        }

        String hidden_type = args[1];
        switch (hidden_type.strip().toLowerCase()) {
            case "diamond":
                if (!hasEnoughItemsForHidden(Material.DIAMOND, Config.data.getInt("hidden.diamonds"), player)) {
                    player.sendMessage(Utils.formatText(
                            "&cYou need " + Config.data.getInt("hidden.diamonds") + " diamonds to be hidden."));
                    return;
                }
                player.sendMessage(
                        Utils.formatText("&aYou are now hidden with diamond. All reveals will be a random location."));
                hidden_player.type = 1;
                break;
            case "netherite":
                if (!hasEnoughItemsForHidden(Material.NETHERITE_INGOT, Config.data.getInt("hidden.netherite_ingots"),
                        player)) {
                    player.sendMessage(Utils.formatText("&cYou need " + Config.data.getInt("hidden.netherite_ingots")
                            + " netherite ingots to be hidden."));
                    return;
                }
                player.sendMessage(Utils
                        .formatText("&aYou are now hidden with netherite. All reveals will deflect to the leaker."));
                hidden_player.type = 2;
                break;
            default:
                Utils.formatText("&cInvalid hidden usage: /hidden [start] [diamond/netherite]");
                break;
        }
    }

    private void endHidden(Player player) {
        HiddenPlayer hidden_player = this.hidden_players.get(player.getUniqueId());
        if (hidden_player == null) {
            hidden_player = new HiddenPlayer(0, Config.data.getInt("hidden.timer", 360));
            this.hidden_players.put(player.getUniqueId(), hidden_player);
        }

        if (hidden_player.type == 0) {
            player.sendMessage(Utils.formatText("&cYou aren't actively hidden!"));
            return;
        }

        hidden_player.type = 0;
        hidden_player.timer = Config.data.getInt("hidden.timer", 360);
        player.sendMessage(Utils.formatText("&aYou're hidden has ended."));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        if (args.length <= 0) {
            player.sendMessage(
                    Utils.formatText("&cInvalid hidden usage: /hidden [start/end] [start: diamond/netherite]"));
            return false;
        }

        String action = args[0];
        switch (action.strip().toLowerCase()) {
            case "start":
                startHidden(args, player);
                break;
            case "end":
                endHidden(player);
                break;
        }

        return false;
    }

    public HashMap<UUID, HiddenPlayer> getHiddenPlayers() {
        return this.hidden_players;
    }

    public void addToHiddenPlayers(UUID uuid, HiddenPlayer hp) {
        this.hidden_players.put(uuid, hp);
    }
}
