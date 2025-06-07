package ca.ckay9;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

public class Storage {
    public static File config_file;
    public static YamlConfiguration config;
    public static File data_file;
    public static YamlConfiguration data;

    public static void initializeData() {
        try {
            config_file = new File(Utils.getPlugin().getDataFolder(), "config.yml");
            if (!config_file.exists()) {
                if (config_file.getParentFile().mkdirs()) {
                    Utils.getPlugin().getLogger().info("Created data folder!");
                }

                if (config_file.createNewFile()) {
                    Utils.getPlugin().getLogger().info("Created config file!");
                }
            }

            config = YamlConfiguration.loadConfiguration(config_file);

            data_file = new File(Utils.getPlugin().getDataFolder(), "data.yml");
            if (!data_file.exists()) {
                if (data_file.getParentFile().mkdirs()) {
                    Utils.getPlugin().getLogger().info("Created data folder!");
                }

                if (data_file.createNewFile()) {
                    Utils.getPlugin().getLogger().info("Created data file!");
                }
            }

            data = YamlConfiguration.loadConfiguration(data_file);

            if (!config.isSet("reveal.cooldown")) {
                config.set("reveal.cooldown", 180);
            }

            if (!config.isSet("reveal.enabled")) {
                config.set("reveal.enabled", true);
            }

            if (!config.isSet("hidden.enabled")) {
                config.set("hidden.enabled", true);
            }

            if (!config.isSet("hidden.timer")) {
                config.set("hidden.timer", 360);
            }

            if (!config.isSet("hidden.diamonds")) {
                config.set("hidden.diamonds", 8);
            }

            if (!config.isSet("hidden.netherite_ingots")) {
                config.set("hidden.netherite_ingots", 4);
            }

            if (!config.isSet("reveal.reveal_range")) {
                config.set("reveal.reveal_range", 200);
            }

            if (!config.isSet("reveal.random_chance")) {
                config.set("reveal.random_chance", 25);
            }

            if (!config.isSet("tp.player_radius")) {
                config.set("tp.player_radius", 10);
            }

            if (!config.isSet("tp.enabled")) {
                config.set("tp.enabled", true);
            }

            if (!config.isSet("death.timeout_enabled")) {
                config.set("death.timeout_enabled", true);
            }

            if (!config.isSet("death.timeout_duration")) {
                config.set("death.timeout_duration", 300);
            }

            if (!config.isSet("killstreaks.enabled")) {
                config.set("killstreaks.enabled", true);
            }

            if (!config.isSet("groups.enabled")) {
                config.set("groups.enabled", true);
            }

            if (!config.isSet("whisper.enabled")) {
                config.set("whisper.enabled", true);
            }

            if (!config.isSet("combat_logging.enabled")) {
                config.set("combat_logging.enabled", true);
            }

            if (!config.isSet("combat_logging.duration")) {
                config.set("combat_logging.duration", 30);
            }  
            
            if (!config.isSet("combat_logging.die_if_leave")) {
                config.set("combat_logging.die_if_leave", true);
            }        

            config.save(config_file);
            data.save(data_file);
        } catch (IOException ex) {
            Utils.getPlugin().getLogger().warning(ex.toString());
        }
    }

}
