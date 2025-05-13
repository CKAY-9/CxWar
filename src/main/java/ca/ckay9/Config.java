package ca.ckay9;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
    public static File data_file;
    public static YamlConfiguration data;
    public static File leaderboardFile;
    public static YamlConfiguration leaderboard;

    public static void initializeData() {
        try {
            data_file = new File(Utils.getPlugin().getDataFolder(), "config.yml");
            if (!data_file.exists()) {
                if (data_file.getParentFile().mkdirs()) {
                    Utils.getPlugin().getLogger().info("Created data folder!");
                }

                if (data_file.createNewFile()) {
                    Utils.getPlugin().getLogger().info("Created config file!");
                }
            }

            data = YamlConfiguration.loadConfiguration(data_file);

            if (!data.isSet("reveal.cooldown")) {
                data.set("reveal.cooldown", 180);
            }

            data.save(data_file);
        } catch (IOException ex) {
            Utils.getPlugin().getLogger().warning(ex.toString());
        }
    }

}
